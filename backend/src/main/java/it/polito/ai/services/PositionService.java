package it.polito.ai.services;

import it.polito.ai.exceptions.InvalidPositionException;
import it.polito.ai.models.Account;
import it.polito.ai.models.Position;
import it.polito.ai.models.PositionEntry;
import it.polito.ai.repositories.PositionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PositionService {

    private static final int THRESHOLD = 100;

    @Autowired
    private PositionRepo positionRepo;

    @Autowired
    private AccountService accountService;

    private List<PositionEntry> convertPositionsToPositionsEntries(List<Position> positions) {
        List<PositionEntry> result = new ArrayList<>();
        for (Position p: positions) {
            PositionEntry entry = new PositionEntry();
            entry.setUserId(p.getUserId());
            entry.setLongitude(p.getPoint().getX());
            entry.setLatitude(p.getPoint().getY());
            entry.setTimestamp(p.getTimestamp());

            result.add(entry);
        }

        return result;
    }

    public List<Position> checkPositions(String userId, List<PositionEntry> positions) throws InvalidPositionException {
        List<PositionEntry> userPositions = this.getPositions(userId, Optional.empty(), Optional.empty());

        int startIndex;
        PositionEntry lastValidPosition = null;
        if (userPositions.size() > 0) {
            lastValidPosition = userPositions.get(userPositions.size() - 1);
            startIndex = 0;
        } else {
            lastValidPosition = positions.get(0);
            startIndex = 1;
        }

        if (lastValidPosition == null) {
            throw new InvalidPositionException("Invalid last position");
        }

        if (!lastValidPosition.checkIfValid()) {
            throw new InvalidPositionException("Invalid last position");
        }

        // check that all the positions are valid
        PositionEntry previous = lastValidPosition;
        for (int i = startIndex; i < positions.size(); i++) {
            PositionEntry current = positions.get(i);
            if (!current.checkIfValid() || current.getTimestamp() < previous.getTimestamp() || current.getSpeed(previous) > THRESHOLD) {
                throw new InvalidPositionException("Position " + (i + 1) + " is not valid");
            }

            previous = current;
        }

        // if we get here, all the submitted positions are valid
        List<Position> realPositions = new ArrayList<Position>(positions.size());
        for (PositionEntry position: positions) {
            Position p = new Position();
            p.setUserId(userId);
            p.setPoint(new GeoJsonPoint(position.getLongitude(), position.getLatitude()));
            p.setTimestamp(position.getTimestamp());

            realPositions.add(p);
        }
        return realPositions;
    }

    @Transactional
    public List<Position> savePositions(String userId, List<PositionEntry> positions) throws InvalidPositionException {
        List<PositionEntry> userPositions = this.getPositions(userId, Optional.empty(), Optional.empty());

        int startIndex;
        PositionEntry lastValidPosition = null;
        if (userPositions.size() > 0) {
            lastValidPosition = userPositions.get(userPositions.size() - 1);
            startIndex = 0;
        } else {
            lastValidPosition = positions.get(0);
            startIndex = 1;
        }

        if (lastValidPosition == null) {
            throw new InvalidPositionException("Invalid last position");
        }

        if (!lastValidPosition.checkIfValid()) {
            throw new InvalidPositionException("Invalid last position");
        }

        // check that all the positions are valid
        PositionEntry previous = lastValidPosition;
        for (int i = startIndex; i < positions.size(); i++) {
            PositionEntry current = positions.get(i);
            if (!current.checkIfValid() || current.getTimestamp() < previous.getTimestamp() || current.getSpeed(previous) > THRESHOLD) {
                throw new InvalidPositionException("Position " + (i + 1) + " is not valid");
            }

            previous = current;
        }

        // if we get here, all the submitted positions are valid
        List<Position> realPositions = new ArrayList<Position>(positions.size());
        for (PositionEntry position: positions) {
            Position p = new Position();
            p.setUserId(userId);
            p.setPoint(new GeoJsonPoint(position.getLongitude(), position.getLatitude()));
            p.setTimestamp(position.getTimestamp());
            positionRepo.save(p);

            realPositions.add(p);
        }
        return realPositions;
    }

    public List<PositionEntry> getPositions(
            String userId,
            Optional<Long> from,
            Optional<Long> to
    ) {
        List<Position> positions = new ArrayList<>();
        boolean found = false;

        if (from.isPresent() && to.isPresent()) {
            positions = positionRepo.findAllByUserIdEqualsAndTimestampBetween(userId, from.get(), to.get());
            found = true;
        }

        if (from.isPresent()) {
            positions =  positionRepo.findAllByUserIdEqualsAndTimestampAfter(userId, from.get());
            found = true;
        }

        if (to.isPresent()) {
            positions =  positionRepo.findAllByUserIdEqualsAndTimestampBefore(userId, to.get());
            found = true;
        }

        if (!found) {
            positions = positionRepo.findAllByUserIdEquals(userId);
        }

        List<PositionEntry> result = convertPositionsToPositionsEntries(positions);
        return result;
    }

    public List<PositionEntry> getPositionsWithinPolygon(
            List<PositionEntry> positions,
            Long from,
            Long to
    ) {
        List<Point> points = new ArrayList<Point>();
        for (PositionEntry p: positions) {
            points.add(new Point(p.getLongitude(), p.getLatitude()));
        }

        GeoJsonPolygon polygon = new GeoJsonPolygon(points);

        List<Position> positionsResult = positionRepo.findAllByPointWithinAndTimestampBetween(polygon, from, to);
        List<PositionEntry> result = convertPositionsToPositionsEntries(positionsResult);
        return result;
    }
}
