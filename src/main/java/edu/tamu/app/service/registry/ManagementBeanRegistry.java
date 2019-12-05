package edu.tamu.app.service.registry;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import com.versionone.apiclient.exceptions.V1Exception;

import edu.tamu.app.model.ManagementService;
import edu.tamu.app.service.manager.GitHubService;
import edu.tamu.app.service.manager.VersionOneService;

@Service
public class ManagementBeanRegistry {

    private static final Logger logger = Logger.getLogger(ManagementBeanRegistry.class);

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
            case GITHUB:
                service = Optional.of((ManagementBean) new GitHubService(managementService));
            default:
                break;
            }

        } catch (MalformedURLException | V1Exception e) {
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

}
