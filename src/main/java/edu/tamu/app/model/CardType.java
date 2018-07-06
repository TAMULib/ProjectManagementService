package edu.tamu.app.model;

import java.util.Set;

import javax.persistence.Entity;

@Entity
public class CardType extends ServiceMapping<String> {

    public CardType() {
        super();
    }

    public CardType(String identifier, Set<String> matches) {
        super(identifier, matches);
    }

}
