package edu.tamu.app.cache.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import edu.tamu.app.cache.service.AbstractScheduledCacheService;
import edu.tamu.weaver.response.ApiResponse;

public abstract class AbstractCacheController<S extends AbstractScheduledCacheService<?, ?>> implements CacheController {

    @Autowired
    private S cacheService;

    @GetMapping
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse get() {
        return new ApiResponse(SUCCESS, cacheService.get());
    }

    @GetMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse update() {
        cacheService.update();
        cacheService.broadcast();
        return new ApiResponse(SUCCESS);
    }

}
