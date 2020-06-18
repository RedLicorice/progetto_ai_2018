package it.polito.ai.services;

import it.polito.ai.exceptions.ArchiveNotFoundException;
import it.polito.ai.exceptions.MeasuresNotFoundException;
import it.polito.ai.exceptions.InvalidPositionException;
import it.polito.ai.models.archive.Archive;
import it.polito.ai.models.archive.Measure;
import it.polito.ai.models.archive.MeasureSubmission;
import it.polito.ai.repositories.ArchiveDAO;
import it.polito.ai.repositories.ArchiveRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ArchiveService {
    private static final int SPEED_THRESHOLD = 10; // Average sprint speed for humans is 32 km/h which is roughly 9 m/s
    private static final int PRICE_PER_POSITION = 1;

    @Autowired
    private ArchiveRepo archiveRepo;

    // Check that submitted positions are valid
    protected void checkPositions(String username, List<MeasureSubmission> positions) throws InvalidPositionException {
        Archive last = archiveRepo.findTop1ByLastTimestampAndUsername(username);
        long previous_timestamp = 0;
        if(last != null){
            previous_timestamp = last.getLastTimestamp();
        }

        // check that all the positions are valid
        MeasureSubmission previous = null;
        int i = 0;
        for(MeasureSubmission current: positions){
            if (!current.checkIfValid()) {
                throw new InvalidPositionException("Position " + (i + 1) + " is not valid (check)");
            }
            if (previous_timestamp > 0 && current.getTimestamp() < previous_timestamp) {
                throw new InvalidPositionException("Position " + (i + 1) + " is not valid (timestamp)");
            }
            if (previous != null) {
                double speed = current.getSpeed(previous);
                if (speed > SPEED_THRESHOLD) {
                    throw new InvalidPositionException("Position " + (i + 1) + " is not valid (speed "+ (speed) +")");
                }
            }
            previous = current;
            previous_timestamp = current.getTimestamp();
            i++;
        }
    }

    // Generate an archive uuid and save the measures belonging to the same archive
    @Transactional
    public Archive createArchive(String username, List<MeasureSubmission> positionEntries) throws InvalidPositionException {
        checkPositions(username, positionEntries); // Will throw if any position is not valid.
        Archive a = new Archive();
        a.setUsername(username);
        a.setPurchases(0);
        a.setPrice(positionEntries.size() * PRICE_PER_POSITION);
        a.setDeleted(false);

        List<Measure> measures = new LinkedList<>();
        List<Measure> approxMeasures = new LinkedList<>();

        for (MeasureSubmission p: positionEntries) {
            Measure m = new Measure();
            m.setTimestamp(p.getTimestamp());
            m.setPosition(new GeoJsonPoint(p.getLongitude(), p.getLatitude()));
            measures.add(m);
            // Approximated measure representation
            // Round coordinates to second decimal digit
            double lat = Math.round(p.getLatitude() * 100.0) / 100.0;
            double lon = Math.round(p.getLongitude() * 100.0) / 100.0;
            // Round timestamp to last minute
            long ts = p.getTimestamp() - p.getTimestamp() % 60;

            Measure aM = new Measure();
            aM.setTimestamp(ts);
            aM.setPosition(new GeoJsonPoint(lon, lat));
            approxMeasures.add(aM);
        }

        a.setMeasures(measures);
        a.setApproxMeasures(approxMeasures);
        archiveRepo.save(a);
        return a;
    }

    public List<Archive> findPurchasableArchives(String username, GeoJsonPolygon area, Long from, Long to, List<String> usernames)
    {
        ArchiveDAO dao = new ArchiveDAO();
        return dao.findArchivesByPositionInAndTimestampBetweenAndNotDeleted(username, area, from, to, usernames);
    }

    public List<Archive> findPurchasedArchives(String username)
    {
        ArchiveDAO dao = new ArchiveDAO();
        return dao.findPurchasedArchives(username);
    }

    // Retrieve all archives uploaded by username
    public List<Archive> findUserArchives(String username) {
        return archiveRepo.findAllByUsername(username);
    }

    // Retrieve a specific archive uploaded by username
    public Archive getUserArchive(String username, String archiveId) throws ArchiveNotFoundException {
        Optional<Archive> a = archiveRepo.findByUsernameAndId(username, archiveId);
        if(!a.isPresent())
            throw new ArchiveNotFoundException("Archive " + archiveId + " not found!");
        return a.get();
    }

    public Archive getArchive(String archiveId) throws ArchiveNotFoundException{
        Archive a = archiveRepo.findById(archiveId);
        if(a == null)
            throw new ArchiveNotFoundException("Archive " + archiveId + " not found!");
        return a;
    }

    // Set the deleted flag on the measures belonging to the archive with specified id
    @Transactional
    public Archive toggleDeleteArchive(String username, String archiveId) throws ArchiveNotFoundException {
        Optional<Archive> archive = archiveRepo.findByUsernameAndId(username, archiveId);
        if(!archive.isPresent())
            throw new ArchiveNotFoundException(archiveId);
        archive.get().setDeleted(!archive.get().getDeleted());
        archiveRepo.save(archive.get());
        return archive.get();
    }
}
