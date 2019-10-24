package it.polito.ai.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PositionEntry {

    @JsonIgnore
    private String userId;
    private double longitude;
    private double latitude;
    private long timestamp;

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonIgnore
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean checkIfValid() {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return false;
        }
        return true;
    }

    //Returns distance between two positions
    public double getDistance(PositionEntry that) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(that.getLatitude() - this.getLatitude());
        double lonDistance = Math.toRadians(that.getLongitude() - this.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.getLatitude())) * Math.cos(Math.toRadians(that.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }

    public double getSpeed(PositionEntry that){
        long time = this.getTimestamp() - that.getTimestamp();
        if (time != 0){
            return (this.getDistance(that)*1000)/time;
        }

        return 0;
    }

}
