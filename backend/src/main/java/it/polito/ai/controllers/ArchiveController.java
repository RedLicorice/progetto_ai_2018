package it.polito.ai.controllers;

import it.polito.ai.exceptions.*;
import it.polito.ai.models.*;
import it.polito.ai.services.AccountService;
import it.polito.ai.services.ArchiveService;
import it.polito.ai.services.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ArchiveController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ArchiveService archiveService;

    @Autowired
    private StoreService storeService;

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping(path="/archives", produces="application/json")
    public ResponseEntity<?> index(Authentication authentication
    ) {

        try{
            List<Archive> archives = archiveService.getArchivesByUsername(authentication.getName());
            return new ResponseEntity<List<Archive>>(archives, HttpStatus.OK);
        }
        catch(MeasuresNotFoundException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
        catch(UserHasNoArchivesException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping(path="/archives", produces="application/json")
    public ResponseEntity<?> store(
            @RequestBody List<MeasureSubmission> positionEntries,
            Authentication authentication
    ) {
        try {
            Archive a = archiveService.createArchive(authentication.getName(), positionEntries);
            return new ResponseEntity<Object>(a, HttpStatus.CREATED);
        } catch (InvalidPositionException e) {
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping(path="archives/{archiveId}", produces="application/json")
    public ResponseEntity<?> show(
            @PathVariable String archiveId,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());
        // If user has purchased the archive
        if (storeService.hasPurchasedItem(account.getUsername(), archiveId)){
            try {
                Archive a = archiveService.getArchive(archiveId);
                ArchiveDownload ad = archiveService.downloadArchive(a);
                return new ResponseEntity<ArchiveDownload>(ad, HttpStatus.OK);
            } catch(ArchiveNotFoundException | MeasuresNotFoundException e){
                return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
            }
        }

        //If the user is the owner of the archive.
        try {
            Archive a = archiveService.getUserArchive(account.getUsername(), archiveId);
            ArchiveDownload ad = archiveService.downloadArchive(a);
            return new ResponseEntity<ArchiveDownload>(ad, HttpStatus.OK);
        }
        catch(MeasuresNotFoundException | ArchiveNotFoundException e) {
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('USER')")
    @RequestMapping(path="archives/{archiveId}", produces="application/json", method=RequestMethod.DELETE)
    public ResponseEntity<?> delete(
            @PathVariable String archiveId,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());
        String userId = account.getId();
        try {
            archiveService.deleteArchive(userId, archiveId);
        }
        catch(ArchiveNotFoundException | MeasuresNotFoundException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}
