package it.polito.ai.controllers;

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
    private StoreService purchaseService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path="/admin/users", produces="application/json")
    public List<Account> getUsers() {
        return accountService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path="/admin/users/{userId}/positions", produces="application/json")
    public List<Archive> getPositions(
            @PathVariable String userId,
            @RequestParam Optional<Long> from,
            @RequestParam Optional<Long> to
    ) {
        return archiveService.getArchives(userId);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path="/admin/users/{userId}/purchases", produces="application/json")
    public List<Purchase> getPurchases(
            @PathVariable String userId
    ) {
        return purchaseService.getPurchases(userId);
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
