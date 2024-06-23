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

    public String postIssueToDestProjectWithComments(String projectKey, List<Issue> issues) throws JsonProcessingException, UnsupportedEncodingException {
        List<CreateIssueDto> issueDtos = convertIssuesToDTOs(projectKey, issues);
        String jsonIssues = objectMapper.writeValueAsString(new BulkCreateIssueDto(issueDtos));
        String postResponse = jiraHttpClient.postIssues(jsonIssues);

        PostIssuesResponse postIssuesResponse = objectMapper.readValue(postResponse, PostIssuesResponse.class);

        for (int i = 0; i < postIssuesResponse.getIssues().size(); i++) {
            String issueId = postIssuesResponse.getIssues().get(i).getId();
            Issue issue = issues.get(i);
            List<String> comments = issue.getFields().getComment().getComments();

            for (String comment : comments) {
                String commentPayload = objectMapper.writeValueAsString(new PostCommentRequestDto(comment));
                jiraHttpClient.addCommentToIssue(commentPayload, issueId);
            }
//            System.out.println("Comment: "+ commentBody + "\nadded to issue: " + issueId);
        }

        return "";
    }

    private List<CreateIssueDto> convertIssuesToDTOs(String projectKey, List<Issue> issues) {
        return issues.stream()
                .map(issue -> new CreateIssueDto(projectKey, issue))
                .collect(Collectors.toList());
    }
}
