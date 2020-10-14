package edu.tamu.app.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.app.model.converter.CryptoConverter;
import edu.tamu.app.model.validation.ManagementServiceValidator;
import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@MappedSuperclass
public abstract class ManagementService extends ValidatingBaseEntity {

    @Column(nullable = false)
    @JsonView(ApiView.Partial.class)
    @JsonInclude(Include.NON_NULL)
    protected String name;

    @Enumerated
    @JsonView(ApiView.Partial.class)
    @JsonInclude(Include.NON_NULL)
    protected ServiceType type;

    @Column
    @JsonIgnore
    @JsonView(ApiView.Partial.class)
    protected String url;

    @Column
    @JsonIgnore
    @JsonView(ApiView.Partial.class)
    @Convert(converter = CryptoConverter.class)
    protected String token;

    public ManagementService() {
        super();
        modelValidator = new ManagementServiceValidator();
    }

    public ManagementService(String name, ServiceType type, String url, String token) {
        this();
        this.name = name;
        this.type = type;
        this.url = url;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
