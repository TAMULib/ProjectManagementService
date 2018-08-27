package edu.tamu.app.cache.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Card implements Serializable {

    private static final long serialVersionUID = 5692495387386239114L;

    private final String id;

    private final String number;

    private final String type;

    private final String status;

    private final String name;

    @JsonInclude(Include.NON_NULL)
    private final String description;

    @JsonInclude(Include.NON_NULL)
    private final Float estimate;

    private final List<Member> assignees;

    public Card() {
        super();
        this.id = "";
        this.number = "";
        this.type = "";
        this.name = "";
        this.description = "";
        this.status = "";
        this.estimate = null;
        this.assignees = new ArrayList<Member>();
    }

    public Card(String id, String number, String type, String name, String description, String status, Float estimate, List<Member> assignees) {
        super();
        this.id = id;
        this.number = number;
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
        this.estimate = estimate;
        this.assignees = assignees;
    }

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Float getEstimate() {
        return estimate;
    }

    public List<Member> getAssignees() {
        return assignees;
    }

}
