package it.polito.ai.services;

import it.polito.ai.models.Account;
import it.polito.ai.repositories.AccountDAO;
import it.polito.ai.repositories.AccountRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MongoTemplate mongoTempl;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Account> account = accountRepo.findByUsername(s);
        if (account.isPresent()) {
            return account.get();
        } else {
            throw new UsernameNotFoundException("Username " + s + " not found");
        }
    }

    public Account findAccountByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepo.findByUsername(username);
        if (account.isPresent()) {
            return account.get();
        } else {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }

    }

    public Account findAccountById(String id) throws UsernameNotFoundException {
        Optional<Account> account = accountRepo.findById(id);
        if (account.isPresent()) {
            return account.get();
        } else {
            throw new UsernameNotFoundException("Account with ID " + id + " not found");
        }
    }

    public boolean existsAccountByRole(String role) throws UsernameNotFoundException {
        AccountDAO dao = new AccountDAO(mongoTempl);
        List<Account> account = dao.findByRole(role);
        return account.size() > 0;
    }

    @Transactional
    public Account register(Account account) throws AccountException {
    	Optional<Account> existingAccount = accountRepo.findByUsername(account.getUsername());
    	if (!existingAccount.isPresent()) {
    		account.setPassword(passwordEncoder.encode(account.getPassword()));
            return accountRepo.save(account);
    	} else {
            throw new AccountException("Username already taken");
        }
    }

    @Transactional
    public Account update(Account account) throws AccountException {
        Optional<Account> existingAccount = accountRepo.findByUsername(account.getUsername());
        if (existingAccount.isPresent()) {
            return accountRepo.save(account);
        } else {
            throw new AccountException("Account does not exist");
        }
    }

    public List<Account> findAll() {
        return accountRepo.findAll();
    }

    @Transactional
    public Account saveAccount(Account account) {
        return accountRepo.save(account);
    }

}
