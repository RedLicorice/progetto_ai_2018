package it.polito.ai.repositories;


import org.springframework.data.repository.Repository;

import it.polito.ai.models.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepo extends Repository<Account, String> {

    List<Account> findAll();
    Account findByUsername(String username);
    Account findById(String id);
    
    Account save(Account account);
    
    void deleteAccountById(String id);

}
