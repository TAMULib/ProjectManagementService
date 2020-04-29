package edu.tamu.app.cache.service;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.tamu.app.cache.ActiveSprintsCache;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.service.manager.RemoteProductManagerBean;
import edu.tamu.weaver.response.ApiResponse;

@Service
public class ActiveSprintsScheduledCacheService extends AbstractProductScheduledCacheService<List<Sprint>, ActiveSprintsCache> {

    private static final Logger logger = Logger.getLogger(ActiveSprintsScheduledCacheService.class);

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
        Optional<List<Pair<String, RemoteProductManager>>> remoteProducts = Optional.ofNullable(product.getRemoteProducts());
        if (remoteProducts.isPresent()) {
            remoteProducts.get().forEach(rp -> {
                RemoteProductManagerBean remoteProductManagerBean = (RemoteProductManagerBean) managementBeanRegistry.getService(rp.getRight().getName());
                try {
                    activeSprints.addAll(remoteProductManagerBean.getActiveSprintsByProductId(rp.getLeft()));
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
