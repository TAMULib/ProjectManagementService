package edu.tamu.app.mapping;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.ServiceMapping;
import edu.tamu.app.model.repo.ServiceMappingRepo;

public abstract class AbstractMappingService<C, T extends ServiceMapping<C>, R extends ServiceMappingRepo<C, T>> implements MappingService<C, T> {

    @Autowired
    protected R serviceMappingRepo;

    public C map(String rawData) {
        Optional<T> mappedIdentifier = rawData != null ? serviceMappingRepo.findByMapping(rawData) : Optional.empty();
        return mappedIdentifier.isPresent() ? mappedIdentifier.get().getIdentifier() : handleUnmapped(rawData);
    }

}
