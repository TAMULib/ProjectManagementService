package edu.tamu.app.service.manager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHProjectCard;
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
import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.rest.TokenAuthRestTemplate;

public abstract class AbstractGitHubService extends MappingRemoteProjectManagerBean {

    protected static Logger logger = LoggerFactory.getLogger(AbstractGitHubService.class);

    static final String ORGANIZATION = "TAMULib";
    static final String SPRINT = "SPRINT";

    static final String ISSUE_LABEL = "issue";
    static final String REQUEST_LABEL = "request";
    static final String FEATURE_LABEL = "feature";
    static final String DEFECT_LABEL = "bug";

    private static final List<String> MATCHING_LABELS = Arrays.asList(
        REQUEST_LABEL,
        FEATURE_LABEL,
        DEFECT_LABEL
    );

    protected final GitHub github;

    private final ManagementService managementService;

    private final RestTemplate restTemplate;

    private final GitHubBuilder ghBuilder;

    private final Map<String, Member> members;

    protected AbstractGitHubService(final ManagementService managementService) {
        this.managementService = managementService;
        this.restTemplate = getRestTemplate();
        this.ghBuilder = new GitHubBuilder();
        this.members = new HashMap<String, Member>();
        this.github = getGitHubInstance();
    }

    @Override
    public List<RemoteProject> getRemoteProject() throws Exception {
        logger.info("Fetching remote projects");
        final GHOrganization org = github.getOrganization(ORGANIZATION);
        return org.getRepositories().values().stream()
            .map(repo -> exceptionHandlerWrapper(repo, this::buildRemoteProject))
            .collect(Collectors.toList());
    }

    @Override
    public RemoteProject getRemoteProjectByScopeId(final String scopeId) throws Exception {
        logger.info("Fetching remote project by scope id " + scopeId);
        final GHRepository repo = github.getRepositoryById(scopeId);
        return buildRemoteProject(repo);
    }

    @Override
    public String push(final FeatureRequest request) throws Exception {
        logger.info("Submitting feature request " + request.getTitle() + " to product with scope id " + request.getScopeId());

        final String scopeId = String.valueOf(request.getScopeId());
        final String title = request.getTitle();
        final String body = request.getDescription();
        final GHRepository repo = github.getRepositoryById(scopeId);

        return Long.toString(repo.createIssue(title).body(body).create().getId());
    }

    GitHub getGitHubInstance() {
        final Optional<String> endpoint = Optional.of(managementService.getUrl());
        final Optional<String> token = Optional.of(managementService.getToken());

        if (!endpoint.isPresent()) {
            throw new RuntimeException("GitHub service endpoint was not defined");
        }

        if (!token.isPresent()) {
            throw new RuntimeException("GitHub token was not defined");
        }

        GitHub github;
        try {
            github = ghBuilder
                .withEndpoint(endpoint.get())
                .withOAuthToken(token.get())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return github;
    }

    Member getMember(final GHUser user) throws IOException {
        final String memberId = String.valueOf(user.getId());
        final Optional<Member> cachedMember = getCachedMember(memberId);

        if (cachedMember.isPresent()) {
            return cachedMember.get();
        }

        final String name = StringUtils.isEmpty(user.getName()) ? user.getLogin() : user.getName();
        final String avatarUrlString = user.getAvatarUrl();
        final String avatarPath = getAvatarPath(avatarUrlString);
        final Member member = new Member(memberId, name, avatarPath);
        final Optional<URL> avatarUrl = Optional.ofNullable(getClass().getResource("/images/" + avatarPath));

        if (!avatarUrl.isPresent()) {
            storeAvatar(avatarUrlString);
        }

        cacheMember(memberId, member);

        return member;
    }

    String toProductName(GHProject project) {
        return String.format("%s/%s", ORGANIZATION, project.getName());
    }

    Card toCard(GHProjectCard card, GHIssue issue) throws IOException {
        String type = getCardType(issue);
        String status = card.getColumn().getName();
        // TODO: Figure out how we want to handle sizes
        String estimate = null;
        return new Card(
            String.valueOf(card.getId()),
            String.valueOf(issue.getNumber()),
            mapCardType(type),
            issue.getTitle(),
            issue.getBody(),
            mapStatus(status),
            mapEstimate(estimate),
            getAssignees(issue)
        );
    }

    private RestTemplate getRestTemplate() {
        return new TokenAuthRestTemplate(managementService.getToken());
    }

    private RemoteProject buildRemoteProject(final GHRepository repo) throws IOException {
        final String scopeId = String.valueOf(repo.getId());
        final String name = repo.getName();

        final List<GHIssue> issues = repo.getIssues(GHIssueState.OPEN);

        long featureCount = 0;
        long defectCount = 0;
        long requestCount = 0;
        long issueCount = 0;

        for (GHIssue issue : issues) {
            if (issue.isPullRequest()) {
                continue;
            }
            Optional<String> label = issue.getLabels().stream()
                .filter(l -> MATCHING_LABELS.contains(l.getName()))
                .map(GHLabel::getName)
                .findFirst();
            if (label.isPresent()) {
                switch (label.get()) {
                    case FEATURE_LABEL: featureCount++; break;
                    case DEFECT_LABEL: defectCount++; break;
                    case REQUEST_LABEL: requestCount++; break;
                }
            } else {
                issueCount++;
            }
        }

        return new RemoteProject(scopeId, name, requestCount, issueCount, featureCount, defectCount, 0L);
    }

    private Optional<Member> getCachedMember(final String id) {
        return Optional.ofNullable(members.get(id));
    }

    private String getAvatarPath(final String url) {
        return url.substring(url.indexOf("/u/") + 3, url.indexOf("?"));
    }

    private void storeAvatar(final String avatarUrl) throws IOException {
        final URL imagesPath = getClass().getResource("/images/");
        final HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

        final HttpEntity<String> entity = new HttpEntity<String>(headers);
        final ResponseEntity<byte[]> response = restTemplate.exchange(avatarUrl, HttpMethod.GET, entity, byte[].class);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            final File file = new File(imagesPath.getFile() + getAvatarPath(avatarUrl));
            Files.write(file.toPath(), response.getBody());
        }
    }

    private void cacheMember(final String id, final Member member) {
        members.put(id, member);
    }

    private String getCardType(final GHIssue issue) throws IOException {
        final List<GHLabel> labels = (List<GHLabel>) issue.getLabels();
        final Optional<String> label = labels.stream()
            .filter(l -> MATCHING_LABELS.contains(l.getName()))
            .map(GHLabel::getName)
            .findFirst();
        return label.isPresent() ? label.get() : ISSUE_LABEL;
    }

    private List<Member> getAssignees(GHIssue issue) {
        return issue.getAssignees().stream()
            .map(user -> exceptionHandlerWrapper(user, u -> getMember(u)))
            .collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface ExceptionHandler<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    static <T, R> R exceptionHandlerWrapper(T t, ExceptionHandler<T, R, Exception> f) {
        try {
            System.out.print("\nDEBUG: f = " + f + "\n\n\n");
            System.out.print("\nDEBUG: t = " + t + "\n\n\n");
            return f.apply(t);
        } catch (Exception e) {
            System.out.print("\nDEBUG: (failed?) f = " + f + "\n\n\n");
            System.out.print("\nDEBUG: (failed?) t = " + t + "\n\n\n");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
