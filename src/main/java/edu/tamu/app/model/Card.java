package edu.tamu.app.model;

import java.util.List;

public class Card {

    // Identifier for the card
    private String name;

    // Title displayed above the card body
    private String title;

    private String body;

    private List<Assignee> assignees;

    private Status status;

    private CardType cardType;

    public Card(String name, String title, String body, List<Assignee> assignees, Status status, CardType cardType) {
        setName(name);
        setTitle(title);
        setBody(body);
        setAssignees(assignees);
        setStatus(status);
        setCardType(cardType);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Assignee> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<Assignee> assignees) {
        this.assignees = assignees;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

}
