package edu.tamu.app.model;

import java.util.List;

public class Sprint {

    private final String identifier;

    private final String name;

    private final String projectName;

    private final List<Card> cards;

    public Sprint(String identifier, String name, String projectName, List<Card> cards) {
        this.identifier = identifier;
        this.name = name;
        this.projectName = projectName;
        this.cards = cards;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public String getProjectName() {
        return projectName;
    }

    public List<Card> getCards() {
        return cards;
    }

}
