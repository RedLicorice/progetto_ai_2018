package it.polito.ai.repositories;

import it.polito.ai.models.archive.Archive;
import it.polito.ai.models.store.Invoice;
import it.polito.ai.services.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class ArchiveDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StoreService storeService;

    public List<Archive> findArchivesByPositionInAndTimestampBetweenAndNotDeleted(
            String username,
            GeoJsonPolygon area,
            Long from,
            Long to,
            List<String> usernames
    ) {
        List<String> purchasedIds = storeService.getPurchasedItemIdsByUser(username);
        Query query = new Query(Criteria.where("measures.position").within(area));
        query.addCriteria(Criteria.where("deleted").is(false));
        query.addCriteria(Criteria.where("username").ne(username));
        if(!purchasedIds.isEmpty())
            query.addCriteria(Criteria.where("id").nin(purchasedIds));
        if(from != null && to != null)
            query.addCriteria(Criteria.where("measures.timestamp").gt(from).andOperator(Criteria.where("measures.timestamp").lte(to)));
        if(usernames != null && !usernames.isEmpty())
            query.addCriteria(Criteria.where("username").in(username));
        return mongoTemplate.find(query, Archive.class);
    }

    public List<Archive> findPurchasedArchives(String username) {
        List<String> purchasedIds = storeService.getPurchasedItemIdsByUser(username);
        if(purchasedIds == null)
            return new LinkedList<Archive>();
        Query query = new Query(Criteria.where("id").in(purchasedIds));
        return mongoTemplate.find(query, Archive.class);
    }

    public List<Archive> findNotDeletedByIdIn(
            List<String> archiveIds
    ) {
        Query query = new Query(Criteria.where("id").in(archiveIds));
        query.addCriteria(Criteria.where("deleted").is(false));
        return mongoTemplate.find(query, Archive.class);
    }
}
