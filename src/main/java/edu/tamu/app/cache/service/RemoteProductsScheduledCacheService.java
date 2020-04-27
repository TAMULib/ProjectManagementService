package edu.tamu.app.cache.service;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.tamu.app.cache.RemoteProductsCache;
import edu.tamu.app.cache.model.RemoteProduct;
import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.repo.RemoteProductManagerRepo;
import edu.tamu.app.service.manager.RemoteProductManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.response.ApiResponse;

@Service
public class RemoteProductsScheduledCacheService extends AbstractScheduledCacheService<Map<Long, List<RemoteProduct>>, RemoteProductsCache> {

    private static final Logger logger = Logger.getLogger(RemoteProductsScheduledCacheService.class);

    @Autowired
    private RemoteProductManagerRepo remoteProductManagerRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    public RemoteProductsScheduledCacheService() {
        super(new RemoteProductsCache());
    }

    @Override
    @Scheduled(initialDelayString = "${app.cache.remote-products.delay}", fixedDelayString = "${app.cache.remote-products.interval}")
    public void schedule() {
        super.schedule();
    }

    public void update() {
        logger.info("Caching remote products...");
        Map<Long, List<RemoteProduct>> remoteproducts = new HashMap<Long, List<RemoteProduct>>();
        for (RemoteProductManager remoteproductManager : remoteProductManagerRepo.findAll()) {
            RemoteProductManagerBean remoteproductManagerBean = (RemoteProductManagerBean) managementBeanRegistry
                    .getService(remoteproductManager.getName());
            try {
                remoteproducts.put(remoteproductManager.getId(), remoteproductManagerBean.getRemoteProduct());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        set(remoteproducts);
        logger.info("Finished caching remote products");
    }

    public void broadcast() {
        logger.info("Broadcasting cached remote products");
        simpMessagingTemplate.convertAndSend("/channel/products/remote", new ApiResponse(SUCCESS, get()));
    }

    public Optional<RemoteProduct> getRemoteProduct(Long remoteproductManagerId, String scopeId) {
        Optional<RemoteProduct> remoteproduct = Optional.empty();
        Optional<List<RemoteProduct>> remoteproducts = Optional.ofNullable(get().get(remoteproductManagerId));
        if (remoteproducts.isPresent()) {
            for (RemoteProduct rp : remoteproducts.get()) {
                if (rp.getId().equals(scopeId)) {
                    remoteproduct = Optional.of(rp);
                    break;
                }
            }
        }
        return remoteproduct;
    }

    @Override
    public int getOrder() {
        return 1;
    }

}
