package edu.tamu.app.model;

import java.util.Set;

import javax.persistence.Entity;

@Entity
public class Status extends ServiceMapping<String> {

    public Status() {
        super();
    }

    public Status(String identifier, Set<String> matches) {
        super(identifier, matches);
    }

}
