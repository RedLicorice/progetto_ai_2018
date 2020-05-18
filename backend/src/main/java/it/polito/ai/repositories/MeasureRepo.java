package it.polito.ai.repositories;

import it.polito.ai.models.Measure;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.repository.Repository;
import java.util.List;
import java.util.Optional;


public interface MeasureRepo extends Repository<Measure, String> {
    Optional<List<Measure>> findAllByPositionWithin(
            GeoJsonPolygon polygon
    );
    Optional<List<String>> findDistinctArchiveByUsernameInAndTimestampBetweenAndPositionWithin(
            List<String> users,
            Long begin,
            Long end,
            GeoJsonPolygon polygon
    );
    Optional<List<Measure>> findAllByArchive(String guid);
    Optional<List<String>> findDistinctArchiveByUsername(String username);
    Optional<Measure> findTopByOrderByTimestampByUsername(String username);
    Measure save(Measure m);
}
