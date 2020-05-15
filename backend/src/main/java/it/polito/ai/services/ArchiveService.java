package it.polito.ai.services;

import it.polito.ai.exceptions.ArchiveNotFoundException;
import it.polito.ai.exceptions.InvalidPositionException;
import it.polito.ai.models.Archive;
import it.polito.ai.models.PositionEntry;
import it.polito.ai.repositories.ArchiveRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ArchiveService {
    private static final int SPEED_THRESHOLD = 100;

    @Autowired
    private ArchiveRepo archiveRepo;

    protected void checkPositions(String userId, List<PositionEntry> positions) throws InvalidPositionException{
        List<Archive> archives = archiveRepo.findAllByUserIdEquals(userId);
        long previous_timestamp = 0;
        if(!archives.isEmpty()){
            Archive last = archives.get(archives.size() -1);
            previous_timestamp = last.getLastPositionTimestamp();
        }

        // check that all the positions are valid
        PositionEntry previous = null;
        int i = 0;
        for(PositionEntry current: positions){
            if (!current.checkIfValid() || previous_timestamp > 0 && current.getTimestamp() < previous_timestamp || previous != null && current.getSpeed(previous) > SPEED_THRESHOLD) {
                throw new InvalidPositionException("Position " + (i + 1) + " is not valid");
            }
            previous = current;
            previous_timestamp = current.getTimestamp();
            i++;
        }
    }

    @Transactional
    public Archive createArchive(String userId, List<PositionEntry> positionEntries) throws InvalidPositionException{
        checkPositions(userId, positionEntries); // Will throw if any position is not valid.

        List<Long> timestamps = new ArrayList<>();
        List<GeoJsonPoint> positions = new ArrayList<>();
        Map<Long, GeoJsonPoint> realPositions = new HashMap<>();
        
        for (PositionEntry p: positionEntries) {
            // Round timestamp to last minute
            long roundedTimestamp = p.getTimestamp() - p.getTimestamp() % 60;
            // Round coordinates to second decimal digit
            Double lat = Math.round(p.getLatitude() * 100.0) / 100.0;
            Double lng = Math.round(p.getLongitude() * 100.0) / 100.0;

            timestamps.add(roundedTimestamp);
            positions.add(new GeoJsonPoint(lat, lng));
            realPositions.put(p.getTimestamp(), new GeoJsonPoint(p.getLatitude(), p.getLongitude()));
        }
        Archive a = new Archive();
        a.setUserId(userId);
        a.setDeleted(false);
        a.setRealPositions(realPositions);
        a.setPositions(positions);
        a.setTimestamps(timestamps);
        archiveRepo.save(a);

        return a;
    }

    public List<Archive> getArchives(String userId){
        return archiveRepo.findAllByUserIdEquals(userId);
    }

    public Archive getArchive(String userId, String archiveId) throws ArchiveNotFoundException {
        Optional<Archive> oa = archiveRepo.findByIdAndUserIdEquals(archiveId, userId);
        if(!oa.isPresent()){
            throw new ArchiveNotFoundException(archiveId);
        }
        return oa.get();
    }

    public List<Archive> getArchivesWithinPolygon(String userId){
        return archiveRepo.findAllByUserIdEquals(userId);
    }

    public void deleteArchive(String userId, String archiveId) throws ArchiveNotFoundException {
        Optional<Archive> oa = archiveRepo.findByIdAndUserIdEquals(archiveId, userId);
        if(!oa.isPresent()){
            throw new ArchiveNotFoundException(archiveId);
        }
        Archive a = oa.get();
        a.setDeleted(true);
        archiveRepo.save(a);
    }
}
