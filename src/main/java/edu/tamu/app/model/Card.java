package edu.tamu.app.model;

import java.util.List;

public class Card {

    // Identifier for the card
    private final String name;

    // Title displayed above the card body
    private final String title;

    private final String body;

    private final String estimate;

    private final List<Member> members;

    private final String status;

    private final String cardType;

    public Card(String name, String title, String body, String estimate, List<Member> members, String status, String cardType) {
        this.name = name;
        this.title = title;
        this.body = body;
        this.estimate = estimate;
        this.members = members;
        this.status = status;
        this.cardType = cardType;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getEstimate() {
        return estimate;
    }

    public List<Member> getMembers() {
        return members;
    }

    public String getStatus() {
        return status;
    }

    public String getCardType() {
        return cardType;
    }

}
