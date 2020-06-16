package it.polito.ai.controllers;

import it.polito.ai.exceptions.*;
import it.polito.ai.models.*;
import it.polito.ai.services.AccountService;
import it.polito.ai.services.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
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
        Account account = accountService.findAccountByUsername(authentication.getName());

        List<Invoice> invoices = storeService.getInvoices(account.getUsername());
        return new ResponseEntity<List<Invoice>>(invoices, HttpStatus.OK);
    }

    /*
    * Client sends list of selected users, time interval and polygon
    * Server creates an invoice for each of the archives intersecting
    * the received parameters.
    * Invoices are initially in an "unpaid" state.
    * */
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping(path="/store/invoices", produces="application/json")
    public ResponseEntity<?> createInvoices(
            @RequestBody List<String> users,
            @RequestParam Long from,
            @RequestParam Long to,
            @RequestParam GeoJsonPolygon polygon,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());

        try {
            List<Invoice> invoices = storeService.createInvoices(account.getUsername(), users, from, to, polygon);
            return new ResponseEntity<List<Invoice>>(invoices, HttpStatus.OK);
        }
        catch(NoResultsException e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.OK);
        }
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
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.OK);
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
    @PostMapping(path="/store/search/points", produces="application/json")
    public ResponseEntity<?> searchPoints(
            @RequestParam GeoJsonPolygon rect,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());

        try {
            List<GeoJsonPoint> points = storeService.searchPointsInRect(rect);
            return new ResponseEntity<>(points, HttpStatus.OK);
        }
        catch(NoResultsException  e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping(path="/store/search/users", produces="application/json")
    public ResponseEntity<?> searchUsers(
            @RequestParam GeoJsonPolygon rect,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());

        try {
            List<String> points = storeService.searchUsernamesInRect(rect);
            return new ResponseEntity<>(points, HttpStatus.OK);
        }
        catch(NoResultsException  e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping(path="/store/search/timestamps", produces="application/json")
    public ResponseEntity<?> searchTimestamps(
            @RequestParam GeoJsonPolygon rect,
            Authentication authentication
    ) {
        Account account = accountService.findAccountByUsername(authentication.getName());

        try {
            List<Long> timestamps = storeService.searchTimestampsInRect(rect);
            return new ResponseEntity<>(timestamps, HttpStatus.OK);
        }
        catch(NoResultsException  e){
            return new ResponseEntity<Object>(new RestErrorResponse(e.getMessage()), HttpStatus.OK);
        }
    }

}
