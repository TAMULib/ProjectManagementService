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

import org.apache.commons.lang3.StringUtils;
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
import org.kohsuke.github.GHProject.ProjectStateFilter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.RemoteProduct;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.rest.BasicAuthRestTemplate;
import edu.tamu.app.rest.TokenAuthRestTemplate;

public class GitHubService extends MappingRemoteProductManagerBean {

    private static final Logger logger = Logger.getLogger(GitHubService.class);

    protected static final String ORGANIZATION = "TAMULib";
    protected static final String REQUEST_LABEL = "request";
    protected static final String ISSUE_LABEL = "issue";
    protected static final String FEATURE_LABEL = "feature";
    protected static final String DEFECT_LABEL = "bug";

    private final ManagementService managementService;

    private final GitHubBuilder ghBuilder;

    private final GitHub github;

    private final Map<String, Member> members;

    private final RestTemplate restTemplate;

    private GHLabel label;

    public GitHubService(final ManagementService managementService) throws IOException {
        this.managementService = managementService;
        ghBuilder = new GitHubBuilder();
        github = getGitHubInstance();
        restTemplate = getRestTemplate();
        members = new HashMap<String, Member>();
    }

    @Override
    public List<RemoteProduct> getRemoteProduct() throws Exception {
        logger.info("Fetching remote products");
        final List<RemoteProduct> remoteProducts = new ArrayList<RemoteProduct>();
        final GHOrganization org = github.getOrganization(ORGANIZATION);
        for (GHRepository repo : org.getRepositories().values()) {
            final List<GHLabel> labels = repo.listLabels().asList();
            remoteProducts.add(buildRemoteProduct(repo, labels));
        }
        return remoteProducts;
    }

    @Override
    public RemoteProduct getRemoteProductByScopeId(final String scopeId) throws Exception {
        logger.info("Fetching remote product by scope id " + scopeId);
        GHRepository repo = github.getRepositoryById(scopeId);
        List<GHLabel> labels = repo.listLabels().asList();
        return buildRemoteProduct(repo, labels);
    }

    @Override
    public List<Sprint> getActiveSprintsByProductId(final String productScopeId) throws Exception {
        logger.info("Fetching active sprints for product with scope id " + productScopeId);
        List<Sprint> activeSprints = new ArrayList<Sprint>();
        GHRepository repo = github.getRepositoryById(productScopeId);
        List<GHProject> projects = repo.listProjects(ProjectStateFilter.OPEN).asList();
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
    public List<Sprint> getAdditionalActiveSprints() throws Exception {
        GHOrganization organization = github.getOrganization(ORGANIZATION);
        List<GHProject> projects = organization.listProjects(ProjectStateFilter.OPEN).asList();
        List<Sprint> sprints = new ArrayList<Sprint>();
        for (GHProject project : projects) {
            String sprintId = String.valueOf(project.getId());
            String name = project.getName();
            List<Card> cards = getCards(project);
            sprints.add(new Sprint(sprintId, name, ORGANIZATION, cards));
        }
        return sprints;
    }

    @Override
    public Object push(final FeatureRequest request) throws Exception {
        logger.info("Submitting feature request " + request.getTitle() + " to product with scope id " + request.getScopeId());
        String repoId = String.valueOf(request.getProductId());
        String title = request.getTitle();
        String body = request.getDescription();
        GHRepository repo = github.getRepositoryById(repoId);
        return repo.createIssue(title).body(body).create();
    }

    protected GitHub getGitHubInstance() throws IOException {
        GitHub githubInstance;
        final Optional<String> endpoint = managementService.getSettingValue("url");
        final Optional<String> token = managementService.getSettingValue("token");
        if (!endpoint.isPresent()) {
            throw new RuntimeException("GitHub service enpoint was not defined");
        }
        if (token.isPresent()) {
            githubInstance = ghBuilder
                .withEndpoint(endpoint.get())
                .withOAuthToken(token.get())
                .build();
        } else {
            githubInstance = ghBuilder
                .withEndpoint(endpoint.get())
                .withPassword(getSettingValue("username"), getSettingValue("password"))
                .build();
        }
        return githubInstance;
    }

    private String getSettingValue(final String key) {
        final Optional<String> setting = managementService.getSettingValue(key);
        if (setting.isPresent()) {
            return setting.get();
        } else {
            return null;
        }
    }

    private RestTemplate getRestTemplate() {
        String token = getSettingValue("token");
        return StringUtils.isNotBlank(token) ? new TokenAuthRestTemplate(token) : new BasicAuthRestTemplate(getSettingValue("username"), getSettingValue("password"));
    }

    private RemoteProduct buildRemoteProduct(GHRepository repo, List<GHLabel> labels) throws IOException {
        List<GHProject> projects = repo.listProjects().asList();
        final String scopeId = String.valueOf(repo.getId());
        final String name = repo.getName();
        int requestCount = 0;
        int issueCount = 0;
        int featureCount = 0;
        int defectCount = 0;

        for (GHProject project : projects) {
            requestCount += getPrimaryWorkItemCount(REQUEST_LABEL, project, labels);
            issueCount += getPrimaryWorkItemCount(ISSUE_LABEL, project, labels);
            featureCount += getPrimaryWorkItemCount(FEATURE_LABEL, project, labels);
            defectCount += getPrimaryWorkItemCount(DEFECT_LABEL, project, labels);
        }

        return new RemoteProduct(scopeId, name, requestCount, issueCount, featureCount, defectCount);
    }

    private int getPrimaryWorkItemCount(final String type, final GHProject project, final List<GHLabel> labels)
            throws IOException {
        label = getLabelByName(labels, type);
        if (label == null) {
            return 0;
        }
        return project.listColumns()
            .asList()
            .stream()
            .map(this::countCardsOnColumn)
            .reduce(0L, (a, b) -> a + b)
            .intValue();
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

    private long countCardsOnColumn(GHProjectColumn column) {
        try {
            return column.listCards()
                .asList()
                .stream()
                .filter(this::cardIsLabelType)
                .count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean cardIsLabelType(GHProjectCard card) {
        try {
            Collection<GHLabel> labels = card.getContent().getLabels();
            if (label.getName().equals(ISSUE_LABEL) && isAnIssue(card)) {
                return true;
            }
            return hasLabelByName(labels, label.getName());
            } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isAnIssue(GHProjectCard card) throws IOException {
        Collection<GHLabel> labels = card.getContent().getLabels();
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