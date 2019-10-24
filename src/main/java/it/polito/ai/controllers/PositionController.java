package it.polito.ai.controllers;

import it.polito.ai.exceptions.InvalidPositionException;
import it.polito.ai.models.Account;
import it.polito.ai.models.PositionEntry;
import it.polito.ai.models.RestErrorResponse;
import it.polito.ai.services.AccountService;
import it.polito.ai.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class PositionController {

    @Autowired
    private PositionService positionService;

    @Autowired
    private AccountService accountService;

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping(path="/api/positions", produces="application/json")
    public List<PositionEntry> getPositions(
            @RequestParam Optional<Long> from,
            @RequestParam Optional<Long> to,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());
        String userId = account.getId();

        return positionService.getPositions(userId, from, to);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping(path="/api/positions", produces="application/json")
    public ResponseEntity<?> savePositions(
            @RequestBody List<PositionEntry> positions,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());
        String userId = account.getId();

        try {
            positionService.savePositions(userId, positions);
        } catch (InvalidPositionException e) {
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Object>(HttpStatus.CREATED);
    }

}
