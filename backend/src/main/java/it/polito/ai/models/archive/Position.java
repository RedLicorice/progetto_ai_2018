package it.polito.ai.models.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.util.Objects;

@JsonSerialize(using = PositionSerializer.class)
public class Position implements Comparable<Position> {

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint point;

    @JsonCreator
    public Position(
            @JsonProperty(value = "longitude", required = true) double longitude,
            @JsonProperty(value = "latitude", required = true) double latitude
    ) {
        point = new GeoJsonPoint(longitude, latitude); // X and Y
    }

    public Position() {
    }

    @JsonGetter("longitude")
    public double getLongitude(){
        return point.getX();
    }

    @JsonGetter("latitude")
    public double getLatitude(){
        return point.getY();
    }

    public GeoJsonPoint getPoint() {
        return point;
    }

    public void setPoint(GeoJsonPoint point) {
        this.point = point;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position that = (Position) o;
        return Objects.equals(getPoint(), that.getPoint());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPoint());
    }

    @Override
    public int compareTo(Position that) {
        if (this.getLongitude() > that.getLongitude()) {
            return 1;
        } else if (getLongitude() < that.getLongitude()) {
            return -1;
        } else {
            if(getLatitude() > that.getLatitude()) {
                return 1;
            } else if (getLatitude() < that.getLatitude()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}