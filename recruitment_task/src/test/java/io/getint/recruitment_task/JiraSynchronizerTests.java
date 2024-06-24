package io.getint.recruitment_task;

import org.junit.Before;
import org.junit.Test;

public class JiraSynchronizerTests {

    private String jiraBaseUrl = "https://czarun99.atlassian.net/rest/api/2";
    private String username = "czarun99@gmail.com";
    private String apiToken="ATATT3xFfGF0LR9BzHmWGWHgZRUaGA39YbNqnj8NTdej0dG7ZMxyG20eCePB3n4TkVBTEWqAhaKZ9Lf_4llBn71qicETUZkjP30xk_UB8k3B_yIHrW2YQ6MB-D-fdZVgRNZzFr9rBDGGIdl3Qb4jVk0Tl1Z9L7VKY9oBu4ZCMbFjzlAafrZKpW8=5879D4A4";
    private String sourceProjectKey = "SOURC";
    private String targetProjectKey = "DEST";

    private JiraSynchronizer jiraSynchronizer;


    @Before
    public void setUp() {
        JiraHttpClient jiraHttpClient = new JiraHttpClient(jiraBaseUrl, username, apiToken);
        JiraService jiraService = new JiraService(jiraHttpClient);
        jiraSynchronizer = new JiraSynchronizer(jiraService);
    }
    @Test
    public void shouldSyncTasks() throws Exception {
        jiraSynchronizer.moveTasksToOtherProject(sourceProjectKey,targetProjectKey, 5);
    }
}
