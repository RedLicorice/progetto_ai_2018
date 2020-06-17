package it.polito.ai.models;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

public class StoreSearchResult {
    List<String> users;
    List<Long> timestamps;
    List<GeoJsonPoint> points;

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<Long> timestamps) {
        this.timestamps = timestamps;
    }

    public List<GeoJsonPoint> getPoints() {
        return points;
    }

    public void setPoints(List<GeoJsonPoint> points) {
        this.points = points;
    }
}
