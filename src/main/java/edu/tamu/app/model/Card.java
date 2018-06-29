package edu.tamu.app.model;

import java.util.List;

public class Card {

    // Identifier for the card
    private String name;

    // Title displayed above the card body
    private String title;

    private String body;

    private List<Member> members;

    private String status;

    private String cardType;

    public Card(String name, String title, String body, List<Member> members, String status, String cardType) {
        setName(name);
        setTitle(title);
        setBody(body);
        setMembers(members);
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

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

}
