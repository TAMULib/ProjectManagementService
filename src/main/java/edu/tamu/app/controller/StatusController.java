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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.Status;
import edu.tamu.app.model.repo.StatusRepo;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/status")
public class StatusController {

    @Autowired
    private StatusRepo statusRepo;

    @GetMapping
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse read() {
        return new ApiResponse(SUCCESS, statusRepo.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse read(@PathVariable Long id) {
        Optional<Status> status = statusRepo.findById(id);
        if (status.isPresent()) {
            return new ApiResponse(SUCCESS, status.get());
        } else {
            return new ApiResponse(ERROR, String.format("Status with id %d does not exist.", id));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse create(@WeaverValidatedModel Status status) {
        return new ApiResponse(SUCCESS, statusRepo.create(status));
    }

    @PutMapping
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse update(@WeaverValidatedModel Status status) {
        return new ApiResponse(SUCCESS, statusRepo.update(status));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('MANAGER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse delete(@WeaverValidatedModel Status status) {
        statusRepo.delete(status);
        return new ApiResponse(SUCCESS);
    }

}