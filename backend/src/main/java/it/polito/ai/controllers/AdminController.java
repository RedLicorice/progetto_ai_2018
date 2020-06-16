package it.polito.ai.controllers;

import it.polito.ai.exceptions.MeasuresNotFoundException;
import it.polito.ai.exceptions.UserHasNoArchivesException;
import it.polito.ai.exceptions.UserNotFoundException;
import it.polito.ai.models.*;
import it.polito.ai.services.AccountService;
import it.polito.ai.services.ArchiveService;
import it.polito.ai.services.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Optional;

@RestController
public class AdminController {

    @Autowired
    private ArchiveService archiveService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private StoreService storeService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path="/admin/users", produces="application/json")
    public ResponseEntity<?> getUsers() {
        List<Account> users = accountService.findAll();
        return new ResponseEntity<List<Account>>(users, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path="/admin/users/{username}/archives", produces="application/json")
    public ResponseEntity<?> getPositions(
            @PathVariable String username
    ) {
        try {
            List<Archive> result = archiveService.getArchivesByUsername(username);
            return new ResponseEntity<List<Archive>>(result, HttpStatus.OK);
        }
        catch(MeasuresNotFoundException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
        catch(UserHasNoArchivesException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path="/admin/users/{username}/invoices", produces="application/json")
    public List<Invoice> getInvoices(
            @PathVariable String username
    ) {
        return storeService.getInvoices(username);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path="/admin/users/grant/{role}/{userId}", produces="application/json")
    public ResponseEntity<?> grantRole(
            @PathVariable String userId,
            @PathVariable String role
    ) {
        try {
            Account acc = accountService.findAccountById(userId);
            acc.grantAuthority("ROLE_"+role);
            return new ResponseEntity<Object>(accountService.update(acc), HttpStatus.OK);
        }
        catch(AccountException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND );
        }
        catch(UsernameNotFoundException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND );
        }
    }
}
