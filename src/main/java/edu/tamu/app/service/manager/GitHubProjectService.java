package edu.tamu.app.service.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHProject.ProjectStateFilter;
import org.kohsuke.github.GHProjectCard;
import org.kohsuke.github.GHProjectColumn;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.ManagementService;

public class GitHubProjectService extends AbstractGitHubService {

    public GitHubProjectService(final ManagementService managementService) throws IOException {
        super(managementService);
    }

    @Override
    public List<Sprint> getActiveSprintsByScopeId(final String scopeId) throws Exception {
        logger.info("Fetching active sprints for remote project with scope id " + scopeId);
        List<Sprint> activeSprints = new ArrayList<Sprint>();
        GHRepository repo = github.getRepositoryById(scopeId);
        List<GHProject> projects = repo.listProjects(ProjectStateFilter.OPEN).asList();
        for (GHProject project : projects) {
            // Ignore projects without "Sprint" in the name
            if (!project.getName().toUpperCase().contains(SPRINT)) {
                continue;
            }
            String sprintId = String.valueOf(project.getId());
            String name = project.getName();
            String projectName = repo.getName();
            List<Card> cards = getCards(project);
            activeSprints.add(new Sprint(sprintId, name, projectName, cards));
        }
        return activeSprints;
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

    private List<Card> getCards(GHProject project) throws IOException {
        List<Card> cards = new ArrayList<Card>();
        for (GHProjectColumn column : project.listColumns().asList()) {
            List<GHProjectCard> projectCards = column.listCards().asList();
            for (GHProjectCard card : projectCards) {
                GHIssue content = card.getContent();
                // If content is null the card is a note and shouldn't be included
                if (content == null) {
                    continue;
                }
                String id = String.valueOf(card.getId());
                String name = content.getTitle();
                String number = String.valueOf(content.getNumber());
                String type = getCardType(content);
                String description = content.getBody();
                String status = card.getColumn().getName();
                // TODO: Figure out how we want to handle sizes
                String estimate = null;
                List<Member> assignees = new ArrayList<Member>();
                for (GHUser user : content.getAssignees()) {
                    assignees.add(getMember(user));
                }
                cards.add(new Card(id, number, mapCardType(type), name, description, mapStatus(status), mapEstimate(estimate), assignees));
            }
        }
        return cards;
    }

}
