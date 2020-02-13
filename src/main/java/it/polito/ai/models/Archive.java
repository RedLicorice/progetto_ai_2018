package it.polito.ai.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

/*
*   ToDO: Implement the rest of the model.
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
    private long createdAt; // When the archive was submitted

}
