package edu.tamu.app.model;

public class Member {

    private String name;

    public Member(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
