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
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.service.manager.RemoteProductManagerBean;
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

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse read() {
        return new ApiResponse(SUCCESS, internalRequestRepo.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse read(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, internalRequestRepo.findOne(id));
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

    @PutMapping("/push/{requestId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse push(@PathVariable Long requestId, @RequestBody FeatureRequest featureRequestParam) {
        Optional<InternalRequest> internalRequest = Optional.ofNullable(internalRequestRepo.findOne(requestId));
        Optional<Product> product = Optional.ofNullable(productRepo.findOne(featureRequestParam.getProductId()));
        ApiResponse response;

        if (internalRequest.isPresent() && product.isPresent()) {
            Optional<RemoteProductManager> remoteProductManager = Optional
                .ofNullable(product.get().getRemoteProductManager());

            if (remoteProductManager.isPresent()) {
                FeatureRequest featureRequest = new FeatureRequest(
                        internalRequest.get().getTitle(), internalRequest.get().getDescription(), product.get().getId(), product.get().getScopeId());

                RemoteProductManagerBean remoteProductManagerBean = 
                    (RemoteProductManagerBean) managementBeanRegistry.getService(remoteProductManager.get().getName());

                try {
                    response = new ApiResponse(SUCCESS, remoteProductManagerBean.push(featureRequest));
                    internalRequestRepo.delete(internalRequest.get());
                } catch (Exception e) {
                    response = new ApiResponse(ERROR, "Error pushing request to " + remoteProductManager.get().getName()
                        + " for product " + product.get().getName() + "!");
                }
            } else {
                response = new ApiResponse(ERROR,
                    product.get().getName() + " product does not have a Remote Product Manager!");
            }
        } else if (internalRequest.isPresent()) {
            response = new ApiResponse(ERROR, "Product with id " + featureRequestParam.getProductId() + " not found!");

        } else {
            response = new ApiResponse(ERROR, "Internal Request with id " + requestId + " not found!");
        }

        return response;
    }

}