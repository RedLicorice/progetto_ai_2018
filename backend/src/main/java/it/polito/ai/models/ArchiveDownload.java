package it.polito.ai.models;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.Map;

public class ArchiveDownload {
    private String id;
    private String username; //Owner of this archive
    private Map<Long, GeoJsonPoint> measures;

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

    public Map<Long, GeoJsonPoint> getMeasures() {
        return measures;
    }

    public void setMeasures(Map<Long, GeoJsonPoint> measures) {
        this.measures = measures;
    }
}
