package edu.tamu.app.service.registry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import com.versionone.apiclient.exceptions.V1Exception;

import edu.tamu.app.model.ManagementService;
import edu.tamu.app.service.manager.GitHubMilestoneService;
import edu.tamu.app.service.manager.GitHubProjectService;
import edu.tamu.app.service.manager.VersionOneService;

@Service
public class ManagementBeanRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ManagementBeanRegistry.class);

    private static final Map<String, ManagementBean> services = new HashMap<String, ManagementBean>();

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    public void register(ManagementService managementService) {
        String serviceName = managementService.getName();

        logger.info("Registering service: " + serviceName);

        Optional<ManagementBean> service = Optional.empty();

        try {

            switch (managementService.getType()) {
            case VERSION_ONE:
                service = Optional.of((ManagementBean) new VersionOneService(managementService));
                break;
            case GITHUB_PROJECT:
                service = Optional.of((ManagementBean) new GitHubProjectService(managementService));
                break;
            case GITHUB_MILESTONE:
                service = Optional.of((ManagementBean) new GitHubMilestoneService(managementService));
                break;
            default:
                break;
            }

        } catch (IOException | V1Exception e) {
            e.printStackTrace();
        }

        if (service.isPresent()) {
            beanFactory.autowireBean(service.get());
            services.put(serviceName, service.get());
        } else {
            logger.info("Service was not instantiated!");
        }
    }

    public void unregister(ManagementService managementService) {
        String serviceName = managementService.getName();

        logger.info("Unregistering service: " + serviceName);

        Optional<ManagementBean> service = Optional.ofNullable(services.get(serviceName));

        if (service.isPresent()) {
            beanFactory.destroyBean(service.get());
            services.remove(serviceName);
        }
    }

    public ManagementBean getService(String name) {
        return services.get(name);
    }

    public Map<String, ManagementBean> getServices() {
        return services;
    }

}
