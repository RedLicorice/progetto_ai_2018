package it.polito.ai.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Virtual entity (not persisted) representing an archive, as seen by the owner
//  and the buyer.
@Document(collection="archives")
public class Archive {
    @Id
    private String id;
    private String username; //Owner of this archive
    private Integer purchases;
    private Integer price;
    private Integer measures;
    private Boolean deleted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPurchases() {
        return purchases;
    }

    public void setPurchases(Integer purchases) {
        this.purchases = purchases;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getMeasures() {
        return measures;
    }

    public void setMeasures(Integer measures) {
        this.measures = measures;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
