package edu.tamu.app.model;

import javax.persistence.Column;

import edu.tamu.app.model.validation.ProjectValidator;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

public class Project extends ValidatingBaseEntity  {

    @Column(unique = true)
    private String name;
    
    public Project() {
        setModelValidator(new ProjectValidator());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
