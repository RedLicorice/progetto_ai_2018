package it.polito.ai.controllers;

import it.polito.ai.models.Account;
import it.polito.ai.models.RestErrorResponse;
import it.polito.ai.models.RestGenericResponse;
import it.polito.ai.services.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping(path = "/me", produces = "application/json" )
    public Account me() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountService.findAccountByUsername(username);
    }

    @PostMapping(path = "/register", produces = "application/json")
    public ResponseEntity<?> register(@RequestBody Account account) {
        try {
            account.grantAuthority("ROLE_USER");
            account.grantAuthority("ROLE_CUSTOMER");
            return new ResponseEntity<Object>(accountService.register(account), HttpStatus.OK);
        } catch (AccountException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()),HttpStatus.BAD_REQUEST );
        }
    }

    @PostMapping(path = "/register-admin", produces = "application/json")
    public ResponseEntity<?> registerAdmin(@RequestBody Account account) {
        try {
            if(accountService.existsAccountByRole("ADMIN")){
                return new ResponseEntity<Object>(new RestErrorResponse("AN_ADMIN_ALREADY_EXISTS"),HttpStatus.BAD_REQUEST );
            }
            account.grantAuthority("ROLE_ADMIN");
            account.grantAuthority("ROLE_USER");
            account.grantAuthority("ROLE_CUSTOMER");
            return new ResponseEntity<Object>(accountService.register(account), HttpStatus.OK);
        } catch (AccountException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()),HttpStatus.BAD_REQUEST );
        }
    }
}
