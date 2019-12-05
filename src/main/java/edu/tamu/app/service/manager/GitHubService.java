package edu.tamu.app.service.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHProjectCard;
import org.kohsuke.github.GHProjectColumn;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.request.FeatureRequest;

public class GitHubService extends MappingRemoteProjectManagerBean {

    private static final Logger logger = Logger.getLogger(VersionOneService.class);

    private static final String ORGANIZATION = "TAMULib";
    private static final String REQUEST_LABEL = "request";
    private static final String ISSUE_LABEL = "issue";
    private static final String FEATURE_LABEL = "feature";
    private static final String DEFECT_LABEL = "bug";

    private final ManagementService managementService;

    private final GitHub github;

    private final Map<String, Member> members;

    private GHLabel label;

    public GitHubService(final ManagementService managementService) throws IOException {
        this.managementService = managementService;
        github = getGitHubInstance();
        members = new HashMap<String, Member>();
    }

    @Override
    public List<RemoteProject> getRemoteProjects() throws Exception {
        logger.info("Fecthing remote projects");
        final List<RemoteProject> remoteProjects = new ArrayList<RemoteProject>();
        final GHOrganization org = github.getOrganization(ORGANIZATION);
        for (final Entry<String, GHRepository> entry : org.getRepositories().entrySet()) {
            final List<GHProject> projects = entry.getValue().listProjects().asList();
            final List<GHLabel> labels = entry.getValue().listLabels().asList();
            for (final GHProject project : projects) {
                remoteProjects.add(buildRemoteProject(project, labels));
            }
        }
        return remoteProjects;
    }

    @Override
    public RemoteProject getRemoteProjectByScopeId(final String scopeId) throws Exception {
        logger.info("Fecthing remote project by scope id " + scopeId);
        GHProject project = github.getProject(Long.parseLong(scopeId));
        List<GHLabel> labels = ((GHRepository) project.getOwner()).listLabels().asList();
        return buildRemoteProject(project, labels);
    }

    @Override
    public List<Sprint> getActiveSprintsByProjectId(final String projectScopeId) throws Exception {
        logger.info("Fecthing active sprints for project with scope id " + projectScopeId);
        List<Sprint> activeSprints = new ArrayList<Sprint>();
        GHRepository repo = github.getRepositoryById(projectScopeId);
        List<GHProject> projects = repo.listProjects().asList();
        for (GHProject project : projects) {
            String sprintId = String.valueOf(project.getId());
            String name = project.getName();
            String projectName = repo.getName();
            List<Card> cards = getCards(project);
            activeSprints.add(new Sprint(sprintId, name, projectName, cards));
        }
        return activeSprints;
    }

    @Override
    public Object push(final FeatureRequest request) throws Exception {
        logger.info("Submitting feature request " + request.getTitle() + " to project with scope id " + request.getScopeId());
        String repoId = String.valueOf(request.getProjectId());
        String title = request.getTitle();
        String body = request.getDescription();
        GHRepository repo = github.getRepositoryById(repoId);
        return repo.createIssue(title).body(body).create();
    }

    private GitHub getGitHubInstance() throws IOException {
        GitHub githubInstance;
        final Optional<String> token = managementService.getSettingValue("token");
        if (token.isPresent()) {
            githubInstance = new GitHubBuilder()
                .withOAuthToken(token.get())
                .build();
        } else {
            githubInstance = new GitHubBuilder()
                .withPassword(getSettingValue("username"), getSettingValue("password"))
                .build();
        }
        return githubInstance;
    }

    private String getSettingValue(final String key) {
        final Optional<String> setting = managementService.getSettingValue(key);
        if (setting.isPresent()) {
            return setting.get();
        }
                
        throw new RuntimeException("No setting " + key + " found in settings for service " + managementService.getName());
    }

    private int getPrimaryWorkItemCount(final String type, final GHProject project, final List<GHLabel> labels)
            throws IOException {
        label = getLabelByName(labels, type);
        return project.listColumns()
            .asList()
            .stream()
            .map(this::countCardsOnColumn)
            .reduce(0L, (a, b) -> a + b)
            .intValue();
    }

    private GHLabel getLabelByName(final List<GHLabel> labels, final String name) {
        return labels.stream()
            .filter(label -> label.getName().equals(name))
            .findFirst()
            .get();
    }

    private boolean cardContainsLabel(GHProjectCard card) {
        try {
            return card.getContent().getLabels().contains(label);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long countCardsOnColumn(GHProjectColumn column) {
        try {
            return column.listCards()
                .asList()
                .stream()
                .filter(this::cardContainsLabel)
                .count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RemoteProject buildRemoteProject(GHProject project, List<GHLabel> labels) throws IOException {
        final String scopeId = String.valueOf(project.getId());
        final String name = project.getName();
        final int requestCount = getPrimaryWorkItemCount(REQUEST_LABEL, project, labels);
        final int issueCount = getPrimaryWorkItemCount(ISSUE_LABEL, project, labels);
        final int featureCount = getPrimaryWorkItemCount(FEATURE_LABEL, project, labels);
        final int defectCount = getPrimaryWorkItemCount(DEFECT_LABEL, project, labels);
        return new RemoteProject(scopeId, name, requestCount, issueCount, featureCount, defectCount);
    }

    private List<Card> getCards(GHProject project) throws IOException {
        List<Card> cards = new ArrayList<Card>();
        for (GHProjectColumn column : project.listColumns().asList()) {
            List<GHProjectCard> projectCards = column.listCards().asList();
            AtomicInteger cardNumber = new AtomicInteger();
            for (GHProjectCard card : projectCards) {
                GHIssue content = card.getContent();

                String id = String.valueOf(card.getId());
                String name = content.getTitle();
                String number = String.valueOf(cardNumber.getAndIncrement());
                // TODO: Figure out what the type priority is
                String type = "";
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

    private Member getMember(GHUser user) throws IOException {
        Member member;
        String memberId = String.valueOf(user.getId());
        Optional<Member> cachedMember = getCachedMember(memberId);
        if (cachedMember.isPresent()) {
            member = cachedMember.get();
        } else {
            String name = user.getName();
            String avatarPath = user.getAvatarUrl();
            member = new Member(memberId, name, avatarPath);

            cacheMember(memberId, member);
        }
        return member;
    }

    private Optional<Member> getCachedMember(final String id) {
        return Optional.ofNullable(members.get(id));
    }

    private void cacheMember(String id, Member member) {
        members.put(id, member);
    }

}