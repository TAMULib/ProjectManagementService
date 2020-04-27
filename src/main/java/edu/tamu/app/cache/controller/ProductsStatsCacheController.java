package edu.tamu.app.cache.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;

@RestController
@RequestMapping("/products/stats")
public class ProductsStatsCacheController extends AbstractCacheController<ProductsStatsScheduledCacheService> {

}
