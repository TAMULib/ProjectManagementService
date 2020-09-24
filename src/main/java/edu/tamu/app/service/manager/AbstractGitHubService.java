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

import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.rest.TokenAuthRestTemplate;

public abstract class AbstractGitHubService extends MappingRemoteProjectManagerBean {

    protected static final Logger logger = Logger.getLogger(GitHubProjectService.class);

    protected static final String ORGANIZATION = "TAMULib";
    protected static final String REQUEST_LABEL = "request";
    protected static final String ISSUE_LABEL = "issue";
    protected static final String FEATURE_LABEL = "feature";
    protected static final String DEFECT_LABEL = "bug";
    protected static final String SPRINT = "SPRINT";

    protected final ManagementService managementService;

    protected final GitHubBuilder ghBuilder;

    protected final GitHub github;

    protected final Map<String, Member> members;

    protected final RestTemplate restTemplate;

    protected GHLabel label;

    protected AbstractGitHubService(final ManagementService managementService) throws IOException {
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
        for (final GHRepository repo : org.getRepositories().values()) {
            final List<GHLabel> labels = repo.listLabels().asList();
            remoteProjects.add(buildRemoteProject(repo, labels));
        }
        return remoteProjects;
    }

    @Override
    public RemoteProject getRemoteProjectByScopeId(final String scopeId) throws Exception {
        logger.info("Fetching remote project by scope id " + scopeId);
        final GHRepository repo = github.getRepositoryById(scopeId);
        final List<GHLabel> labels = repo.listLabels().asList();
        return buildRemoteProject(repo, labels);
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

    protected RestTemplate getRestTemplate() {
        return new TokenAuthRestTemplate(managementService.getToken());
    }

    protected RemoteProject buildRemoteProject(final GHRepository repo, final List<GHLabel> labels) throws IOException {
        final String scopeId = String.valueOf(repo.getId());
        final String name = repo.getName();
        final long requestCount = getPrimaryWorkItemCount(REQUEST_LABEL, repo, labels);
        final long issueCount = getPrimaryWorkItemCount(ISSUE_LABEL, repo, labels);
        final long featureCount = getPrimaryWorkItemCount(FEATURE_LABEL, repo, labels);
        final long defectCount = getPrimaryWorkItemCount(DEFECT_LABEL, repo, labels);

        return new RemoteProject(scopeId, name, requestCount, issueCount, featureCount, defectCount, 0L);
    }

    protected long getPrimaryWorkItemCount(final String type, final GHRepository repo, final List<GHLabel> labels)
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

    protected GHLabel getLabelByName(final List<GHLabel> labels, final String name) {
        GHLabel returnValue = null;
        final Optional<GHLabel> match = labels.stream()
            .filter(label -> label.getName().equals(name))
            .findFirst();
        if (match.isPresent()) {
            returnValue = match.get();
        }
        return returnValue;
    }

    protected boolean cardIsLabelType(GHIssue card) {
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

    protected boolean isAnIssue(final GHIssue card) throws IOException {
        final Collection<GHLabel> labels = card.getLabels();
        return !hasLabelByName(labels, REQUEST_LABEL)
            && !hasLabelByName(labels, DEFECT_LABEL)
            && !hasLabelByName(labels, FEATURE_LABEL);
    }

    protected boolean hasLabelByName(final Collection<GHLabel> labels, final String name) {
        return labels.parallelStream()
            .filter(cardLabel -> cardLabel.getName().equals(name))
            .findAny()
            .isPresent();
    }

    protected String getCardType(final GHIssue content) throws IOException {
        final List<GHLabel> labels = (List<GHLabel>) content.getLabels();
        final GHLabel label = labels.stream()
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

    protected Member getMember(final GHUser user) throws IOException {
        Member member;
        final String memberId = String.valueOf(user.getId());
        final Optional<Member> cachedMember = getCachedMember(memberId);
        if (cachedMember.isPresent()) {
            member = cachedMember.get();
        } else {
            final String name = user.getName();
            final String avatarUrlString = user.getAvatarUrl();
            final String avatarPath = getAvatarPath(avatarUrlString);
            member = new Member(memberId, name, avatarPath);

            final Optional<URL> avatarUrl = Optional.ofNullable(getClass().getResource("/images/" + avatarPath));
            if (!avatarUrl.isPresent()) {
                storeAvatar(avatarUrlString);
            }

            cacheMember(memberId, member);
        }
        return member;
    }

    protected Optional<Member> getCachedMember(final String id) {
        return Optional.ofNullable(members.get(id));
    }

    protected String getAvatarPath(final String url) {
        return url.substring(url.indexOf("/u/") + 3, url.indexOf("?"));
    }

    protected void storeAvatar(final String avatarUrl) throws IOException {
        final URL imagesPath = getClass().getResource("/images/");
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        final HttpEntity<String> entity = new HttpEntity<String>(headers);
        final ResponseEntity<byte[]> response = restTemplate.exchange(avatarUrl, HttpMethod.GET, entity, byte[].class, "1");
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            final File file = new File(imagesPath.getFile() + getAvatarPath(avatarUrl));
            Files.write(file.toPath(), response.getBody());
        }
    }

    protected void cacheMember(final String id, final Member member) {
        members.put(id, member);
    }
}