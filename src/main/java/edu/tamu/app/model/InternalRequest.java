package edu.tamu.app.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.app.model.validation.InternalRequestValidator;
import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class InternalRequest extends ValidatingBaseEntity {

    @NotNull
    @Column(nullable = false)
    @JsonView(ApiView.Partial.class)
    private String title;

    @NotNull
    @Column(nullable = false)
    @JsonView(ApiView.Partial.class)
    private String description;

    @NotNull
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdOn;

    public InternalRequest() {
        super();
        this.modelValidator = new InternalRequestValidator();
    }

    public InternalRequest(String title, String description) {
        this.title = title;
        this.description = description;
        this.createdOn = new Date();
    }

    public InternalRequest(String title, String description, Date createdOn) {
        this.title = title;
        this.description = description;
        this.createdOn = createdOn;
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

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date created) {
        this.createdOn = created;
    }

}
