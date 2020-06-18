package it.polito.ai.models.archive;

/*
* Different representations of the archive model
* */
public class ArchiveView {
    public interface Summary {}
    public interface OwnerSummary extends Summary {}
    public interface Resource extends Summary {}
    public interface PublicResource extends Summary {}
}
