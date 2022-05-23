package edu.tamu.app.cache.service;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.tamu.app.cache.ActiveSprintsCache;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBean;
import edu.tamu.weaver.response.ApiResponse;

@Service
public class ActiveSprintsScheduledCacheService extends AbstractProductScheduledCacheService<List<Sprint>, ActiveSprintsCache> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProductRepo productRepo;

    public ActiveSprintsScheduledCacheService() {
        super(new ActiveSprintsCache());
    }

    @Override
    @Scheduled(initialDelayString = "${app.cache.active-sprints.delay}", fixedDelayString = "${app.cache.active-sprints.interval}")
    public void schedule() {
        super.schedule();
    }

    public void update() {
        logger.info("Caching active sprints...");
        List<Sprint> activeSprints = new ArrayList<Sprint>();
        productRepo.findAll().forEach(product -> {
            activeSprints.addAll(fetchActiveSprints(product));
        });
        for (ManagementBean managementBean : managementBeanRegistry.getServices().values()) {
            RemoteProjectManagerBean rpm = (RemoteProjectManagerBean) managementBean;
            try {
                activeSprints.addAll(rpm.getAdditionalActiveSprints());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        set(activeSprints);
        logger.info("Finished caching active sprints");
    }

    public void broadcast() {
        logger.info("Broadcasting cached active sprints");
        simpMessagingTemplate.convertAndSend("/channel/sprints/active", new ApiResponse(SUCCESS, get()));
    }

    public void addProduct(Product product) {
        List<Sprint> activeSprints = get();
        activeSprints.addAll(fetchActiveSprints(product));
        set(activeSprints);
        broadcast();
    }

    public void updateProduct(Product product) {
        List<Sprint> activeSprints = get().stream().filter(as -> !as.getProduct().equals(product.getName()))
                .collect(Collectors.toList());
        activeSprints.addAll(fetchActiveSprints(product));
        set(activeSprints);
        broadcast();
    }

    public void removeProduct(Product product) {
        List<Sprint> activeSprints = get().stream().filter(p -> !p.getProduct().equals(product.getName()))
                .collect(Collectors.toList());
        set(activeSprints);
        broadcast();
    }

    private List<Sprint> fetchActiveSprints(Product product) {
        List<Sprint> activeSprints = new ArrayList<Sprint>();
        Optional<List<RemoteProjectInfo>> remoteProjectInfo = Optional.ofNullable(product.getRemoteProjectInfo());
        if (remoteProjectInfo.isPresent()) {
            remoteProjectInfo.get().forEach(rp -> {
                RemoteProjectManagerBean remoteProjectManagerBean = (RemoteProjectManagerBean) managementBeanRegistry.getService(rp.getRemoteProjectManager().getName());
                try {
                    activeSprints.addAll(remoteProjectManagerBean.getActiveSprintsByScopeId(rp.getScopeId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return activeSprints;
    }

    @Override
    public int getOrder() {
        return 3;
    }

}
