package it.polito.ai.repositories;

import it.polito.ai.models.Position;
import org.springframework.data.geo.Polygon;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface PositionRepo extends Repository<Position, String> {

    List<Position> findAllByUserIdEquals(String userId);

    List<Position> findAllByUserIdEqualsAndTimestampAfter(String userId, long from);
    List<Position> findAllByUserIdEqualsAndTimestampBefore(String userId, long to);
    List<Position> findAllByUserIdEqualsAndTimestampBetween(String userId, long from, long to);

    List<Position> findAllByPointWithinAndTimestampBetween(Polygon polygon, long from, long to);

    Position save(Position position);

    void deletePositionById(String id);
}
