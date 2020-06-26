package it.polito.ai.repositories;

import it.polito.ai.models.archive.Archive;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ArchiveRepo  extends Repository<Archive, String> {
    public Archive save(Archive a);
    public List<Archive> deleteAllByUsername(String username);
    public Archive findById(String id);
    //public Archive findTop1ByLastTimestampAndUsername(String username);
    public Archive findFirstByUsernameOrderByLastTimestampDesc(String username);
    public Optional<Archive> findByUsernameAndId(String username, String id);
    public List<Archive> findAllByIdIn(List<String> id);
    public List<Archive> findAllByUsername(String username);
}
