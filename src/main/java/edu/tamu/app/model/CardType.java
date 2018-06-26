package edu.tamu.app.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class CardType {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    public CardType(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
