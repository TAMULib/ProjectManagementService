package edu.tamu.app.service.versioning;

import edu.tamu.app.model.request.ProjectRequest;
import edu.tamu.app.service.registry.ManagementBean;

public interface VersionManagementSoftwareBean extends ManagementBean {
    
    public Object push(ProjectRequest request);

}
