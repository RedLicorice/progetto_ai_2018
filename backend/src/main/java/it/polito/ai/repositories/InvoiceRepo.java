package it.polito.ai.repositories;

import it.polito.ai.models.Invoice;
import org.springframework.data.repository.Repository;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepo extends Repository<Invoice, String> {
    Invoice save(Invoice invoice);
    Optional<Invoice> findByIdAndUsername(String id, String username);
}
