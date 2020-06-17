package it.polito.ai.models;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

public class StoreSearchRequest {
    // To draw a rectangle (area shown on the map) we only need the top-left and bottom-right corners.
    private double lat1;
    private double lon1;
    private double lat2;
    private double lon2;

    public double getLat1() {
        return lat1;
    }

    public void setLat1(double lat1) {
        this.lat1 = lat1;
    }

    public double getLon1() {
        return lon1;
    }

    public void setLon1(double lon1) {
        this.lon1 = lon1;
    }

    public double getLat2() {
        return lat2;
    }

    public void setLat2(double lat2) {
        this.lat2 = lat2;
    }

    public double getLon2() {
        return lon2;
    }

    public void setLon2(double lon2) {
        this.lon2 = lon2;
    }

    public GeoJsonPolygon getRect(){
        /* Basically, we have a rectangle like this:
        *       A --------- B
        *       |           |
        *       C --------- D
        *  And this model only contains points A and D.
        *  A ( lon1, lat1 ), D ( lon2, lat2)
        *  Therefore, we can derive pointd B and C by crossing:
        *  C ( lon1, lat2 ) and B ( lon2, lat1 )
        * */
        GeoJsonPolygon result = new GeoJsonPolygon(
                new GeoJsonPoint(lon1, lat1), // A
                new GeoJsonPoint(lon2, lat1), // B
                new GeoJsonPoint(lon1, lat2), // C
                new GeoJsonPoint(lon2, lat2), // D
                new GeoJsonPoint(lon1, lat1) // A - Again, need to close the loop or mongo will cry
        );
        return result;
    }
}
