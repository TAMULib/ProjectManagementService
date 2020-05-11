package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.app.cache.model.RemoteProduct;
import edu.tamu.app.cache.service.ProductScheduledCache;
import edu.tamu.app.model.InternalRequest;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProductInfo;
import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProductManagerRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.app.service.manager.RemoteProductManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.ticketing.SugarService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private RemoteProductManagerRepo remoteProductManagerRepo;

    @Autowired
    private SugarService sugarService;

    @Autowired
    private List<ProductScheduledCache<?, ?>> productSceduledCaches;

    @Autowired
    private InternalRequestRepo internalRequestRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    @JsonView(ApiView.Partial.class)
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, productRepo.findAll());
    }

    @GetMapping("/{id}")
    @JsonView(ApiView.Partial.class)
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse getOne(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, productRepo.findOne(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createProduct(@WeaverValidatedModel Product product) {
        logger.info("Creating Product: " + product.getName());
        reifyProductRemoteProductManager(product);
        product = productRepo.create(product);
        for (ProductScheduledCache<?, ?> productSceduledCache : productSceduledCaches) {
            productSceduledCache.addProduct(product);
        }
        return new ApiResponse(SUCCESS, product);
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateProduct(@WeaverValidatedModel Product product) {
        logger.info("Updating Product: " + product.getName());
        reifyProductRemoteProductManager(product);
        product = productRepo.update(product);
        for (ProductScheduledCache<?, ?> productSceduledCache : productSceduledCaches) {
            productSceduledCache.updateProduct(product);
        }
        return new ApiResponse(SUCCESS, product);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse deleteProduct(@WeaverValidatedModel Product product) {
        logger.info("Deleting Product: " + product.getName());
        reifyProductRemoteProductManager(product);
        productRepo.delete(product);
        for (ProductScheduledCache<?, ?> productSceduledCache : productSceduledCaches) {
            productSceduledCache.removeProduct(product);
        }
        return new ApiResponse(SUCCESS);
    }

    @PostMapping("/issue")
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse submitIssueRequest(@RequestBody TicketRequest request) {
        return new ApiResponse(SUCCESS, sugarService.submit(request));
    }

    @PostMapping("/feature")
    @PreAuthorize("hasRole('MANAGER') or @whitelist.isAllowed()")
    public ApiResponse pushRequest(@RequestBody FeatureRequest request) {
        internalRequestRepo.create(new InternalRequest(request.getTitle(), request.getDescription()));
        return new ApiResponse(SUCCESS, request);
    }

    @GetMapping("/remote-products/{productId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllRemoteProductsForProduct(@PathVariable Long productId) {
        Optional<Product> product = Optional.ofNullable(productRepo.findOne(productId));
        ApiResponse response;

        if (product.isPresent()) {
            Map<String, RemoteProduct> remoteProducts = new HashMap<>();
            Map<String, RemoteProductManagerBean> rpmBeans = new HashMap<>();

            for (RemoteProductInfo rpi : product.get().getRemoteProducts()) {
                if (remoteProducts.containsKey(rpi.getScopeId())) {
                    continue;
                }

                RemoteProductManager rpm = rpi.getRemoteProductManager();
                RemoteProductManagerBean rpmBean;

                if (rpmBeans.containsKey(rpm.getName())) {
                    rpmBean = rpmBeans.get(rpm.getName());
                }
                else {
                    rpmBean = (RemoteProductManagerBean) managementBeanRegistry.getService(rpm.getName());
                }

                try {
                    RemoteProduct remoteProduct = rpmBean.getRemoteProductByScopeId(rpi.getScopeId());
                    remoteProducts.put(rpi.getScopeId(), remoteProduct);
                } catch (Exception e) {
                    response = new ApiResponse(ERROR, "Error fetching remote products associated with product " + product.get().getName() + "!");
                    return response;
                }
            }

            response = new ApiResponse(SUCCESS, remoteProducts);
        } else {
            response = new ApiResponse(ERROR, "Product with id " + productId + " not found!");
        }

        return response;
    }

    @GetMapping("/{remoteProductManagerId}/remote-products")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllRemoteProducts(@PathVariable Long remoteProductManagerId) {
        Optional<RemoteProductManager> remoteProductManager = Optional
                .ofNullable(remoteProductManagerRepo.findOne(remoteProductManagerId));
        ApiResponse response;
        if (remoteProductManager.isPresent()) {
            RemoteProductManagerBean remoteProductManagerBean = (RemoteProductManagerBean) managementBeanRegistry.getService(remoteProductManager.get().getName());
            try {
                response = new ApiResponse(SUCCESS, remoteProductManagerBean.getRemoteProduct());
            } catch (Exception e) {
                response = new ApiResponse(ERROR,
                        "Error fetching remote products from " + remoteProductManager.get().getName() + "!");
            }
        } else {
            response = new ApiResponse(ERROR, "Remote Product Manager with id " + remoteProductManagerId + " not found!");
        }
        return response;
    }

    @GetMapping("/{remoteProductManagerId}/remote-products/{scopeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getRemoteProductByScopeId(@PathVariable Long remoteProductManagerId, @PathVariable String scopeId) {
        Optional<RemoteProductManager> remoteProductManager = Optional
                .ofNullable(remoteProductManagerRepo.findOne(remoteProductManagerId));
        ApiResponse response;
        if (remoteProductManager.isPresent()) {
            RemoteProductManagerBean remoteProductManagerBean = (RemoteProductManagerBean) managementBeanRegistry.getService(remoteProductManager.get().getName());
            try {
                response = new ApiResponse(SUCCESS, remoteProductManagerBean.getRemoteProductByScopeId(scopeId));
            } catch (Exception e) {
                response = new ApiResponse(ERROR, "Error fetching remote product with scope id " + scopeId + " from " + remoteProductManager.get().getName() + "!");
            }
        } else {
            response = new ApiResponse(ERROR, "Remote Product Manager with id " + remoteProductManagerId + " not found!");
        }
        return response;
    }

    private void reifyProductRemoteProductManager(Product product) {
        Optional<List<RemoteProductInfo>> remoteProducts = Optional.ofNullable(product.getRemoteProducts());

        if (remoteProducts.isPresent()) {
            for (int i = 0; i < product.getRemoteProducts().size(); i++) {
                Optional<RemoteProductManager> remoteProductManager = Optional.ofNullable(remoteProducts.get().get(i).getRemoteProductManager());
                if (remoteProductManager.isPresent()) {
                    Long remoteProductManagerId = remoteProductManager.get().getId();
                    RemoteProductInfo remoteProduct = new RemoteProductInfo(remoteProducts.get().get(i).getScopeId(), remoteProductManagerRepo.findOne(remoteProductManagerId));
                    remoteProducts.get().set(i, remoteProduct);
                }
            }
            product.setRemoteProductInfo(remoteProducts.get());
        }
    }

}
