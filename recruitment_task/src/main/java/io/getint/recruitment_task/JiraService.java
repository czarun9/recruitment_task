package io.getint.recruitment_task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getint.recruitment_task.dto.BulkCreateIssueDto;
import io.getint.recruitment_task.dto.CreateIssueDto;
import io.getint.recruitment_task.model.Issue;
import io.getint.recruitment_task.dto.SearchIssueDto;

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
        SearchIssueDto searchIssueDto = objectMapper.readValue(response, SearchIssueDto.class);
        return searchIssueDto.getIssues();
    }

    public String postIssueToDestProject(String projectKey, List<Issue> issues) throws JsonProcessingException, UnsupportedEncodingException {
        List<CreateIssueDto> issueDtos = convertIssuesToDTOs(projectKey, issues);
        String jsonIssues = objectMapper.writeValueAsString(new BulkCreateIssueDto(issueDtos));
        String response = jiraHttpClient.postIssues(jsonIssues);
        return "";
    }

    private List<CreateIssueDto> convertIssuesToDTOs(String projectKey, List<Issue> issues) {
        return issues.stream()
                .map(issue -> new CreateIssueDto(projectKey, issue))
                .collect(Collectors.toList());
    }
}
