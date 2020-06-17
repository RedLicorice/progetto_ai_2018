package it.polito.ai.services;

import it.polito.ai.exceptions.*;
import it.polito.ai.models.*;
import it.polito.ai.repositories.AccountRepo;
import it.polito.ai.repositories.ArchiveRepo;
import it.polito.ai.repositories.InvoiceRepo;
import it.polito.ai.repositories.MeasureRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    private static final double COST_PER_ARCHIVE = 1;

    @Autowired
    private InvoiceRepo invoiceRepo;

    @Autowired
    protected AccountRepo accountRepo;

    @Autowired
    private MeasureRepo measureRepo;

    @Autowired
    private ArchiveRepo archiveRepo;

    @Autowired
    private ArchiveService archiveService;


    /*
    * Returns approximate measures by input filters
    * */
    public StoreSearchResult searchInRect(
            GeoJsonPolygon rect
    ) throws NoResultsException
    {
        Optional<List<Measure>> positionGetter = measureRepo.findAllByPositionWithin(rect);
        Optional<List<Long>> timestampGetter = measureRepo.findDistinctTimestampByPositionWithin(rect);
        Optional<List<String>> usernameGetter = measureRepo.findDistinctUsernameByPositionWithin(rect);

        if(!positionGetter.isPresent()) {
            throw new NoResultsException();
        }
        if(!timestampGetter.isPresent()) {
            throw new NoResultsException();
        }
        if(!usernameGetter.isPresent()) {
            throw new NoResultsException();
        }
        // Round timestamps to the minute
        List<Long> timestamps = new LinkedList<>();
        for(Long ts : timestampGetter.get()){
            timestamps.add(ts - ts % 60);
        }
        // Round positions to second decimal digit
        List<GeoJsonPoint> positions = new LinkedList<>();
        for(Measure m : positionGetter.get()){
            // Round timestamp to last minute
            //long roundedTimestamp = m.getTimestamp() - m.getTimestamp() % 60;
            // Round coordinates to second decimal digit
            GeoJsonPoint p = m.getPosition();
            Double lat = Math.round(p.getY() * 100.0) / 100.0;
            Double lng = Math.round(p.getX() * 100.0) / 100.0;

            positions.add(new GeoJsonPoint(lng, lat));
        }
        // Build search results
        StoreSearchResult result = new StoreSearchResult();
        result.setPoints(positions);
        result.setTimestamps(timestamps);
        result.setUsers(usernameGetter.get());
        return result;
    }

    public List<Invoice> createInvoices(
            String username, // The buyer's username
            List<String> users, // User selects a subset of users from which he wants to buy the archives
            Long from, // User can select a specific time interval for the purchased archivees
            Long to, // -- this is the end timestamp
            GeoJsonPolygon polygon // User selects a subset of displayed data by tracing a polygon on the map
    ) throws NoResultsException
    {
        Optional<List<String>> archiveIds = measureRepo.findDistinctArchiveIdByUsernameInAndTimestampBetweenAndPositionWithin(
                users, from, to, polygon
        );
        Optional<List<Archive>> archives = archiveRepo.findAllByIdIn(archiveIds.get());
        if(!archiveIds.isPresent() || !archives.isPresent()){
            throw new NoResultsException();
        }
        List<Invoice> invoiceList = new LinkedList<>();
        for(Archive a : archives.get()){
            Invoice invoice = new Invoice();
            invoice.setUsername(username);
            invoice.setAmount(a.getPrice());
            invoice.setArchiveId(a.getId());
            invoice.setPaid(false);
            invoiceRepo.save(invoice);
            invoiceList.add(invoice);
        }

        return invoiceList;
    }

    @Transactional
    public Invoice payInvoice(
            String username,
            String invoiceId
    ) throws InvoiceNotFoundException, UserNotFoundException, NotEnoughFundsException, ArchiveNotFoundException
    {
        Optional<Invoice> invoice = invoiceRepo.findByIdAndUsername(invoiceId, username);
        if(!invoice.isPresent()){
            throw new InvoiceNotFoundException("Invoice " + invoiceId + " not found for user " + username);
        }
        Optional<Account> buyer = accountRepo.findByUsername(username);
        if(!buyer.isPresent())
            throw new UserNotFoundException(username);
        if(buyer.get().getWallet() < invoice.get().getAmount())
            throw new NotEnoughFundsException(username, invoice.get().getAmount());
        buyer.get().setWallet(buyer.get().getWallet() - invoice.get().getAmount());
        accountRepo.save(buyer.get());

        Optional<Archive> arc = archiveRepo.findById(invoice.get().getArchiveId());
        Optional<Account> acc = accountRepo.findByUsername(arc.get().getUsername());
        if(!arc.isPresent())
            throw new ArchiveNotFoundException(invoice.get().getArchiveId());

        if(!acc.isPresent())
            throw new UserNotFoundException(arc.get().getUsername());
        acc.get().addWallet(arc.get().getPrice());
        arc.get().addPurchases(1);
        archiveRepo.save(arc.get());
        accountRepo.save(acc.get());

        invoice.get().setPaid(true);
        invoiceRepo.save(invoice.get());
        return invoice.get();
    }

    @Transactional
    public List<Invoice> getInvoices(
            String username
    )
    {
        Optional<List<Invoice>> invoices = invoiceRepo.findByUsername(username);
        //ToDo: Delete unpaid invoices older than 24 hours to avoid cluttering the database
        return invoices.get();
    }

    @Transactional
    public Invoice getInvoice(String username, String invoiceId) throws InvoiceNotFoundException
    {
        Optional<Invoice> invoice = invoiceRepo.findByIdAndUsername(invoiceId, username);
        //ToDo: Delete unpaid invoices older than 24 hours to avoid cluttering the database
        if(!invoice.isPresent())
            throw new InvoiceNotFoundException("Invoice " + invoiceId + " not found for user " + username);
        return invoice.get();
    }

    public boolean hasPurchasedItem(String username, String archiveId) {
        Optional<Invoice> invoice = invoiceRepo.findByUsernameAndArchiveId(username, archiveId);
        if(!invoice.isPresent() || !invoice.get().getPaid())
            return false;
        return true;
    }
}
