package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;

import java.util.Optional;

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

import edu.tamu.app.model.InternalRequest;
import edu.tamu.app.model.InternalStats;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/internal/request")
public class InternalRequestController {

    @Autowired
    private InternalRequestRepo internalRequestRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse read() {
        return new ApiResponse(SUCCESS, internalRequestRepo.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse read(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, internalRequestRepo.findById(id).get());
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse create(@WeaverValidatedModel InternalRequest internalRequest) {
        return new ApiResponse(SUCCESS, internalRequestRepo.create(internalRequest));
    }

    @PutMapping
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse update(@WeaverValidatedModel InternalRequest internalRequest) {
        return new ApiResponse(SUCCESS, internalRequestRepo.update(internalRequest));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse delete(@WeaverValidatedModel InternalRequest internalRequest) {
        internalRequestRepo.delete(internalRequest);
        return new ApiResponse(SUCCESS);
    }

    @PutMapping("/push/{requestId}/{productId}/{rpmId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse push(@PathVariable Long requestId, @PathVariable Long productId, @PathVariable Long rpmId, @RequestBody String scopeId) {
        Optional<InternalRequest> internalRequest = internalRequestRepo.findById(requestId);
        Optional<Product> product = productRepo.findById(productId);
        Optional<RemoteProjectManager> remoteProjectManager = remoteProjectManagerRepo.findById(rpmId);
        ApiResponse response;

        if (internalRequest.isPresent() && product.isPresent() && remoteProjectManager.isPresent() && !scopeId.isEmpty()) {
            FeatureRequest featureRequest = new FeatureRequest(
                    internalRequest.get().getTitle(), internalRequest.get().getDescription(), product.get().getId(), scopeId);

            RemoteProjectManagerBean remoteProjectManagerBean =
                (RemoteProjectManagerBean) managementBeanRegistry.getService(remoteProjectManager.get().getName());

            try {
                response = new ApiResponse(SUCCESS, remoteProjectManagerBean.push(featureRequest));
                internalRequestRepo.delete(internalRequest.get());
            } catch (Exception e) {
                response = new ApiResponse(ERROR, "Error pushing Internal Request to " + remoteProjectManager.get().getName()
                    + " for Product " + product.get().getName() + "!");
            }
        } else if (!remoteProjectManager.isPresent()) {
            response = new ApiResponse(ERROR, "Remote Project Manager with id " + rpmId + " not found!");
        } else if (!internalRequest.isPresent()) {
            response = new ApiResponse(ERROR, "Internal Request with id " + requestId + " not found!");
        } else if (!product.isPresent()) {
            response = new ApiResponse(ERROR, "Product with id " + productId + " not found!");
        } else {
            response = new ApiResponse(ERROR, "Internal Request is missing the scope id!");
        }

        return response;
    }

    @PreAuthorize("hasRole('ANONYMOUS')")
    @GetMapping("/stats")
    public ApiResponse stats() {
        return new ApiResponse(SUCCESS, new InternalStats(internalRequestRepo.countByProductIsNull(), internalRequestRepo.count()));
    }

}
