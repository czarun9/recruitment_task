package io.getint.recruitment_task;


import io.getint.recruitment_task.model.Issue;

import java.io.IOException;
import java.util.List;

public class JiraSynchronizer {
    /**
     * Search for 5 tickets in one project, and move them
     * to the other project within same Jira instance.
     * When moving tickets, please move following fields:
     * - summary (title)
     * - description
     * - priority
     * Bonus points for syncing comments.
     */

    private final JiraService jiraService;

    public JiraSynchronizer(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    public void moveTasksToOtherProject(String sourceProjectKey, String targetProjectKey, int maxResults) throws IOException {
        List<Issue> sourceIssues= jiraService.getIssuesFromProject(sourceProjectKey, maxResults);
        String response = jiraService.postIssueToDestProjectWithComments(targetProjectKey, sourceIssues);
    }
}
