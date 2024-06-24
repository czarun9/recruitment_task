package io.getint.recruitment_task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getint.recruitment_task.dto.*;
import io.getint.recruitment_task.model.Issue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

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

        return objectMapper.readValue(postResponse, PostIssuesResponse.class);
    }

    private void postprocessAddedIssues(List<Issue> issues, PostIssuesResponse postIssuesResponse) throws JsonProcessingException {
        for (int i = 0; i < postIssuesResponse.getIssues().size(); i++) {
            String issueId = postIssuesResponse.getIssues().get(i).getId();
            Issue issue = issues.get(i);

            addComments(issue, issueId);
//            transitIssue(issue, issueId);

        }
    }

    private void addComments(Issue issue, String issueId) throws JsonProcessingException {
        List<String> comments = issue.getFields().getComment().getComments();
        for (String comment : comments) {
            String commentPayload = objectMapper.writeValueAsString(new PostCommentRequestDto(comment));
            jiraHttpClient.addCommentToIssue(commentPayload, issueId);
        }
    }

    private List<CreateIssueDto> convertIssuesToDTOs(String projectKey, List<Issue> issues) {
        return issues.stream()
                .map(issue -> new CreateIssueDto(projectKey, issue))
                .collect(Collectors.toList());
    }
}
