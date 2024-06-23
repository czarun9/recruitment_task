package io.getint.recruitment_task;

import java.io.IOException;

public class JiraTaskMoverApp {

    public static void main(String[] args) {

        String jiraBaseUrl = "https://czarun99.atlassian.net/rest/api/2";
        String username = "czarun99@gmail.com";
        String apiToken="ATATT3xFfGF0LR9BzHmWGWHgZRUaGA39YbNqnj8NTdej0dG7ZMxyG20eCePB3n4TkVBTEWqAhaKZ9Lf_4llBn71qicETUZkjP30xk_UB8k3B_yIHrW2YQ6MB-D-fdZVgRNZzFr9rBDGGIdl3Qb4jVk0Tl1Z9L7VKY9oBu4ZCMbFjzlAafrZKpW8=5879D4A4";
        String sourceProjectKey = "SOURC";
        String targetProjectKey = "DEST";

        JiraHttpClient jiraHttpClient = new JiraHttpClient(jiraBaseUrl, username, apiToken);
        JiraService jiraService = new JiraService(jiraHttpClient);
        JiraSynchronizer jiraSynchronizer = new JiraSynchronizer(jiraService);

        try {
            jiraSynchronizer.moveTasksToOtherProject(sourceProjectKey, targetProjectKey, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
