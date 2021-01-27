package it.polito.ai.services;

import it.polito.ai.exceptions.ArchiveNotFoundException;
import it.polito.ai.exceptions.InvalidPositionException;
import it.polito.ai.models.archive.Position;
import it.polito.ai.models.archive.Archive;
import it.polito.ai.models.archive.Measure;
import it.polito.ai.repositories.ArchiveDAO;
import it.polito.ai.repositories.ArchiveRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArchiveService {
    private static final int SPEED_THRESHOLD = 10; // m/s - Average sprint speed for humans is 32 km/h which is roughly 9 m/s
    private static final int PRICE_PER_POSITION = 1;

    @Autowired
    private ArchiveRepo archiveRepo;

    @Autowired
    private ArchiveDAO archiveDao;

    // Check that submitted positions are valid
    protected void checkPositions(String username, List<Measure> measures) throws InvalidPositionException {
        // Get timestamp of last uploaded archive
        /*Archive last = archiveRepo.findFirstByUsernameOrderByLastTimestampDesc(username);
        long previous_timestamp = 0;
        if(last != null && last.getLastTimestamp() != null){
            previous_timestamp = last.getLastTimestamp();
        }*/

        //Check that all the positions are valid
        Measure previous = null;
        int i = 0;
        for(Measure current: measures){
            if (!current.isValid()) {
                throw new InvalidPositionException("Position " + (i + 1) + " is not valid (check)");
            }
            /*if (previous_timestamp > 0 && current.getTimestamp() < previous_timestamp) {
                throw new InvalidPositionException("Position " + (i + 1) + " is not valid (timestamp)");
            }*/
            if (previous != null) {
                if(previous.getTimestamp() > current.getTimestamp())
                    throw new InvalidPositionException("Position " + (i + 1) + " is not valid (timestamp)");
                // Since archives contain weekly measurements, it could happen that user goes by car or train,
                // making a speed constraint unuseful
//                double speed = current.getSpeed(previous);
//                if (speed > SPEED_THRESHOLD) {
//                    throw new InvalidPositionException("Position " + (i + 1) + " is not valid (speed "+ (speed) +")");
//                }
            }
            previous = current;
            //previous_timestamp = current.getTimestamp();
            i++;
        }
    }

    @Transactional
    public Archive createArchive(String username, ArrayList<Measure> measures) throws InvalidPositionException {
        checkPositions(username, measures); // Will throw if any position is not valid.
        Archive a = new Archive();
        a.setUsername(username);
        a.setPurchases(0);
        a.setPrice(measures.size() * PRICE_PER_POSITION);
        a.setDeleted(false);

        TreeSet<Position> positions = new TreeSet<>();
        TreeSet<Long> timestamps = new TreeSet<>();
        for (Measure m: measures) {
            // Approximated measure representation
            // Round coordinates to second decimal digit
            double lat = Math.round(m.getLatitude() * 1000.0) / 1000.0;
            double lon = Math.round(m.getLongitude() * 1000.0) / 1000.0;
            // Round timestamp to last minute
            long ts = m.getTimestamp() - m.getTimestamp() % 60;

            positions.add(new Position(lon, lat));
            timestamps.add(ts);
        }

        a.setMeasures(measures);
        a.setPositions(new ArrayList<>(positions));
        a.setTimestamps(new ArrayList<>(timestamps));
        archiveRepo.save(a);
        return a;
    }

    public List<Archive> findPurchasableArchives(String username, GeoJsonPolygon area, Long from, Long to, List<String> usernames)
            throws ArchiveNotFoundException
    {
        //ArchiveDAO dao = new ArchiveDAO();
        if(to != null && to > new Date().getTime())
            throw new ArchiveNotFoundException("Cannot purchase future archives!");
        return archiveDao.findArchivesByPositionInAndTimestampBetweenAndNotDeleted(username, area, from, to, usernames);
    }

    public List<Archive> findPurchasedArchives(String username)
    {
        //ArchiveDAO dao = new ArchiveDAO();
        return archiveDao.findPurchasedArchives(username);
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

    public List<Archive> getArchives(List<String> archiveIds) {
        List<Archive> a = archiveDao.findNotDeletedByIdIn(archiveIds);
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

    @Transactional
    public List<Archive> deleteUserArchives(String username) {
        List<Archive> archives = archiveRepo.deleteAllByUsername(username);
        return archives;
    }
}
