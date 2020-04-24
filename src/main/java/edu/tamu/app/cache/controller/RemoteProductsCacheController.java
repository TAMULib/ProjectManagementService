package edu.tamu.app.cache.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.cache.service.RemoteProductsScheduledCacheService;

@RestController
@RequestMapping("/products/remote")
public class RemoteProductsCacheController extends AbstractCacheController<RemoteProductsScheduledCacheService> {

}
