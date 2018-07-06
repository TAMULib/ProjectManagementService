package edu.tamu.app.mapping;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.ServiceMapping;
import edu.tamu.app.model.repo.ServiceMappingRepo;

public abstract class AbstractRepoMappingService<I, M, T extends ServiceMapping<I, M>, R extends ServiceMappingRepo<I, M, T>> implements MappingService<I, M> {

    @Autowired
    protected R serviceMappingRepo;

    protected abstract I handleUnmapped(M mapping);

    public I map(M rawData) {
        Optional<T> mappedIdentifier = rawData != null ? serviceMappingRepo.findByMapping(rawData) : Optional.empty();
        return mappedIdentifier.isPresent() ? mappedIdentifier.get().getIdentifier() : handleUnmapped(rawData);
    }

}
