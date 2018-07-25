package edu.tamu.app.model.repo;

import java.util.Optional;

import edu.tamu.app.model.ServiceMapping;

public interface ServiceMappingRepo<I, M, T extends ServiceMapping<I, M>> {

    public Optional<T> findByMapping(M match);

    public T findByIdentifier(I identifier);

}
