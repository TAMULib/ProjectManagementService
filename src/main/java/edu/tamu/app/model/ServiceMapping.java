package edu.tamu.app.model;

import static javax.persistence.FetchType.EAGER;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;

import edu.tamu.app.model.validation.StatusValidator;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@MappedSuperclass
public abstract class ServiceMapping<I, M> extends ValidatingBaseEntity {

    @Column(unique = true)
    private I identifier;

    @ElementCollection(fetch = EAGER)
    protected Set<M> mapping;

    public ServiceMapping() {
        super();
        mapping = new HashSet<M>();
        modelValidator = new StatusValidator();
    }

    public ServiceMapping(I identifier, Set<M> mapping) {
        this();
        this.identifier = identifier;
        this.mapping = mapping;
    }

    public I getIdentifier() {
        return identifier;
    }

    public void setIdentifier(I identifier) {
        this.identifier = identifier;
    }

    public Set<M> getMapping() {
        return mapping;
    }

    public void setMapping(Set<M> mapping) {
        this.mapping = mapping;
    }

}
