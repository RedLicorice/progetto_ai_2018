package it.polito.ai.models.archive;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import java.util.*;

public class ArchiveSearchRequest {
    // To draw a rectangle (area shown on the map) we only need the top-left and bottom-right corners.
//    private double lat1;
//    private double lon1;
//    private double lat2;
//    private double lon2;
    private Position topLeft;
    private Position bottomRight;
    private Long from;
    private Long to;
    private List<String> users;

    public ArchiveSearchRequest(){
        users = new ArrayList<String>();
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
//                new GeoJsonPoint(lon1, lat1), // A
//                new GeoJsonPoint(lon2, lat1), // B
//                new GeoJsonPoint(lon2, lat2), // D
//                new GeoJsonPoint(lon1, lat2), // C
//                new GeoJsonPoint(lon1, lat1) // A - Again, need to close the loop or mongo will cry
                new GeoJsonPoint(topLeft.getLongitude(), topLeft.getLatitude()), // A
                new GeoJsonPoint(bottomRight.getLongitude(), topLeft.getLatitude()), // B
                new GeoJsonPoint(bottomRight.getLongitude(), bottomRight.getLatitude()), // D
                new GeoJsonPoint(topLeft.getLongitude(), bottomRight.getLatitude()), // C
                new GeoJsonPoint(topLeft.getLongitude(), topLeft.getLatitude()) // A - Again, need to close the loop or mongo will cry
        );
        return result;
    }

//    public GeoJsonPoint getPoint1(){
//        return new GeoJsonPoint(lon1, lat1);
//    }
//
//    public GeoJsonPoint getPoint2(){
//        return new GeoJsonPoint(lon2, lat2);
//    }

//    public double getLat1() {
//        return lat1;
//    }
//
//    public void setLat1(double lat1) {
//        this.lat1 = lat1;
//    }
//
//    public double getLon1() {
//        return lon1;
//    }
//
//    public void setLon1(double lon1) {
//        this.lon1 = lon1;
//    }
//
//    public double getLat2() {
//        return lat2;
//    }
//
//    public void setLat2(double lat2) {
//        this.lat2 = lat2;
//    }
//
//    public double getLon2() {
//        return lon2;
//    }
//
//    public void setLon2(double lon2) {
//        this.lon2 = lon2;
//    }

    public Position getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(Position topLeft) {
        this.topLeft = topLeft;
    }

    public Position getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(Position bottomRight) {
        this.bottomRight = bottomRight;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
