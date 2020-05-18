package it.polito.ai.controllers;

import it.polito.ai.models.Account;
import it.polito.ai.models.MeasureSubmission;
import it.polito.ai.models.Purchase;
import it.polito.ai.models.Invoice;
import it.polito.ai.services.AccountService;
import it.polito.ai.services.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PurchaseController {

    @Autowired
    private StoreService purchaseService;

    @Autowired
    private AccountService accountService;

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping(path="/api/purchases/request", produces="application/json")
    public Invoice requestPurchase(
            @RequestBody List<MeasureSubmission> positions,
            @RequestParam Long from,
            @RequestParam Long to
    ) {
        return purchaseService.requestPurchase(positions, from, to);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping(path="/api/purchases/confirm", produces="application/json")
    public Purchase requestConfirm(
            @RequestBody List<MeasureSubmission> positions,
            @RequestParam Long from,
            @RequestParam Long to,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());
        String userId = account.getId();

        return purchaseService.confirmPurchase(userId, positions, from, to);
    }

}
