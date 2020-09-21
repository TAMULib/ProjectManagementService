package edu.tamu.app.service.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHMilestoneState;
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

public class GitHubMilestoneService extends AbstractGitHubService {

    public GitHubMilestoneService(final ManagementService managementService) throws IOException {
        super(managementService);
    }

    @Override
    public List<Sprint> getActiveSprintsByScopeId(final String scopeId) throws Exception {
        logger.info("Fetching active sprints for remote project with scope id " + scopeId);
        List<Sprint> activeSprints = new ArrayList<Sprint>();
        GHRepository repo = github.getRepositoryById(scopeId);
        List<GHProject> projects = repo.listProjects(ProjectStateFilter.OPEN).asList();
        for (GHProject project : projects) {
            String sprintId = String.valueOf(project.getId());
            String projectName = repo.getName();
            Map<String, List<Card>> partitionedCards = getCards(project);
            for (Entry<String, List<Card>> partition : partitionedCards.entrySet()) {
                activeSprints.add(new Sprint(sprintId, partition.getKey(), projectName, partition.getValue()));
            }
        }
        return activeSprints;
    }

    @Override
    public List<Sprint> getAdditionalActiveSprints() throws Exception {
        GHOrganization organization = github.getOrganization(ORGANIZATION);
        List<GHProject> projects = organization.listProjects(ProjectStateFilter.OPEN).asList();
        List<Sprint> sprints = new ArrayList<Sprint>();
        for (GHProject project : projects) {
            String sprintId = String.valueOf(project.getId());
            Map<String, List<Card>> partitionedCards = getCards(project);
            String productName = String.format("%s - %s", organization.getName(), project.getName());
            int count = 0;
            for (Entry<String, List<Card>> partition : partitionedCards.entrySet()) {
                sprints.add(new Sprint(sprintId + "-" + count, partition.getKey(), productName, partition.getValue()));
                count++;
            }
        }
        return sprints;
    }

    private Map<String, List<Card>> getCards(GHProject project) throws IOException {
        Map<String, List<Card>> cardsByMilestone = new HashMap<>();
        for (GHProjectColumn column : project.listColumns().asList()) {
            List<GHProjectCard> projectCards = column.listCards().asList();
            Map<Long, GHIssue> cardContents = new HashMap<>();
            for (GHProjectCard card : projectCards) {
                cardContents.put(card.getId(), card.getContent());
            }
            Map<GHMilestone, List<GHProjectCard>> partitionedCards = projectCards.stream()
                // Card without contents is a note
                .filter(c -> cardContents.get(c.getId()) != null)
                // Card without a milestone is not on the sprint
                .filter(c -> cardContents.get(c.getId()).getMilestone() != null)
                .collect(Collectors.groupingBy(c -> cardContents.get(c.getId()).getMilestone()));

            for (Entry<GHMilestone, List<GHProjectCard>> partition : partitionedCards.entrySet()) {
                List<Card> cards = new ArrayList<>();
                for (GHProjectCard card : partition.getValue()) {
                    GHIssue content = cardContents.get(card.getId());
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
                GHMilestone milestone = partition.getKey();
                if (milestone.getState().equals(GHMilestoneState.OPEN) && milestone.getTitle().toUpperCase().contains(SPRINT)) {
                    String title = partition.getKey().getTitle();
                    if (cardsByMilestone.containsKey(title)) {
                        cardsByMilestone.get(title).addAll(cards);
                    } else {
                        cardsByMilestone.put(title, cards);
                    }
                }
            }
        }
        return cardsByMilestone;
    }

}
