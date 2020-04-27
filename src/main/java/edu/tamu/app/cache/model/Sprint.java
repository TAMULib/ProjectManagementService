package edu.tamu.app.cache.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sprint implements Serializable {

    private static final long serialVersionUID = -1490373427353614907L;

    private final String id;

    private final String name;

    private final String product;

    private final List<Card> cards;

    public Sprint() {
        super();
        this.id = "";
        this.name = "";
        this.product = "";
        this.cards = new ArrayList<Card>();
    }

    public Sprint(String id, String name, String product, List<Card> cards) {
        super();
        this.id = id;
        this.name = name;
        this.product = product;
        this.cards = cards;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProduct() {
        return product;
    }

    public List<Card> getCards() {
        return cards;
    }

}
