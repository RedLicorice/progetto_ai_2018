package it.polito.ai.services;

import it.polito.ai.exceptions.InvoiceNotFoundException;
import it.polito.ai.exceptions.NoResultsException;
import it.polito.ai.exceptions.NotEnoughFundsException;
import it.polito.ai.exceptions.UserNotFoundException;
import it.polito.ai.models.*;
import it.polito.ai.repositories.AccountRepo;
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
    private ArchiveService archiveService;


    /*
    * Returns approximate measures by input filters
    * */
    public List<GeoJsonPoint> searchPointsInRect(
            GeoJsonPolygon rect
    ) throws NoResultsException
    {
        Optional<List<Measure>> getter = measureRepo.findAllByPositionWithin(rect);
        if(!getter.isPresent()) {
            throw new NoResultsException();
        }
        List<GeoJsonPoint> result = new LinkedList<>();
        for(Measure m : getter.get()){
            // Round timestamp to last minute
            //long roundedTimestamp = m.getTimestamp() - m.getTimestamp() % 60;
            // Round coordinates to second decimal digit
            GeoJsonPoint p = m.getPosition();
            Double lat = Math.round(p.getY() * 100.0) / 100.0;
            Double lng = Math.round(p.getX() * 100.0) / 100.0;

            result.add(new GeoJsonPoint(lng, lat));
        }
        return result;
    }

    public List<Long> searchTimestampsInRect(
            GeoJsonPolygon rect
    ) throws NoResultsException
    {
        Optional<List<Long>> getter = measureRepo.findDistinctTimestampByPositionWithin(rect);
        if(!getter.isPresent()) {
            throw new NoResultsException();
        }
        List<Long> result = new LinkedList<>();
        for(Long ts : getter.get()){
            result.add(ts - ts % 60);
        }
        return result;
    }

    public List<String> searchUsernamesInRect(
            GeoJsonPolygon rect
    ) throws NoResultsException
    {
        Optional<List<String>> getter = measureRepo.findDistinctUsernameByPositionWithin(rect);
        if(!getter.isPresent()) {
            throw new NoResultsException();
        }
        return getter.get();
    }

    public Invoice createInvoice(
            String username, // The buyer's username
            List<String> users, // User selects a subset of users from which he wants to buy the archives
            Long from, // User can select a specific time interval for the purchased archivees
            Long to, // -- this is the end timestamp
            GeoJsonPolygon polygon // User selects a subset of displayed data by tracing a polygon on the map
    ) throws NoResultsException
    {
        Optional<List<String>> archives = measureRepo.findDistinctArchiveByUsernameInAndTimestampBetweenAndPositionWithin(
                users, from, to, polygon
        );
        if(!archives.isPresent()){
            throw new NoResultsException();
        }
        Invoice invoice = new Invoice();
        invoice.setUsername(username);
        //ToDo: Price should be the sum of each archive's price!
        invoice.setAmount(archives.get().size() * COST_PER_ARCHIVE);
        invoice.setArchives(archives.get());
        invoice.setPaid(false);
        invoiceRepo.save(invoice);
        return invoice;
    }

    @Transactional
    public Invoice payInvoice(
            String username,
            String invoiceId
    ) throws InvoiceNotFoundException, UserNotFoundException, NotEnoughFundsException
    {
        Optional<Invoice> invoice = invoiceRepo.findByIdAndUsername(invoiceId, username);
        if(!invoice.isPresent()){
            throw new InvoiceNotFoundException(invoiceId, username);
        }
        Optional<Account> buyer = accountRepo.findByUsername(username);
        if(!buyer.isPresent())
            throw new UserNotFoundException(username);
        if(buyer.get().getWallet() < invoice.get().getAmount())
            throw new NotEnoughFundsException(username, invoice.get().getAmount());
        buyer.get().setWallet(buyer.get().getWallet() - invoice.get().getAmount());
        accountRepo.save(buyer.get());
        for(String archive : invoice.get().getArchives()){
            //ToDO: credit each archive's price to its owner and increase purchase count.
        }
        invoice.get().setPaid(true);
        invoiceRepo.save(invoice.get());
        return invoice.get();
    }
}
