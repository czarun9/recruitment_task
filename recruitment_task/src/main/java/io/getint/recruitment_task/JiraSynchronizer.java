package io.getint.recruitment_task;


import io.getint.recruitment_task.model.Issue;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
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
        log.info("Moving" + maxResults + " tickets from " + sourceProjectKey + " to " + targetProjectKey);

        List<Issue> sourceIssues = jiraService.getIssuesFromProject(sourceProjectKey, maxResults);
        jiraService.addIssuesToDestProjectWithComments(targetProjectKey, sourceIssues);

        log.info("Moving tickets completed successfully");
    }
}
