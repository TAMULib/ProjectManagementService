package edu.tamu.app.cache.model;

import java.io.Serializable;

public class Member implements Serializable {

    private static final long serialVersionUID = -4596979104519289942L;

    private final String id;

    private final String name;

    private final String avatar;

    public Member() {
        super();
        this.id = "";
        this.name = "";
        this.avatar = "";
    }

    public Member(String id, String name, String avatar) {
        super();
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

}
