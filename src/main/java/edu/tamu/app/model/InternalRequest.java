package edu.tamu.app.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.app.model.validation.InternalRequestValidator;
import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class InternalRequest extends ValidatingBaseEntity {

    @NonNull
    @Column(nullable = false)
    @JsonView(ApiView.Partial.class)
    private String title;

    @NonNull
    @Column(nullable = false)
    @JsonView(ApiView.Partial.class)
    private String description;

    @JsonView(ApiView.Partial.class)
    @ManyToOne(targetEntity = Product.class, fetch = EAGER, cascade = { DETACH, REFRESH, MERGE }, optional = true)
    private Product product;

    @NonNull
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdOn;

    public InternalRequest() {
        super();
        this.modelValidator = new InternalRequestValidator();
    }

    public InternalRequest(String title, String description, Product product, Date createdOn) {
        this.title = title;
        this.description = description;
        this.product = product;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date created) {
        this.createdOn = created;
    }

}
