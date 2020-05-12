package edu.tamu.app.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

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

    @Column(nullable = true)
    @JsonInclude(Include.NON_NULL)
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

    @ElementCollection
    @JsonView(ApiView.Partial.class)
    private List<RemoteProductInfo> remoteProductInfo;

    public Product() {
        super();
        this.modelValidator = new ProductValidator();
    }

    public Product(String name) {
        this();
        this.name = name;
    }

    public Product(String name, List<RemoteProductInfo> remoteProducts) {
        this();
        this.name = name;
        this.remoteProductInfo = remoteProducts;
    }

    public Product(String name, List<RemoteProductInfo> remoteProducts, String scopeId, String devUrl, String preUrl, String productionUrl, String wikiUrl) {
        this();
        this.name = name;
        this.remoteProductInfo = remoteProducts;
        this.scopeId = scopeId;
        this.devUrl = devUrl;
        this.preUrl = preUrl;
        this.productionUrl = productionUrl;
        this.wikiUrl = wikiUrl;
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

    public String getDevUrl() {
        return devUrl;
    }

    public void setDevUrl(String devUrl) {
        this.devUrl = devUrl;
    }

    public String getPreUrl() {
        return preUrl;
    }

    public void setPreUrl(String preUrl) {
        this.preUrl = preUrl;
    }

    public String getProductionUrl() {
        return productionUrl;
    }

    public void setProductionUrl(String productionUrl) {
        this.productionUrl = productionUrl;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    public List<RemoteProductInfo> getRemoteProductInfo() {
        return remoteProductInfo;
    }

    public void setRemoteProductInfo(List<RemoteProductInfo> remoteProductInfo) {
        this.remoteProductInfo = remoteProductInfo;
    }

    public void addRemoteProductInfo(RemoteProductInfo remoteProductInfo) {
        this.remoteProductInfo.add(remoteProductInfo);
    }

    public void removeRemoteProduct(RemoteProductInfo remoteProductInfo) {
        this.remoteProductInfo = this.remoteProductInfo.stream()
            .filter(rp -> {
                return !rp.getScopeId().equals(remoteProductInfo.getScopeId()) && !rp.getRemoteProductManager().equals(remoteProductInfo.getRemoteProductManager());
            }).collect(Collectors.toList());
    }
}
