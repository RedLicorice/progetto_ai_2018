package it.polito.ai.repositories;

import it.polito.ai.models.Archive;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ArchiveRepo  extends Repository<Archive, String> {
    public Archive save(Archive a);
    public Optional<Archive> findById(String id);
    public Optional<List<Archive>> findAllByIdIn(List<String> id);
    public Optional<List<Archive>> findAllByUsername(String username);
}
