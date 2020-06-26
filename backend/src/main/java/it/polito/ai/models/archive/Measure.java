package it.polito.ai.models.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.polito.ai.exceptions.InvalidPositionException;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/*
* This model represents a single measure,
* it is not persisted as it is stored inside of an archive.
*
* A custom serializer is needed, otherwise this would be rendered as an empty object.
* */
@JsonSerialize(using = MeasureSerializer.class)
public class Measure {
    protected Long timestamp; // When this measure was taken

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    protected GeoJsonPoint position; // Geolocation for this measurement

    public Measure() {
        //Do nothing
    }

    @JsonCreator
    public Measure(
            @JsonProperty(value = "longitude", required = true) double longitude,
            @JsonProperty(value = "latitude", required = true) double latitude,
            @JsonProperty(value = "timestamp", required = true) long timestamp
    ) throws InvalidPositionException {
        this.timestamp = timestamp;
        this.position = new GeoJsonPoint(longitude, latitude); // X and Y
        if (!isValidTimestamp()) {
            throw new InvalidPositionException("Invalid timestamp!");
        }
        if (!isValidPosition()) {
            throw new InvalidPositionException("Invalid position!");
        }
    }

    public boolean isValidPosition() {
        if (getLatitude() < -90 || getLatitude() > 90 || getLongitude() < -180 || getLongitude() > 180)
            return false;
        return true;
    }

    public boolean isValidTimestamp() {
        if(timestamp > new Date().getTime())
            return false;
        return true;
    }

    public boolean isValid(){
        return isValidPosition() && isValidTimestamp();
    }

    public double getDistance(Measure that) {
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

    public double getSpeed(Measure that){
        // Time between the two measurements in seconds (timestamps are unix epoch)
        long time = this.getTimestamp() - that.getTimestamp();
        if (time != 0){
            //Distance was multiplied by 1000, we don't want that.
            // We measure speed in meters per second m/s.
            return (this.getDistance(that))/time;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Measure)) return false;
        Measure that = (Measure) o;
        return Objects.equals(getPosition(), that.getPosition()) &&
                Objects.equals(getTimestamp(), that.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPosition(), getTimestamp());
    }

    @JsonGetter("longitude")
    public double getLongitude(){
        return position.getX();
    }

    @JsonGetter("latitude")
    public double getLatitude(){
        return position.getY();
    }

    @JsonGetter("timestamp")
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
