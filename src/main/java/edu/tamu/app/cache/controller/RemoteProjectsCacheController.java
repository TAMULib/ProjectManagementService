package edu.tamu.app.cache.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;

@RestController
@RequestMapping("/projects/remote")
public class RemoteProjectsCacheController extends AbstractCacheController<RemoteProjectsScheduledCacheService> {

}
