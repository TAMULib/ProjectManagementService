package edu.tamu.app.service.manager;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.ServiceType;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHProject.ProjectStateFilter;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubProjectService extends AbstractGitHubService {

    protected static Logger logger = LoggerFactory.getLogger(GitHubMilestoneService.class);

    public GitHubProjectService(final ManagementService managementService) throws IOException {
        super(managementService);
    }

    @Override
    public List<Sprint> getActiveSprintsByScopeId(final String scopeId) throws Exception {
        logger.info("Fetching active sprints for remote project with scope id " + scopeId);
        GHRepository repo = github.getRepositoryById(scopeId);
        String productName = repo.getName();
        return repo.listProjects(ProjectStateFilter.OPEN).toList().stream()
            .filter(this::isSprintProject)
            .map(p -> toSprint(p, productName))
            .collect(Collectors.toList());
    }

    @Override
    public List<Sprint> getAdditionalActiveSprints() throws Exception {
        return github.getOrganization(ORGANIZATION)
            .listProjects(ProjectStateFilter.OPEN).toList().stream()
            .filter(this::isSprintProject)
            .map(p -> toSprint(p, ORGANIZATION))
            .collect(Collectors.toList());
    }

    private boolean isSprintProject(GHProject project) {
        return project.getName().toUpperCase().contains(SPRINT);
    }

    private Sprint toSprint(GHProject project, String productName) {
        return new Sprint(
            String.valueOf(project.getId()),
            toProductName(project),
            productName,
            ServiceType.GITHUB_PROJECT.toString(),
            getCards(project)
        );
    }

    private List<Card> getCards(GHProject project) {
        return exceptionHandlerWrapper(project, i -> i.listColumns().toList().stream())
            .flatMap(c -> exceptionHandlerWrapper(c, i -> i.listCards().toList().stream()))
            .map(c -> Pair.of(c, exceptionHandlerWrapper(c, i -> i.getContent())))
            // Card without contents is a note
            .filter(e -> Objects.nonNull(e.getValue()))
            .map(e -> exceptionHandlerWrapper(e, i -> toCard(i.getKey(), i.getValue())))
            .collect(Collectors.toList());
    }

}
