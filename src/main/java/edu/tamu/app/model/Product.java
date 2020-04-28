package edu.tamu.app.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.app.model.validation.ProductValidator;
import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class Product extends ValidatingBaseEntity {

    @Column(unique = true, nullable = false)
    @JsonView(ApiView.Partial.class)
    private String name;

    @JsonInclude(Include.NON_NULL)
    @Column(nullable = true)
    @JsonView(ApiView.Partial.class)
    private String scopeId;

    @Column(nullable = true)
    @JsonInclude(Include.NON_NULL)
    @JsonView(ApiView.Partial.class)
    private String devUrl;

    @Column(nullable = true)
    @JsonInclude(Include.NON_NULL)
    @JsonView(ApiView.Partial.class)
    private String preUrl;

    @Column(nullable = true)
    @JsonInclude(Include.NON_NULL)
    @JsonView(ApiView.Partial.class)
    private String productionUrl;

    @Column(nullable = true)
    @JsonInclude(Include.NON_NULL)
    @JsonView(ApiView.Partial.class)
    private String wikiUrl;

    @JsonInclude(Include.NON_NULL)
    @ManyToOne(fetch = EAGER, cascade = { DETACH, REFRESH, MERGE }, optional = true)
    @JsonView(ApiView.Partial.class)
    private RemoteProductManager remoteProductManager;

    public Product() {
        super();
        this.modelValidator = new ProductValidator();
    }

    public Product(String name) {
        this();
        this.name = name;
    }

    public Product(String name, RemoteProductManager remoteProductManager) {
        this(name);
        this.remoteProductManager = remoteProductManager;
    }

    public Product(String name, String scopeId, RemoteProductManager remoteProductManager) {
        this(name, remoteProductManager);
        this.scopeId = scopeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public RemoteProductManager getRemoteProductManager() {
        return remoteProductManager;
    }

    public void setRemoteProductManager(RemoteProductManager remoteProductManager) {
        this.remoteProductManager = remoteProductManager;
    }

}
