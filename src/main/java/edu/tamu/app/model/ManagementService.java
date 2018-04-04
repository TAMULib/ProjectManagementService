package edu.tamu.app.model;

import static javax.persistence.FetchType.EAGER;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.converter.CryptoConverter;
import edu.tamu.app.model.validation.ManagementServiceValidator;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@MappedSuperclass
public abstract class ManagementService extends ValidatingBaseEntity {

    @Column
    protected String name;

    @Enumerated
    protected ServiceType type;

    @ElementCollection(fetch = EAGER)
    @Fetch(FetchMode.SELECT)
    @Convert(attributeName = "value", converter = CryptoConverter.class)
    protected Map<String, String> settings;

    public ManagementService() {
        super();
        this.modelValidator = new ManagementServiceValidator();
        settings = new HashMap<String, String>();
    }

    public ManagementService(String name, ServiceType type) {
        this();
        this.name = name;
        this.type = type;
    }

    public ManagementService(String name, ServiceType type, Map<String, String> settings) {
        this(name, type);
        this.settings = settings;
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

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(HashMap<String, String> settings) {
        this.settings = settings;
    }

    public Optional<String> getSettingValue(String key) {
        Optional<String> targetSetting = Optional.empty();
        for (Map.Entry<String, String> setting : settings.entrySet()) {
            if (setting.getKey().equals(key)) {
                targetSetting = Optional.of(setting.getValue());
                break;
            }
        }
        return targetSetting;
    }

}
