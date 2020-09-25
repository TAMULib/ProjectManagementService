package edu.tamu.app.service.manager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.kohsuke.github.GHMilestoneState;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHProject.ProjectStateFilter;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.BeanUtils;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.ManagementService;

public class GitHubMilestoneService extends AbstractGitHubService {

    public GitHubMilestoneService(final ManagementService managementService) throws IOException {
        super(managementService);
    }

    @Override
    public List<Sprint> getActiveSprintsByScopeId(final String scopeId) throws Exception {
        logger.info("Fetching active sprints for remote project with scope id " + scopeId);
        GHRepository repo = github.getRepositoryById(scopeId);
        String productName = repo.getName();
        AtomicInteger count = new AtomicInteger(1);
        return repo.listProjects(ProjectStateFilter.OPEN).asList().stream()
            .flatMap(project -> getActiveSprintsForProject(project, productName, count.getAndIncrement()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Sprint> getAdditionalActiveSprints() throws Exception {
        GHOrganization organization = github.getOrganization(ORGANIZATION);
        AtomicInteger count = new AtomicInteger(1);
        return organization.listProjects(ProjectStateFilter.OPEN).asList().stream()
            .flatMap(project -> getActiveSprintsForProject(project, toProductName(project), count.getAndIncrement()))
            .collect(Collectors.toList());
    }

    private Stream<Sprint> getActiveSprintsForProject(GHProject project, String product, int count) {
        String sprintId = String.format("%s-%s", project.getId(), count);
        return exceptionHandlerWrapper(project, p -> getCards(p).entrySet()).stream()
            .map(e -> new Sprint(sprintId, e.getKey(), product, e.getValue()));
    }

    private Map<String, List<Card>> getCards(GHProject project) throws IOException {
        return project.listColumns().asList().stream()
            .flatMap(column -> exceptionHandlerWrapper(column, c -> c.listCards().asList().stream()))
            .map(card -> Pair.of(card, exceptionHandlerWrapper(card, c -> c.getContent())))
            // Card without contents is a note
            .filter(p -> Objects.nonNull(p.getValue()))
            // Card without a milestone is not on the sprint
            .filter(p -> Objects.nonNull(p.getValue().getMilestone()))
            // Remove cards with a closed milestone
            .filter(p -> p.getValue().getMilestone().getState().equals(GHMilestoneState.OPEN))
            // Remove cards that don't have "sprint" in the milestone title
            .filter(p -> p.getValue().getMilestone().getTitle().toUpperCase().contains(SPRINT))
            .map(p -> new SprintCard(p.getValue().getMilestone().getTitle(), exceptionHandlerWrapper(p, i -> toCard(i.getKey(), i.getValue()))))
            .collect(Collectors.groupingBy(c -> c.getSprint(), Collectors.toList()));
    }

    private class SprintCard extends Card {
        private static final long serialVersionUID = 1632772205053913423L;
        private final String sprint;
        public SprintCard(String sprint, Card card) {
            this.sprint = sprint;
            BeanUtils.copyProperties(card, this);
        }
        public String getSprint() {
            return sprint;
        }
    }

}
