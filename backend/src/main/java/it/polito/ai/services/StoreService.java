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
    public List<Measure> searchData(
            GeoJsonPolygon rect, // The rectangle currently shown in the map
            List<String> users,
            Long from,
            Long to
    ) throws NoResultsException
    {
        Optional<List<Measure>> measures_getter = measureRepo.findAllByPositionWithin(rect);
        if(!measures_getter.isPresent()){
            throw new NoResultsException();
        }
        List<Measure> measures = measures_getter.get();
        //Apply filters
        if(!users.isEmpty()){
            measures.removeIf(m -> (!users.contains(m.getUsername())));
        }
        if(from != 0 && to != 0){
            measures.removeIf(m -> (m.getTimestamp() < from || m.getTimestamp() > to));
        }
        // Approximate
        for(Measure m : measures){
            // Round timestamp to last minute
            long roundedTimestamp = m.getTimestamp() - m.getTimestamp() % 60;
            // Round coordinates to second decimal digit
            GeoJsonPoint p = m.getPosition();
            Double lat = Math.round(p.getY() * 100.0) / 100.0;
            Double lng = Math.round(p.getX() * 100.0) / 100.0;

            m.setTimestamp(roundedTimestamp);
            m.setPosition(new GeoJsonPoint(lng, lat));
        }
        return measures;
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
            Optional<Archive> archive = archiveRepo.
            Optional<Account> seller = accountRepo.findByUsername(sellerUsername);
            if(!seller.isPresent())
                throw new UserNotFoundException(sellerUsername);
            seller.get().setWallet(seller.get().getWallet() + amount);
        }


        invoice.get().setPaid(true);
        invoiceRepo.save(invoice.get());
        return invoice.get();
    }

    public List<Purchase> getPurchases(String userId) {
        return purchaseRepo.findAllByUserIdEquals(userId);
    }

}
