package edu.tamu.app.service.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.ManagementService;
import edu.tamu.app.service.versioning.VersionOneService;

@Service
public class ManagementBeanRegistry {

    private static final Logger logger = Logger.getLogger(ManagementBeanRegistry.class);

    private static final Map<String, ManagementBean> services = new HashMap<String, ManagementBean>();

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    public void register(ManagementService managementService) {

        logger.info("Registering service: " + managementService.getName());

        Optional<ManagementBean> service = Optional.empty();

        switch (managementService.getType()) {
        case VERSION_ONE:
            service = Optional.of((ManagementBean) new VersionOneService(managementService));
            break;
        default:
            break;
        }

        if (service.isPresent()) {
            beanFactory.autowireBean(service.get());
            services.put(managementService.getName(), service.get());
        } else {
            logger.info("Service was not instantiated!");
        }
    }

    public ManagementBean getService(String name) {
        return services.get(name);
    }

}
