package edu.tamu.app.model;

import static javax.persistence.FetchType.EAGER;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.tamu.app.model.validation.StatusValidator;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@MappedSuperclass
public abstract class ServiceMapping<C> extends ValidatingBaseEntity {

    @Column(unique = true)
    private C identifier;

    @ElementCollection(fetch = EAGER)
    protected Set<String> mapping;

    public ServiceMapping() {
        super();
        mapping = new HashSet<String>();
        modelValidator = new StatusValidator();
    }

    public ServiceMapping(C identifier, Set<String> mapping) {
        this();
        this.identifier = identifier;
        this.mapping = mapping;
    }

    public C getIdentifier() {
        return identifier;
    }

    public void setIdentifier(C identifier) {
        this.identifier = identifier;
    }

    @JsonIgnore
    public Set<String> getMapping() {
        return mapping;
    }

    public void setMapping(Set<String> mapping) {
        this.mapping = mapping;
    }

}
