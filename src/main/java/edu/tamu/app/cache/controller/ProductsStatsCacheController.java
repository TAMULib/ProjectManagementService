package edu.tamu.app.cache.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.cache.service.ProjectsStatsScheduledCacheService;

@RestController
@RequestMapping("/projects/stats")
public class ProjectsStatsCacheController extends AbstractCacheController<ProjectsStatsScheduledCacheService> {

}
