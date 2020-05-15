package it.polito.ai.controllers;

import it.polito.ai.exceptions.ArchiveNotFoundException;
import it.polito.ai.exceptions.InvalidPositionException;
import it.polito.ai.models.*;
import it.polito.ai.services.AccountService;
import it.polito.ai.services.ArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ArchiveController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ArchiveService archiveService;

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping(path="/archives", produces="application/json")
    public ResponseEntity<?> index(Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());
        String userId = account.getId();

        List<Archive> archives = archiveService.getArchives(userId);
        return new ResponseEntity<List<Archive>>(archives, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping(path="/archives", produces="application/json")
    public ResponseEntity<?> store(
            @RequestBody List<PositionEntry> positionEntries,
            Authentication authentication
    ) {
        /*
        *
        * */
        Account account = accountService.findAccountByUsername(authentication.getName());
        String userId = account.getId();

        try {
            Archive a = archiveService.createArchive(userId, positionEntries);
            return new ResponseEntity<Object>(a, HttpStatus.CREATED);
        } catch (InvalidPositionException e) {
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping(path="archive/{archiveId}", produces="application/json")
    public ResponseEntity<?> show(
            @PathVariable String archiveId,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());
        String userId = account.getId();

        try {
            Archive a = archiveService.getArchive(userId, archiveId);
            return new ResponseEntity<Archive>(a, HttpStatus.OK);
        }
        catch(ArchiveNotFoundException e) {
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('USER')")
    @RequestMapping(path="archive/{archiveId}", produces="application/json", method=RequestMethod.DELETE)
    public ResponseEntity<?> delete(
            @PathVariable String archiveId,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());
        String userId = account.getId();
        try {
            archiveService.deleteArchive(userId, archiveId);
        }
        catch(ArchiveNotFoundException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}
