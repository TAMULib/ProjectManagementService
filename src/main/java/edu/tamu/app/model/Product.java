package edu.tamu.app.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.swing.RepaintManager;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.app.model.validation.ProductValidator;
import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class Product extends ValidatingBaseEntity {

    @Column(unique = true, nullable = false)
    @JsonView(ApiView.Partial.class)
    private String name;

    // @JsonInclude(Include.NON_NULL)
    // @Column(nullable = true)
    // @JsonView(ApiView.Partial.class)
    // private String scopeId;

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

    // @JsonInclude(Include.NON_NULL)
    // @ManyToOne(fetch = EAGER, cascade = { DETACH, REFRESH, MERGE }, optional = true)
    // @JsonView(ApiView.Partial.class)
    // private RemoteProductManager remoteProductManager;

    @ElementCollection
    @JsonView(ApiView.Partial.class)
    private List<Pair<String, RemoteProductManager>> remoteProducts;

    public Product() {
        super();
        this.modelValidator = new ProductValidator();
    }

    public Product(String name) {
        this();
        this.name = name;
    }

    public Product(String name, List<Pair<String, UUID>> remoteProducts) {
        this();
        this.name = name;
    }

    // public Product(String name, RemoteProductManager remoteProductManager) {
    //     this(name);
    //     // this.remoteProductManager = remoteProductManager;
    // }

    // public Product(String name, String scopeId, RemoteProductManager remoteProductManager) {
    //     this(name, remoteProductManager);
    //     // this.scopeId = scopeId;
    // }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pair<String, RemoteProductManager>> getRemoteProducts() {
        return remoteProducts;
    }

    public void setRemoteProducts(List<Pair<String, RemoteProductManager>> remoteProducts) {
        this.remoteProducts = remoteProducts;
    }

    public void addRemoteProduct(Pair<String, RemoteProductManager> remoteProduct) {
        remoteProducts.add(remoteProduct);
    }

    public void remoteRemoteProduct(Pair<String, RemoteProductManager> remoteProduct) {
        remoteProducts = remoteProducts.stream()
            .filter(rp -> {
                return !rp.getLeft().equals(remoteProduct.getLeft()) && !rp.getRight().equals(remoteProduct.getRight());
            }).collect(Collectors.toList());
    }

    // public String getScopeId() {
    //     return scopeId;
    // }

    // public void setScopeId(String scopeId) {
    //     this.scopeId = scopeId;
    // }

    // public RemoteProductManager getRemoteProductManager() {
    //     return remoteProductManager;
    // }

    // public void setRemoteProductManager(RemoteProductManager remoteProductManager) {
    //     this.remoteProductManager = remoteProductManager;
    // }

}
