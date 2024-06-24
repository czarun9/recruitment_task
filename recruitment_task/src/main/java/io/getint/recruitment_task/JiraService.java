package io.getint.recruitment_task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.getint.recruitment_task.dto.*;
import io.getint.recruitment_task.model.Issue;
import io.getint.recruitment_task.model.Transition;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JiraService {

    private final JiraHttpClient jiraHttpClient;
    private final ObjectMapper objectMapper;

    public JiraService(JiraHttpClient jiraHttpClient) {
        this.jiraHttpClient = jiraHttpClient;
        this.objectMapper = new ObjectMapper();
    }

    public List<Issue> getIssuesFromProject(String projectKey, int maxResults) throws IOException {
        String response = jiraHttpClient.searchIssues(projectKey, maxResults);
        SearchIssueResponse searchIssueDto = objectMapper.readValue(response, SearchIssueResponse.class);
        log.info(searchIssueDto.getIssues().size() + " issues was found");
        return searchIssueDto.getIssues();
    }

    public String addIssuesToDestProjectWithComments(String projectKey, List<Issue> issues) throws JsonProcessingException, UnsupportedEncodingException {
        PostIssuesResponse postIssuesResponse = addIssuesToProject(projectKey, issues);
        postprocessAddedIssues(issues, postIssuesResponse);

        return "";
    }

    private PostIssuesResponse addIssuesToProject(String projectKey, List<Issue> issues) throws JsonProcessingException {
        List<CreateIssueDto> issueDtos = convertIssuesToDTOs(projectKey, issues);
        String jsonIssues = objectMapper.writeValueAsString(new BulkCreateIssueDto(issueDtos));
        String postResponse = jiraHttpClient.postIssues(jsonIssues);
        issues.forEach(i -> log.info("Added issue " + i.getKey() + " to project " + projectKey));
        return objectMapper.readValue(postResponse, PostIssuesResponse.class);
    }

    private void postprocessAddedIssues(List<Issue> issues, PostIssuesResponse addIssuesResponse) throws JsonProcessingException {
        for (int i = 0; i < addIssuesResponse.getIssues().size(); i++) {
            int addedIssueId = addIssuesResponse.getIssues().get(i).getId();
            Issue issue = issues.get(i);

            addComments(issue, addedIssueId);
            transitIssue(issue, addedIssueId);

        }
    }

    private void addComments(Issue sourceIssue, int destIssueId) throws JsonProcessingException {
        List<String> comments = sourceIssue.getFields().getComment().getComments();
        for (String comment : comments) {
            String commentPayload = objectMapper.writeValueAsString(new PostCommentRequestDto(comment));
            jiraHttpClient.addCommentToIssue(commentPayload, destIssueId);
        }
        log.info("Issue " + sourceIssue.getKey() + " comments added");
    }

    private void transitIssue(Issue sourceIssue, int destIssueId) throws JsonProcessingException {
        int statusId = sourceIssue.getFields().getStatus().getStatusCategoryId();

        String response = jiraHttpClient.getTransitions(destIssueId);
        TransitionsResponse transitionsResponse = objectMapper.readValue(response, TransitionsResponse.class);
        Transition transition = transitionsResponse.getTransitions().stream()
                .filter(t -> t.getStatus().getStatusCategoryId() == statusId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No matching transition"));


        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        String transitionPayload = objectMapper.writeValueAsString(new TransitionDto(transition.getId()));
        objectMapper.disable(SerializationFeature.WRAP_ROOT_VALUE);

        jiraHttpClient.transitIssue(transitionPayload, destIssueId);
        log.info("Issue " + sourceIssue.getKey() + " was transited to status: " + transition.getStatus().getStatusCategoryName());
    }

    private List<CreateIssueDto> convertIssuesToDTOs(String projectKey, List<Issue> issues) {
        return issues.stream()
                .map(issue -> new CreateIssueDto(projectKey, issue))
                .collect(Collectors.toList());
    }
}
