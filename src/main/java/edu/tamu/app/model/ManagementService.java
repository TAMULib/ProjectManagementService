package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.validation.ManagementServiceValidator;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@MappedSuperclass
public abstract class ManagementService extends ValidatingBaseEntity {

    @Column
    protected String name;

    @Enumerated
    protected ServiceType type;

    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE }, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    protected List<ManagementSetting> settings;

    public ManagementService() {
        super();
        this.modelValidator = new ManagementServiceValidator();
        settings = new ArrayList<ManagementSetting>();
    }

    public ManagementService(String name, ServiceType type) {
        this();
        this.name = name;
        this.type = type;
    }

    public ManagementService(String name, ServiceType type, List<ManagementSetting> settings) {
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

    public List<ManagementSetting> getSettings() {
        return settings;
    }

    public void setSettings(List<ManagementSetting> settings) {
        this.settings = settings;
    }

    public List<String> getSettingValues(String key) {
        List<String> targetSetting = null;
        for (ManagementSetting setting : settings) {
            if (setting.getKey().equals(key)) {
                targetSetting = setting.getValues();
                break;
            }
        }
        return targetSetting;
    }

}
