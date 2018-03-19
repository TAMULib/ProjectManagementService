package edu.tamu.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import edu.tamu.app.model.validation.ProjectValidator;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class Project extends ValidatingBaseEntity  {

    @Column(unique = true, nullable = false)
    private String name;
    
    public Project() {
        setModelValidator(new ProjectValidator());
    }
    
    public Project(String name) {
        this();
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
