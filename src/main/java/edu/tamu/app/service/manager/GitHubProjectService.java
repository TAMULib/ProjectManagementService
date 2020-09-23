package edu.tamu.app.service.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHProject.ProjectStateFilter;
import org.kohsuke.github.GHProjectCard;
import org.kohsuke.github.GHRepository;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.ManagementService;

public class GitHubProjectService extends AbstractGitHubService {

    public GitHubProjectService(final ManagementService managementService) throws IOException {
        super(managementService);
    }

    @Override
    public List<Sprint> getActiveSprintsByScopeId(final String scopeId) throws Exception {
        logger.info("Fetching active sprints for remote project with scope id " + scopeId);
        GHRepository repo = github.getRepositoryById(scopeId);
        String projectName = repo.getName();
        return repo.listProjects(ProjectStateFilter.OPEN)
            .asList().stream()
            .filter(p -> p.getName().toUpperCase().contains(SPRINT))
            .map(p -> new Sprint(String.valueOf(p.getId()), p.getName(), projectName, getCards(p)))
            .collect(Collectors.toList());
    }

    @Override
    public List<Sprint> getAdditionalActiveSprints() throws Exception {
        GHOrganization organization = github.getOrganization(ORGANIZATION);
        List<GHProject> projects = organization.listProjects(ProjectStateFilter.OPEN).asList();
        List<Sprint> sprints = new ArrayList<Sprint>();
        for (GHProject project : projects) {
            // Ignore projects without "Sprint" in the name
            if (!project.getName().toUpperCase().contains(SPRINT)) {
                continue;
            }
            String sprintId = String.valueOf(project.getId());
            String productName = String.format("%s - %s", organization.getName(), project.getName());
            List<Card> cards = getCards(project);
            sprints.add(new Sprint(sprintId, productName, ORGANIZATION, cards));
        }
        return sprints;
    }

    private List<Card> getCards(GHProject project) {
        return exceptionHandlerWrapper(project, i -> i.listColumns().asList().stream())
            .flatMap(c -> exceptionHandlerWrapper(c, i -> i.listCards().asList().stream()))
            .collect(HashMap<GHProjectCard, GHIssue>::new, (m, c) -> m.put(c, exceptionHandlerWrapper(c, i -> i.getContent())), HashMap<GHProjectCard, GHIssue>::putAll)
            .entrySet().stream()
            .filter(e -> e.getValue() != null)
            .map(e -> new Card(
                String.valueOf(e.getKey().getId()),
                String.valueOf(e.getValue().getNumber()),
                mapCardType(exceptionHandlerWrapper(e.getValue(), i -> getCardType(i))),
                e.getValue().getTitle(),
                e.getValue().getBody(),
                mapStatus(exceptionHandlerWrapper(e.getKey(), i -> i.getColumn().getName())),
                mapEstimate(null),
                e.getValue().getAssignees().stream()
                    .map(a -> exceptionHandlerWrapper(a, i -> getMember(i)))
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
    }
}
