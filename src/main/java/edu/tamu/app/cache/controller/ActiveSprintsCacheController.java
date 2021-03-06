package edu.tamu.app.cache.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;

@RestController
@RequestMapping("/sprints/active")
public class ActiveSprintsCacheController extends AbstractCacheController<ActiveSprintsScheduledCacheService> {

}
