package edu.tamu.app.model;

public class Member {

    private final String name;

    private final String avatarUrl;

    public Member(String name, String avatarUrl) {
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

}
