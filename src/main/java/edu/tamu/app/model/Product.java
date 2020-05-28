package edu.tamu.app.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @JsonView(ApiView.Partial.class)
    private List<RemoteProjectInfo> remoteProjectInfo;

    public Product() {
        super();
        this.modelValidator = new ProductValidator();
    }

    public Product(String name) {
        this();
        this.name = name;
    }

    public Product(String name, List<RemoteProjectInfo> remoteProjectInfo) {
        this();
        this.name = name;
        this.remoteProjectInfo = remoteProjectInfo;
    }

    public Product(String name, List<RemoteProjectInfo> remoteProjectInfo, String scopeId, String devUrl, String preUrl, String productionUrl, String wikiUrl) {
        this();
        this.name = name;
        this.remoteProjectInfo = remoteProjectInfo;
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

    public List<RemoteProjectInfo> getRemoteProjectInfo() {
        return remoteProjectInfo;
    }

    public void setRemoteProductInfo(List<RemoteProjectInfo> remoteProjectInfo) {
        this.remoteProjectInfo = remoteProjectInfo;
    }

    public void addRemoteProductInfo(RemoteProjectInfo remoteProjectInfo) {
        this.remoteProjectInfo.add(remoteProjectInfo);
    }

    public void removeRemoteProduct(RemoteProjectInfo remoteProjectInfo) {
        this.remoteProjectInfo = this.remoteProjectInfo.stream()
            .filter(rp -> {
                return !rp.getScopeId().equals(remoteProjectInfo.getScopeId()) && !rp.getRemoteProjectManager().equals(remoteProjectInfo.getRemoteProjectManager());
            }).collect(Collectors.toList());
    }
}
