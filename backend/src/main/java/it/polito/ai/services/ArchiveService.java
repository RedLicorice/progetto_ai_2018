package it.polito.ai.services;

import it.polito.ai.exceptions.ArchiveNotFoundException;
import it.polito.ai.exceptions.MeasuresNotFoundException;
import it.polito.ai.exceptions.InvalidPositionException;
import it.polito.ai.exceptions.UserHasNoArchivesException;
import it.polito.ai.models.Archive;
import it.polito.ai.models.ArchiveDownload;
import it.polito.ai.models.Measure;
import it.polito.ai.models.MeasureSubmission;
import it.polito.ai.repositories.ArchiveRepo;
import it.polito.ai.repositories.MeasureRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ArchiveService {
    private static final int SPEED_THRESHOLD = 100;
    private static final int PRICE_PER_POSITION = 1;

    @Autowired
    private MeasureRepo measureRepo;

    @Autowired
    private ArchiveRepo archiveRepo;

    // Check that submitted positions are valid
    protected void checkPositions(String username, List<MeasureSubmission> positions) throws InvalidPositionException{
        Optional<Measure> last = measureRepo.findTopByOrderByTimestampByUsername(username);
        long previous_timestamp = 0;
        if(last.isPresent()){
            previous_timestamp = last.get().getTimestamp();
        }

        // check that all the positions are valid
        MeasureSubmission previous = null;
        int i = 0;
        for(MeasureSubmission current: positions){
            if (!current.checkIfValid() || previous_timestamp > 0 && current.getTimestamp() < previous_timestamp || previous != null && current.getSpeed(previous) > SPEED_THRESHOLD) {
                throw new InvalidPositionException("Position " + (i + 1) + " is not valid");
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
        a.setMeasures(positionEntries.size());
        archiveRepo.save(a);

        List<Measure> measures = new LinkedList<>();
        for (MeasureSubmission p: positionEntries) {
            // Round timestamp to last minute
            //long roundedTimestamp = p.getTimestamp() - p.getTimestamp() % 60;
            // Round coordinates to second decimal digit
            //Double lat = Math.round(p.getLatitude() * 100.0) / 100.0;
            //Double lng = Math.round(p.getLongitude() * 100.0) / 100.0;
            Measure m = new Measure();
            m.setArchiveId(a.getId());
            m.setUsername(username);
            m.setTimestamp(p.getTimestamp());
            m.setPosition(new GeoJsonPoint(p.getLongitude(), p.getLatitude()));
            m.setDeleted(false);
            measureRepo.save(m);
            measures.add(m);
        }

        return a;
    }

    // Retrieve all archives uploaded by username
    public List<Archive> getArchivesByUsername(String username) throws UserHasNoArchivesException, MeasuresNotFoundException {
        Optional<List<Archive>> archives =archiveRepo.findAllByUsername(username);
        if (!archives.isPresent())
            throw new UserHasNoArchivesException(username);
        return archives.get();
    }

    public Archive getUserArchive(String username, String archiveId) throws ArchiveNotFoundException{
        Optional<Archive> a = archiveRepo.findByUsernameAndId(username, archiveId);
        if(!a.isPresent())
            throw new ArchiveNotFoundException("Archive " + archiveId + " not found!");
        return a.get();
    }

    public Archive getArchive(String archiveId) throws ArchiveNotFoundException{
        Optional<Archive> a = archiveRepo.findById(archiveId);
        if(!a.isPresent())
            throw new ArchiveNotFoundException("Archive " + archiveId + " not found!");
        return a.get();
    }

    // Set the deleted flag on the measures belonging to the archive with specified id
    @Transactional
    public void deleteArchive(String username, String archiveId) throws MeasuresNotFoundException, ArchiveNotFoundException {
        Optional<Archive> archive = archiveRepo.findByUsernameAndId(username, archiveId);
        if(!archive.isPresent())
            throw new ArchiveNotFoundException(archiveId);
        archive.get().setDeleted(true);
        archiveRepo.save(archive.get());

        Optional<List<Measure>> measures = measureRepo.findAllByArchiveId(archiveId);
        if(!measures.isPresent())
            throw new MeasuresNotFoundException(archiveId);
        for(Measure m : measures.get()){
            m.setDeleted(true);
            measureRepo.save(m);
        }
    }

    /*
    * Get the downloadable archive resource
    * */
    public ArchiveDownload downloadArchive(Archive a) throws MeasuresNotFoundException, ArchiveNotFoundException {
        Map<Long, GeoJsonPoint> measureDict = new HashMap<>();
        Optional<List<Measure>> measures = measureRepo.findAllByArchiveId(a.getId());
        if(!measures.isPresent())
            throw new MeasuresNotFoundException(a.getId());
        for(Measure m : measures.get()){
            measureDict.put(m.getTimestamp(), m.getPosition());
        }

        ArchiveDownload res = new ArchiveDownload();
        res.setMeasures(measureDict);
        res.setId(a.getId());
        res.setUsername(a.getUsername());
        return res;
    }
}
