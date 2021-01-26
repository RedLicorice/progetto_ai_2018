package it.polito.ai.services;

import it.polito.ai.exceptions.InvalidDataException;
import it.polito.ai.models.Account;
import it.polito.ai.repositories.AccountDAO;
import it.polito.ai.repositories.AccountRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.security.auth.login.AccountException;
import java.util.List;

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
        Account account = accountRepo.findByUsername(s);
        if (account != null) {
            return account;
        } else {
            throw new UsernameNotFoundException("Username " + s + " not found");
        }
    }

    public Account findAccountByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepo.findByUsername(username);
        if (account != null) {
            return account;
        } else {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }

    }

    public Account findAccountById(String id) throws UsernameNotFoundException {
        Account account = accountRepo.findById(id);
        if (account != null) {
            return account;
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
    	Account existingAccount = accountRepo.findByUsername(account.getUsername());
    	if (existingAccount == null) {
    		account.setPassword(passwordEncoder.encode(account.getPassword()));
            return accountRepo.save(account);
    	} else {
            throw new AccountException("Username already taken");
        }
    }

    @Transactional
    public Account update(Account account) throws AccountException {
        Account existingAccount = accountRepo.findByUsername(account.getUsername());
        if (existingAccount == null) {
            return accountRepo.save(account);
        } else {
            throw new AccountException("Account does not exist");
        }
    }

    @Transactional
    public Account setPassword(String username, String password) throws AccountException {
        Account existingAccount = accountRepo.findByUsername(username);
        if (existingAccount != null) {
            existingAccount.setPassword(passwordEncoder.encode(password));
            return accountRepo.save(existingAccount);
        } else {
            throw new AccountException("Account not found!");
        }
    }

    @Transactional
    public Account topupTokens(String username, Double amount) throws AccountException, InvalidDataException {
        if(amount.isInfinite() || amount.isNaN() || amount < 10 || amount > 1000){
            throw new InvalidDataException("Invalid topup amount");
        }
        Account existingAccount = accountRepo.findByUsername(username);
        if (existingAccount != null) {
            existingAccount.addWallet(amount);
            return accountRepo.save(existingAccount);
        } else {
            throw new AccountException("Account not found!");
        }
    }

    public List<Account> findAll() {
        return accountRepo.findAll();
    }

}
