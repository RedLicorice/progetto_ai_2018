package it.polito.ai.controllers;

import it.polito.ai.exceptions.*;
import it.polito.ai.models.*;
import it.polito.ai.models.archive.ArchiveSearchRequest;
import it.polito.ai.models.store.Invoice;
import it.polito.ai.services.AccountService;
import it.polito.ai.services.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private AccountService accountService;

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @GetMapping(path="/store/invoices", produces="application/json")
    public ResponseEntity<?> showInvoices(
            Authentication authentication
    ) {
        List<Invoice> invoices = storeService.getInvoices(authentication.getName());
        return new ResponseEntity<>(invoices, HttpStatus.OK);
    }

   /*
    * Each invoice shall be paid individually,
    * the client sends a request for each invoice.
    * Once the invoice is paid, the archive is accessible through the Archives API
    * */
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping(path="/store/invoices/{invoiceId}", produces="application/json")
    public ResponseEntity<?> payInvoice(
            @PathVariable String invoiceId,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());

        try {
            Invoice invoice = storeService.payInvoice(account.getUsername(), invoiceId);
            return new ResponseEntity<Invoice>(invoice, HttpStatus.OK);
        }
        catch(InvoiceNotFoundException | UserNotFoundException | NotEnoughFundsException | ArchiveNotFoundException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @GetMapping(path="/store/invoices/{invoiceId}", produces="application/json")
    public ResponseEntity<?> getInvoice(
            @PathVariable String invoiceId,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());

        try {
            Invoice invoice = storeService.getInvoice(account.getUsername(), invoiceId);
            return new ResponseEntity<Invoice>(invoice, HttpStatus.OK);
        }
        catch(InvoiceNotFoundException  e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @DeleteMapping(path="/store/invoices/{invoiceId}", produces="application/json")
    public ResponseEntity<?> deleteInvoice(
            @PathVariable String invoiceId,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());

        try {
            Boolean result = storeService.cancelInvoice(account.getUsername(), invoiceId);
            return new ResponseEntity<>( result ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
        }
        catch(InvoiceNotFoundException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.OK);
        }
    }
}
