package it.polito.ai.models.archive;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import it.polito.ai.models.store.Purchasable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/*
* This model represents the stores archive, it also is the resource which is shown to the users.
* JsonView is used to obtain the different representations.
* ArchiveView defines such representations.
* */
@Document(collection="archives")
public class Archive implements Purchasable {
    @Id
    @JsonView(ArchiveView.Summary.class)
    private String id; // Archive's unique identifier

    @JsonView(ArchiveView.Summary.class)
    private String username; //Owner of this archive

    @JsonView(ArchiveView.OwnerSummary.class)
    private Integer purchases;

    @JsonView(ArchiveView.OwnerSummary.class)
    private Boolean deleted;

    @JsonView(ArchiveView.Resource.class)
    private List<Measure> measures;

    @JsonView(ArchiveView.PublicResource.class)
    private Integer price;

    @JsonView(ArchiveView.PublicResource.class)
    private List<Position> positions;

    @JsonView(ArchiveView.PublicResource.class)
    private List<Long> timestamps;

    @JsonGetter("count")
    @JsonView(ArchiveView.PublicResource.class)
    public int getCount() {
        return measures.size();
    }

    @JsonIgnore
    private Long lastTimestamp;

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

    public void addPurchases(Integer amount) {
        this.purchases += amount;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
        this.lastTimestamp = this.measures.stream()// Using measures as stream
                .map(Measure::getTimestamp) // Map to timestamps
                .max(Long::compare) // And take the highest timestamp
                .get();
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<Long> timestamps) {
        this.timestamps = timestamps;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(Long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }
}
