package it.polito.ai.repositories;

import it.polito.ai.models.Measure;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.repository.Repository;
import java.util.List;
import java.util.Optional;


public interface MeasureRepo extends Repository<Measure, String> {
    //ToDo: Add not deleted condition to these 4 methods
    Optional<List<Measure>> findAllByPositionWithin(
            GeoJsonPolygon polygon
    );
    Optional<List<String>> findDistinctUsernameByPositionWithin(
            GeoJsonPolygon polygon
    );
    Optional<List<Long>> findDistinctTimestampByPositionWithin(
            GeoJsonPolygon polygon
    );
    Optional<List<String>> findDistinctArchiveIdByUsernameInAndTimestampBetweenAndPositionWithin(
            List<String> users,
            Long begin,
            Long end,
            GeoJsonPolygon polygon
    );
    Optional<List<Measure>> findAllByArchiveId(String archiveId);
    //Optional<Measure> findTopByOrderByTimestampByUsername(String username);
    Optional<Measure> findTopByOrderByTimestampAscUsername(String username);
    Measure save(Measure m);
}
