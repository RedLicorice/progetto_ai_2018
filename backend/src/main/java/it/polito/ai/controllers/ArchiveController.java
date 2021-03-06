package it.polito.ai.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import it.polito.ai.exceptions.*;
import it.polito.ai.models.*;
import it.polito.ai.models.archive.*;
import it.polito.ai.models.store.Invoice;
import it.polito.ai.services.ArchiveService;
import it.polito.ai.services.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ArchiveController {

    @Autowired
    private ArchiveService archiveService;

    @Autowired
    private StoreService storeService;

    /*
     *   Return all archives "public" summary
     */
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping(path="/archives", produces="application/json")
    @JsonView(ArchiveView.Summary.class)
    public ResponseEntity<?> purchasedArchives(Authentication authentication)
    {
        List<Archive> archives = archiveService.findPurchasedArchives(authentication.getName());
        // Also include user-owned archives in search results
        // I prefer keeping things separed so this is out.
        //archives.addAll(archiveService.findUserArchives(authentication.getName()));
        return new ResponseEntity<List<Archive>>(archives, HttpStatus.OK);
    }

    /*
     *   Return uploaded archives "owner" summary which includes number of purchases
     */
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping(path="/archives/upload", produces="application/json")
    @JsonView(ArchiveView.OwnerSummary.class)
    public ResponseEntity<?> userArchives(Authentication authentication)
    {
        List<Archive> archives = archiveService.findUserArchives(authentication.getName());
        return new ResponseEntity<List<Archive>>(archives, HttpStatus.OK);
    }

    /*
     *   Upload a new archive and return it as resource
     */
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping(path="/archives/upload", produces="application/json")
    @JsonView(ArchiveView.OwnerSummary.class)
    public ResponseEntity<?> uploadArchive(
            @RequestBody ArrayList<Measure> measures,
            Authentication authentication
    ) {
        try {
            Archive a = archiveService.createArchive(authentication.getName(), measures);
            return new ResponseEntity<Object>(a, HttpStatus.CREATED);
        } catch (InvalidPositionException e) {
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /*
     *   Download an uploaded or purchased archive as resource
     */
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping(path="/archives/download/{archiveId}", produces="application/json")
    @JsonView(ArchiveView.Resource.class)
    public ResponseEntity<?> downloadArchive(
            @PathVariable String archiveId,
            Authentication authentication
    ) {
        // If user has purchased the archive
        if (storeService.hasPurchasedItem(authentication.getName(), archiveId)){
            try {
                Archive a = archiveService.getArchive(archiveId);
                return new ResponseEntity<Archive>(a, HttpStatus.OK);
            } catch(ArchiveNotFoundException  e){
                return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
            }
        }

        //If the user is the owner of the archive.
        try {
            Archive a = archiveService.getUserArchive(authentication.getName(), archiveId);
            System.out.println(a.toString());
            return new ResponseEntity<Archive>(a, HttpStatus.OK);
        }
        catch(ArchiveNotFoundException e) {
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping(path="/archives/public/{archiveId}", produces="application/json")
    @JsonView(ArchiveView.PublicResource.class)
    public ResponseEntity<?> showPublicArchive(
            @PathVariable String archiveId,
            Authentication authentication
    ) {
        try {
            Archive a = archiveService.getArchive(archiveId);
            return new ResponseEntity<Archive>(a, HttpStatus.OK);
        } catch(ArchiveNotFoundException  e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    /*
     *   Toggle archive deleted flag and return "owner" summary
     */
    @PreAuthorize("hasAnyRole('USER')")
    @RequestMapping(path="/archives/{archiveId}", produces="application/json", method=RequestMethod.DELETE)
    @JsonView(ArchiveView.OwnerSummary.class)
    public ResponseEntity<?> delete(
            @PathVariable String archiveId,
            Authentication authentication
    ) {
        try {
            Archive a = archiveService.toggleDeleteArchive(authentication.getName(), archiveId);
            return new ResponseEntity<Archive>(a, HttpStatus.OK);
        }
        catch(ArchiveNotFoundException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    /*
     *   Search archives in a square/rectangular area defined by two (lng, lat) tuples
     *   Server-side filtering is supported to reduce traffic, but since our aim is
     *   to SELL archives, it would be wise to do the filtering on the client-side
     *   so constraints can be relapsed if there are no archives satisfying them in the area.
     */
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping(path="/archives/search", produces="application/json")
    @JsonView(ArchiveView.PublicResource.class)
    public ResponseEntity<?> searchAvailableArchives(
            @RequestBody ArchiveSearchRequest req,
            Authentication authentication
    ) {
        try {
           List<Archive> archives = archiveService.findPurchasableArchives(
                   authentication.getName(),
                   req.getRect(),
                   req.getFrom(),
                   req.getTo(),
                   req.getUsers()

           );
           return new ResponseEntity<List<Archive>>(archives, HttpStatus.OK);
        }
        catch(ArchiveNotFoundException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /*
     *   Generate invoice for archives:
     *   - Not belonging to current user
     *   - Not deleted
     *   - Within a specified polygon
     *   - Whose positions contain timestamps between from and to
     *   - Uploaded by users in specified list
     */
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping(path="/archives/buy", produces="application/json")
    public ResponseEntity<?> buyAvailableArchives(
            @RequestBody List<String> archiveIds,
            Authentication authentication
    ) {
        List<Archive> archives = archiveService.getArchives(archiveIds);
        List<String> archive_ids = archives.stream().map(Archive::getId).collect(Collectors.toList());
        Invoice invoice =  storeService.createInvoice(authentication.getName(), archive_ids);
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }
}
