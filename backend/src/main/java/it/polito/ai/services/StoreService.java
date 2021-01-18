package it.polito.ai.services;

import it.polito.ai.exceptions.*;
import it.polito.ai.models.*;
import it.polito.ai.models.archive.Archive;
import it.polito.ai.models.store.Invoice;
import it.polito.ai.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreService {

    @Autowired
    private InvoiceRepo invoiceRepo;

    @Autowired
    protected AccountRepo accountRepo;

    @Autowired
    private ArchiveRepo archiveRepo;

    public Invoice createInvoice(
            String username, Integer amount, String itemId
    )
    {
        Invoice invoice = new Invoice();
        invoice.setUsername(username);
        invoice.setAmount(amount);
        invoice.setItemId(itemId);
        invoice.setPaid(false);
        invoiceRepo.save(invoice);
        return invoice;
    }

    @Transactional
    public Invoice payInvoice(
            String username,
            String invoiceId
    ) throws InvoiceNotFoundException, UserNotFoundException, NotEnoughFundsException, ArchiveNotFoundException
    {
        Invoice invoice = invoiceRepo.findByIdAndUsername(invoiceId, username);
        if(invoice == null){
            throw new InvoiceNotFoundException("Invoice " + invoiceId + " not found for user " + username);
        }
        Account buyer = accountRepo.findByUsername(username);
        if(buyer == null)
            throw new UserNotFoundException(username);
        if(buyer.getWallet() < invoice.getAmount())
            throw new NotEnoughFundsException(username, invoice.getAmount());
        buyer.setWallet(buyer.getWallet() - invoice.getAmount());
        accountRepo.save(buyer);

        Archive item = archiveRepo.findById(invoice.getItemId());
        if(item == null)
            throw new ArchiveNotFoundException(invoice.getItemId());

        Account seller = accountRepo.findByUsername(item.getUsername());
        if(seller == null)
            throw new UserNotFoundException(item.getUsername());
        seller.addWallet(invoice.getAmount());
        item.addPurchases(1);

        archiveRepo.save(item);
        accountRepo.save(seller);

        invoice.setPaid(true);
        invoiceRepo.save(invoice);
        return invoice;
    }

    @Transactional
    public Boolean cancelInvoice(
            String username,
            String invoiceId
    ) throws InvoiceNotFoundException
    {
        Invoice invoice = invoiceRepo.findByIdAndUsername(invoiceId, username);
        if(invoice == null){
            throw new InvoiceNotFoundException("Invoice " + invoiceId + " not found for user " + username);
        }
        invoiceRepo.deleteById(invoiceId);
        return true;
    }

    @Transactional
    public List<Invoice> getInvoices(
            String username
    )
    {
        //ToDo: Delete unpaid invoices older than 24 hours to avoid cluttering the database
        return invoiceRepo.findByUsername(username);
    }

    @Transactional
    public Invoice getInvoice(String username, String invoiceId) throws InvoiceNotFoundException
    {
        Invoice invoice = invoiceRepo.findByIdAndUsername(invoiceId, username);
        //ToDo: Delete unpaid invoices older than 24 hours to avoid cluttering the database
        if(invoice == null)
            throw new InvoiceNotFoundException("Invoice " + invoiceId + " not found for user " + username);
        return invoice;
    }

    public boolean hasPurchasedItem(String username, String archiveId) {
        Invoice invoice = invoiceRepo.findByUsernameAndItemId(username, archiveId);
        return invoice != null && invoice.getPaid();
    }

    public List<String> getPurchasedItemIdsByUser(String username){
        List<Invoice> purchased = invoiceRepo.findInvoiceByUsernameAndIsPaidIsTrue(username);
        return purchased.stream().map(Invoice::getItemId).collect(Collectors.toList());
    }
}
