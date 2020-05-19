package edu.tamu.app.cache.service;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.tamu.app.cache.ProductsStatsCache;
import edu.tamu.app.cache.model.ProductStats;
import edu.tamu.app.cache.model.RemoteProduct;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProductInfo;
import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.weaver.response.ApiResponse;

@Service
public class ProductsStatsScheduledCacheService extends AbstractProductScheduledCacheService<List<ProductStats>, ProductsStatsCache> {

    private static final Logger logger = Logger.getLogger(ProductsStatsScheduledCacheService.class);

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private InternalRequestRepo internalRequestRepo;

    @Autowired
    private RemoteProductsScheduledCacheService remoteProductsScheduledCacheService;

    public ProductsStatsScheduledCacheService() {
        super(new ProductsStatsCache());
    }

    @Override
    @Scheduled(initialDelayString = "${app.cache.products-stats.delay}", fixedDelayString = "${app.cache.products-stats.interval}")
    public void schedule() {
        super.schedule();
    }

    public void update() {
        logger.info("Caching products stats...");
        List<ProductStats> productsStats = new ArrayList<ProductStats>();
        productRepo.findAll().forEach(product -> {
            productsStats.add(getProductStats(product));
        });
        set(productsStats);
        logger.info("Finished caching products stats");
    }

    public void broadcast() {
        logger.info("Broadcasting cached products stats");
        simpMessagingTemplate.convertAndSend("/channel/products/stats", new ApiResponse(SUCCESS, get()));
    }

    public void addProduct(Product product) {
        List<ProductStats> productsStats = get();
        productsStats.add(getProductStats(product));
        set(productsStats);
        broadcast();
    }

    public void updateProduct(Product product) {
        List<ProductStats> productsStats = get().stream().filter(p -> !p.getId().equals(product.getId().toString()))
                .collect(Collectors.toList());
        productsStats.add(getProductStats(product));
        set(productsStats);
        broadcast();
    }

    public void removeProduct(Product product) {
        List<ProductStats> productsStats = get().stream().filter(p -> !p.getId().equals(product.getId().toString()))
                .collect(Collectors.toList());
        set(productsStats);
        broadcast();
    }

    private ProductStats getProductStats(Product product) {
        String id = product.getId().toString();
        String name = product.getName();
        long requestCount = 0;
        long issueCount = 0;
        long featureCount = 0;
        long defectCount = 0;
        long internalCount = 0;

        Optional<Long> productId = Optional.ofNullable(product.getId());
        if (productId.isPresent()) {
            internalCount = internalRequestRepo.countByProductId(productId.get());
        }

        List<RemoteProductInfo> remoteProductInfo = product.getRemoteProductInfo();
        for (RemoteProductInfo rpi : remoteProductInfo) {
            Optional<RemoteProductManager> remoteProductManager = Optional.ofNullable(rpi.getRemoteProductManager());
            Optional<String> scopeId = Optional.ofNullable(rpi.getScopeId());
            if (remoteProductManager.isPresent() && scopeId.isPresent()) {
                Optional<RemoteProduct> remoteProduct = remoteProductsScheduledCacheService.getRemoteProduct(remoteProductManager.get().getId(), scopeId.get());
                if (remoteProduct.isPresent()) {
                    requestCount += remoteProduct.get().getRequestCount();
                    issueCount += remoteProduct.get().getIssueCount();
                    featureCount += remoteProduct.get().getFeatureCount();
                    defectCount += remoteProduct.get().getDefectCount();
                }
            }
        }

        return new ProductStats(id, name, requestCount, issueCount, featureCount, defectCount, internalCount);
    }

    @Override
    public int getOrder() {
        return 2;
    }

}
