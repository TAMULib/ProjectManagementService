package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;

import java.util.Date;
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

import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.service.ProductScheduledCache;
import edu.tamu.app.model.InternalRequest;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
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
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

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
        return new ApiResponse(SUCCESS, productRepo.findById(id).get());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createProduct(@WeaverValidatedModel Product product) {
        logger.info("Creating Product: " + product.getName());
        reifyProductRemoteProjectManager(product);
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
        reifyProductRemoteProjectManager(product);
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

        if (internalRequestRepo.countByProductId(product.getId()) > 0) {
            return new ApiResponse(ERROR, "Cannot delete Product " + product.getName() + " because it has one or more associated Internal Requests.");
        }

        reifyProductRemoteProjectManager(product);
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
        Optional<Product> product = productRepo.findById(request.getProductId());
        ApiResponse response;

        if (product.isPresent()) {
            internalRequestRepo.create(new InternalRequest(request.getTitle(), request.getDescription(), product.get(), new Date()));
            response = new ApiResponse(SUCCESS, request);
        } else {
            response = new ApiResponse(ERROR, "Product with id " + request.getProductId() + " not found!");
        }

        return response;
    }

    @GetMapping("/remote-projects/{productId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllRemoteProjectsForProduct(@PathVariable Long productId) {
        Optional<Product> product = productRepo.findById(productId);
        ApiResponse response;

        if (product.isPresent()) {
            Map<String, RemoteProject> remoteProjects = new HashMap<>();
            Map<String, RemoteProjectManagerBean> rpmBeans = new HashMap<>();

            for (RemoteProjectInfo rpi : product.get().getRemoteProjectInfo()) {
                if (remoteProjects.containsKey(rpi.getScopeId())) {
                    continue;
                }

                RemoteProjectManager rpm = rpi.getRemoteProjectManager();
                RemoteProjectManagerBean rpmBean;

                if (rpmBeans.containsKey(rpm.getName())) {
                    rpmBean = rpmBeans.get(rpm.getName());
                } else {
                    rpmBean = (RemoteProjectManagerBean) managementBeanRegistry.getService(rpm.getName());
                }

                try {
                    RemoteProject remoteProject = rpmBean.getRemoteProjectByScopeId(rpi.getScopeId());
                    remoteProjects.put(rpi.getScopeId(), remoteProject);
                } catch (Exception e) {
                    response = new ApiResponse(ERROR, "Error fetching remote projects associated with product " + product.get().getName() + "!");
                    return response;
                }
            }

            response = new ApiResponse(SUCCESS, remoteProjects);
        } else {
            response = new ApiResponse(ERROR, "Product with id " + productId + " not found!");
        }

        return response;
    }

    @GetMapping("/{remoteProjectManagerId}/remote-projects")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllRemoteProjects(@PathVariable Long remoteProjectManagerId) {
        Optional<RemoteProjectManager> remoteProjectManager = remoteProjectManagerRepo.findById(remoteProjectManagerId);
        ApiResponse response;
        if (remoteProjectManager.isPresent()) {
            RemoteProjectManagerBean remoteProjectManagerBean = (RemoteProjectManagerBean) managementBeanRegistry.getService(remoteProjectManager.get().getName());
            try {
                response = new ApiResponse(SUCCESS, remoteProjectManagerBean.getRemoteProject());
            } catch (Exception e) {
                response = new ApiResponse(ERROR, "Error fetching remote projects from " + remoteProjectManager.get().getName() + "!");
            }
        } else {
            response = new ApiResponse(ERROR, "Remote Project Manager with id " + remoteProjectManagerId + " not found!");
        }
        return response;
    }

    @GetMapping("/{remoteProjectManagerId}/remote-projects/{scopeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getRemoteProjectByScopeId(@PathVariable Long remoteProjectManagerId, @PathVariable String scopeId) {
        Optional<RemoteProjectManager> remoteProjectManager = remoteProjectManagerRepo.findById(remoteProjectManagerId);
        ApiResponse response;
        if (remoteProjectManager.isPresent()) {
            RemoteProjectManagerBean remoteProjectManagerBean = (RemoteProjectManagerBean) managementBeanRegistry.getService(remoteProjectManager.get().getName());
            try {
                response = new ApiResponse(SUCCESS, remoteProjectManagerBean.getRemoteProjectByScopeId(scopeId));
            } catch (Exception e) {
                response = new ApiResponse(ERROR, "Error fetching remote project with scope id " + scopeId + " from " + remoteProjectManager.get().getName() + "!");
            }
        } else {
            response = new ApiResponse(ERROR, "Remote Project Manager with id " + remoteProjectManagerId + " not found!");
        }
        return response;
    }

    private void reifyProductRemoteProjectManager(Product product) {
        Optional<List<RemoteProjectInfo>> remoteProjectInfo = Optional.ofNullable(product.getRemoteProjectInfo());

        if (remoteProjectInfo.isPresent()) {
            for (int i = 0; i < product.getRemoteProjectInfo().size(); i++) {
                Optional<RemoteProjectManager> remoteProjectManager = Optional.ofNullable(remoteProjectInfo.get().get(i).getRemoteProjectManager());
                if (remoteProjectManager.isPresent()) {
                    Long remoteProductManagerId = remoteProjectManager.get().getId();
                    RemoteProjectInfo rpi = new RemoteProjectInfo(remoteProjectInfo.get().get(i).getScopeId(), remoteProjectManagerRepo.getById(remoteProductManagerId));
                    remoteProjectInfo.get().set(i, rpi);
                }
            }
            product.setRemoteProductInfo(remoteProjectInfo.get());
        }
    }

}
