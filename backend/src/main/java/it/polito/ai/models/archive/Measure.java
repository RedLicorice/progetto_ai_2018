package it.polito.ai.models.archive;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

/*
* This model represents a single measure,
* it is not persisted as it is stored inside of an archive.
* */
public class Measure {

    protected Long timestamp; // When this measure was taken

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    protected GeoJsonPoint position; // Geolocation for this measurement

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
}
