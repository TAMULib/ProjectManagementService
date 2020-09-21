package edu.tamu.app.service.manager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHProject.ProjectStateFilter;
import org.kohsuke.github.GHProjectCard;
import org.kohsuke.github.GHProjectColumn;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.rest.TokenAuthRestTemplate;

public class GitHubProjectService extends MappingRemoteProjectManagerBean {

    private static final Logger logger = Logger.getLogger(GitHubProjectService.class);

    protected static final String ORGANIZATION = "TAMULib";
    protected static final String REQUEST_LABEL = "request";
    protected static final String ISSUE_LABEL = "issue";
    protected static final String FEATURE_LABEL = "feature";
    protected static final String DEFECT_LABEL = "bug";
    protected static final String SPRINT = "SPRINT";

    private final ManagementService managementService;

    private final GitHubBuilder ghBuilder;

    private final GitHub github;

    private final Map<String, Member> members;

    private final RestTemplate restTemplate;

    private GHLabel label;

    public GitHubProjectService(final ManagementService managementService) throws IOException {
        this.managementService = managementService;
        ghBuilder = new GitHubBuilder();
        github = getGitHubInstance();
        restTemplate = getRestTemplate();
        members = new HashMap<String, Member>();
    }

    @Override
    public List<RemoteProject> getRemoteProject() throws Exception {
        logger.info("Fetching remote projects");
        final List<RemoteProject> remoteProjects = new ArrayList<RemoteProject>();
        final GHOrganization org = github.getOrganization(ORGANIZATION);
        for (GHRepository repo : org.getRepositories().values()) {
            final List<GHLabel> labels = repo.listLabels().asList();
            remoteProjects.add(buildRemoteProject(repo, labels));
        }
        return remoteProjects;
    }

    @Override
    public RemoteProject getRemoteProjectByScopeId(final String scopeId) throws Exception {
        logger.info("Fetching remote project by scope id " + scopeId);
        GHRepository repo = github.getRepositoryById(scopeId);
        List<GHLabel> labels = repo.listLabels().asList();
        return buildRemoteProject(repo, labels);
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

    @Override
    public String push(final FeatureRequest request) throws Exception {
        logger.info("Submitting feature request " + request.getTitle() + " to product with scope id " + request.getScopeId());

        String scopeId = String.valueOf(request.getScopeId());
        String title = request.getTitle();
        String body = request.getDescription();
        GHRepository repo = github.getRepositoryById(scopeId);

        return Long.toString(repo.createIssue(title).body(body).create().getId());
    }

    protected GitHub getGitHubInstance() throws IOException {
        final Optional<String> endpoint = Optional.of(managementService.getUrl());
        final Optional<String> token = Optional.of(managementService.getToken());

        if (!endpoint.isPresent()) {
            throw new RuntimeException("GitHub service endpoint was not defined");
        }

        if (!token.isPresent()) {
            throw new RuntimeException("GitHub token was not defined");
        }

        return ghBuilder
            .withEndpoint(endpoint.get())
            .withOAuthToken(token.get())
            .build();
    }

    private RestTemplate getRestTemplate() {
        return new TokenAuthRestTemplate(managementService.getToken());
    }

    private RemoteProject buildRemoteProject(GHRepository repo, List<GHLabel> labels) throws IOException {
        final String scopeId = String.valueOf(repo.getId());
        final String name = repo.getName();
        long requestCount = getPrimaryWorkItemCount(REQUEST_LABEL, repo, labels);
        long issueCount = getPrimaryWorkItemCount(ISSUE_LABEL, repo, labels);
        long featureCount = getPrimaryWorkItemCount(FEATURE_LABEL, repo, labels);
        long defectCount = getPrimaryWorkItemCount(DEFECT_LABEL, repo, labels);

        return new RemoteProject(scopeId, name, requestCount, issueCount, featureCount, defectCount, 0L);
    }

    private long getPrimaryWorkItemCount(final String type, final GHRepository repo, final List<GHLabel> labels)
            throws IOException {
        label = getLabelByName(labels, type);
        if (label == null) {
            return 0;
        }
        return repo.listIssues(GHIssueState.OPEN)
            .asList()
            .stream()
            .filter(this::cardIsLabelType)
            .count();
    }

    private GHLabel getLabelByName(final List<GHLabel> labels, final String name) {
        GHLabel returnValue = null;
        Optional<GHLabel> match = labels.stream()
            .filter(label -> label.getName().equals(name))
            .findFirst();
        if (match.isPresent()) {
            returnValue = match.get();
        }
        return returnValue;
    }

    private boolean cardIsLabelType(GHIssue card) {
        try {
            Collection<GHLabel> labels = card.getLabels();
            if (label.getName().equals(ISSUE_LABEL) && isAnIssue(card)) {
                return true;
            }
            return hasLabelByName(labels, label.getName());
            } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isAnIssue(GHIssue card) throws IOException {
        Collection<GHLabel> labels = card.getLabels();
        return !hasLabelByName(labels, REQUEST_LABEL)
            && !hasLabelByName(labels, DEFECT_LABEL)
            && !hasLabelByName(labels, FEATURE_LABEL);
    }

    private boolean hasLabelByName(Collection<GHLabel> labels, String name) {
        return labels.parallelStream()
            .filter(cardLabel -> cardLabel.getName().equals(name))
            .findAny()
            .isPresent();
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

    private String getCardType(GHIssue content) throws IOException {
        List<GHLabel> labels = (List<GHLabel>) content.getLabels();
        GHLabel label = labels.stream()
            .filter(label1 -> label1.getName().equals(DEFECT_LABEL))
            .findFirst()
            .orElseGet(() -> labels.stream()
                .filter(label2 -> label2.getName().equals(FEATURE_LABEL))
                .findFirst()
                .orElseGet(() -> labels.stream()
                    .filter(label3 -> label3.getName().equals(ISSUE_LABEL))
                    .findFirst()
                    .orElseGet(() -> labels.stream()
                        .filter(label4 -> label4.getName().equals(REQUEST_LABEL))
                        .findFirst()
                        .orElse(null)
                    )
                )
            );
        return label == null ? null : label.getName();
    }

    protected Member getMember(GHUser user) throws IOException {
        Member member;
        String memberId = String.valueOf(user.getId());
        Optional<Member> cachedMember = getCachedMember(memberId);
        if (cachedMember.isPresent()) {
            member = cachedMember.get();
        } else {
            String name = user.getName();
            String avatarUrlString = user.getAvatarUrl();
            String avatarPath = getAvatarPath(avatarUrlString);
            member = new Member(memberId, name, avatarPath);

            Optional<URL> avatarUrl = Optional.ofNullable(getClass().getResource("/images/" + avatarPath));
            if (!avatarUrl.isPresent()) {
                storeAvatar(avatarUrlString);
            }

            cacheMember(memberId, member);
        }
        return member;
    }

    private Optional<Member> getCachedMember(final String id) {
        return Optional.ofNullable(members.get(id));
    }

    private String getAvatarPath(String url) {
        return url.substring(url.indexOf("/u/") + 3, url.indexOf("?"));
    }

    private void storeAvatar(String avatarUrl) throws IOException {
        URL imagesPath = getClass().getResource("/images/");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(avatarUrl, HttpMethod.GET, entity, byte[].class, "1");
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            File file = new File(imagesPath.getFile() + getAvatarPath(avatarUrl));
            Files.write(file.toPath(), response.getBody());
        }
    }

    private void cacheMember(String id, Member member) {
        members.put(id, member);
    }

}
