package it.polito.ai.repositories;

import it.polito.ai.models.Account;
import it.polito.ai.models.Archive;
import it.polito.ai.models.Position;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ArchiveRepo extends Repository<Archive, String> {

    List<Archive> findAllByUserIdEquals(String userId);
    Optional<Archive> findById(String id);
    Optional<Archive> findByIdAndUserIdEquals(String archiveId, String userId);
    List<Archive> findAllByUserIdEqualsAndTimestampAfter(String userId, long from);
    List<Archive> findAllByUserIdEqualsAndTimestampBefore(String userId, long to);
    List<Archive> findAllByUserIdEqualsAndTimestampBetween(String userId, long from, long to);
    List<Archive> findAllByTimestampBetween(long from, long to);

    Archive save(Archive position);

    void deleteArchiveById(String id);
}
