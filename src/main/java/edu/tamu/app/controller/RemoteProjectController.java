package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/projects/remote")
public class RemoteProjectController {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @GetMapping("/by-product/{productId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllForProduct(@PathVariable Long productId) {
        Optional<Product> product = Optional.ofNullable(productRepo.findOne(productId));
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
                }
                else {
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
}
