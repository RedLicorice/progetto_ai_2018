package it.polito.ai.repositories;

import it.polito.ai.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    public AccountDAO(MongoTemplate tmpl){
        super();
        this.mongoTemplate = tmpl;
    }

    public List<Account> findByRole(String role)
    {
        String _role = "ROLE_"+role;
        Query q=new Query();
        q.addCriteria( Criteria.where("roles").in(_role));
        return mongoTemplate.find(q, Account.class);
    }
}
