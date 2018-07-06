package edu.tamu.app.model.repo;

import java.util.Optional;

import edu.tamu.app.model.ServiceMapping;

public interface ServiceMappingRepo<C, T extends ServiceMapping<C>> {

    public Optional<T> findByMapping(String match);

    public T findByIdentifier(C identifier);

}
