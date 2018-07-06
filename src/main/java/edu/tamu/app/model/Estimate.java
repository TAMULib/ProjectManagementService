package edu.tamu.app.model;

import java.util.Set;

import javax.persistence.Entity;

@Entity
public class Estimate extends ServiceMapping<Float> {

    public Estimate() {
        super();
    }

    public Estimate(Float identifier, Set<String> matches) {
        super(identifier, matches);
    }

}
