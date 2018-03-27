package edu.tamu.app.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;

import edu.tamu.app.model.converter.CryptoConverter;
import edu.tamu.weaver.data.model.BaseEntity;

@Entity
public class ManagementSetting extends BaseEntity {

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    @Convert(converter = CryptoConverter.class)
    private String value;

    public ManagementSetting() {
        super();
    }

    public ManagementSetting(String key) {
        this();
        this.key = key;
    }

    public ManagementSetting(String key, String value) {
        this(key);
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}