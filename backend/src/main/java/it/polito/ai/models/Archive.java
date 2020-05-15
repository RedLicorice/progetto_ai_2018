package it.polito.ai.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import java.util.*;

/*
*    An archive shall:
*       - Refer to the instances of Position included in the archive
*       - Include an (approximated) list of points corresponding to the positions
*           Second-decimal-digit precision 0.02343425 -> 0.02
*           Sorted
*           No duplicates
*       - Include an (approximated) list of timestamps corresponding to the positions
*           Minute precision 23:59:20 -> 23:59
*           Sorted
*           No duplicates
*       - Include a flag indicating whether it is deleted, therefore available FOR PURCHASE (TBD StoreService)
*           Note that requirements specify a SOFT DELETE not a real delete, ie. users who purchased the archive
*           must always be able to download it.
*       - Include an human-friendly identifier, i'd suggest building it as:
*                   (USERNAME)_(FIRST_POSITION_NAME)_(LAST_POSITION_NAME)_(DATETIME)
 */
@Document(collection="archives")
public class Archive {
    @Id
    private String id;

    private String userId; //Owner of this archive

    private Boolean isDeleted; // If true, archive is not available for purchase

    // List of positions/timestamps for purchasers/owner
    private Map<Long, GeoJsonPoint> realPositions;

    // Approximated positions and timestamps
    private List<GeoJsonPoint> positions;
    private List<Long> timestamps;

    // When the archive has been uploaded
    @CreatedDate
    private Date createdAt;
    // When the archive is updated (ie soft deleted)
    @LastModifiedDate
    private Date modifiedAt;

    public long getFirstPositionTimestamp(){
        Set<Long> t = realPositions.keySet();
        return Collections.min(t);
    }

    public long getLastPositionTimestamp(){
        Set<Long> t = realPositions.keySet();
        return Collections.max(t);
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Map<Long, GeoJsonPoint> getRealPositions() {
        return realPositions;
    }

    public void setRealPositions(Map<Long, GeoJsonPoint> realPositions) {
        this.realPositions = realPositions;
    }

    public List<GeoJsonPoint> getPositions() {
        return positions;
    }

    public void setPositions(List<GeoJsonPoint> positions) {
        this.positions = positions;
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<Long> timestamps) {
        this.timestamps = timestamps;
    }
}
