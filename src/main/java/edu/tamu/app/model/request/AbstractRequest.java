package edu.tamu.app.model.request;

import java.io.Serializable;

public class AbstractRequest implements Serializable {

    private static final long serialVersionUID = 5685012937446553807L;

    private String title;

    private String description;

    public AbstractRequest() {
        super();
    }

    public AbstractRequest(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
