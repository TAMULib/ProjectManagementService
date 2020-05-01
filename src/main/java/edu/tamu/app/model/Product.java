package edu.tamu.app.model;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonView;

import org.apache.commons.lang3.tuple.Pair;

import edu.tamu.app.model.validation.ProductValidator;
import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class Product extends ValidatingBaseEntity {

    @Column(unique = true, nullable = false)
    @JsonView(ApiView.Partial.class)
    private String name;

    @ElementCollection
    @JsonView(ApiView.Partial.class)
    private List<RemoteProductInfo> remoteProducts;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RemoteProductInfo> getRemoteProducts() {
        return remoteProducts;
    }

    public void setRemoteProducts(List<RemoteProductInfo> remoteProducts) {
        this.remoteProducts = remoteProducts;
    }

    public void addRemoteProduct(RemoteProductInfo remoteProduct) {
        remoteProducts.add(remoteProduct);
    }

    public void removeRemoteProduct(RemoteProductInfo remoteProduct) {
        remoteProducts = remoteProducts.stream()
            .filter(rp -> {
                return !rp.getScopeId().equals(remoteProduct.getScopeId()) && !rp.getRemoteProductManager().equals(remoteProduct.getRemoteProductManager());
            }).collect(Collectors.toList());
    }
}
