package it.polito.ai.repositories;

import it.polito.ai.models.archive.Archive;
import it.polito.ai.models.store.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.Repository;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepo extends Repository<Invoice, String> {
    Invoice save(Invoice invoice);
    Invoice findByIdAndUsername(String id, String username);
    Invoice findByUsernameAndItemId(String username, String archiveId);
    List<Invoice> findByUsername(String username);
    List<Invoice> findInvoiceByUsernameAndIsPaidIsTrue(String username);
    void deleteById(String id);
}
