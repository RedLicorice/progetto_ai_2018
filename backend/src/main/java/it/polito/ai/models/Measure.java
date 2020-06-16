package it.polito.ai.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="measures")
public class Measure {
    @Id
    protected String id; // Unique ID for this measure
    protected String archiveId; // GUID of the archive this measure belongs to
    protected String username; // Unique username of the user who uploaded this measure
    protected Long timestamp; // When this measure was taken
    protected GeoJsonPoint position; // Geolocation for this measurement
    protected Boolean isDeleted; // If False, this measure is not available for purchase

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public GeoJsonPoint getPosition() {
        return position;
    }

    public void setPosition(GeoJsonPoint position) {
        this.position = position;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
