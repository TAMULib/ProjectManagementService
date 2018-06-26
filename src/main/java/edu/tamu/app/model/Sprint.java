package edu.tamu.app.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Sprint {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    private String projectName;

    private List<Card> cards;

    public Sprint(String name, String projectName, List<Card> cards) {
        setName(name);
        setProjectName(projectName);
        setCards(cards);
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
