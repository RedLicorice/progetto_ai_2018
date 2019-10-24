package it.polito.ai.repositories;


import it.polito.ai.models.Purchase;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface PurchaseRepo extends Repository<Purchase, String> {

    List<Purchase> findAllByUserIdEquals(String userId);
    Purchase save(Purchase purchase);

}
