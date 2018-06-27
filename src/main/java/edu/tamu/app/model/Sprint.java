package edu.tamu.app.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
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
    
    public Sprint(String name, String projectName) {
        setName(name);
        setProjectName(projectName);
        this.cards = new ArrayList<Card>();
    }

    public Sprint(String name, String projectName, List<Card> cards) {
        this(name, projectName);
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
