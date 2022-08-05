package edu.tamu.app.controller;

import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/remote")
public class RemoteProjectController {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;
}
