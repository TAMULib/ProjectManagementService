package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.List;

public class Sprint {

    private String identifier;

    private String name;

    private String projectName;

    private List<Card> cards;

    public Sprint(String identifier, String name, String projectName) {
        setIdentifier(identifier);
        setName(name);
        setProjectName(projectName);
        this.cards = new ArrayList<Card>();
    }

    public Sprint(String identifier, String name, String projectName, List<Card> cards) {
        this(identifier, name, projectName);
        setCards(cards);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

}
