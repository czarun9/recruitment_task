package io.getint.recruitment_task;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JiraHttpClient {

    private final String jiraBaseUrl;
    private final String authHeader;

    public JiraHttpClient(String jiraBaseUrl, String username, String apiToken) {
        this.jiraBaseUrl = jiraBaseUrl;
        String auth = username + ":" + apiToken;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    public String searchIssues(String sourceProjectKey, int maxResults) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            URI uri = new URIBuilder(jiraBaseUrl)
                    .setPath("/rest/api/2/search")
                    .addParameter("jql", "project=" + sourceProjectKey)
                    .addParameter("fields", "summary,description,priority,comment,issuetype")
                    .addParameter("maxResults", Integer.toString(maxResults))
                    .build();
            HttpGet httpGet = new HttpGet(uri);

            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Authorization", authHeader);

            HttpResponse response = httpClient.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + statusCode);
            }
            System.out.println("Found issues");
            return EntityUtils.toString(response.getEntity());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String postIssues(String jsonPayload) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            URI uri = new URIBuilder(jiraBaseUrl)
                    .setPath("/rest/api/2/issue/bulk")
                    .build();
            HttpPost httpPost = new HttpPost(uri);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", authHeader);
            StringEntity entity = new StringEntity(jsonPayload, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 201) {
                throw new RuntimeException("Failed : HTTP error code : " + statusCode);
            }
            System.out.println("Issues added");

            return EntityUtils.toString(response.getEntity());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String addCommentToIssue(String jsonPayload, String issueId){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            URI uri = new URIBuilder(jiraBaseUrl)
                    .setPath("rest/api/2/issue/" +  issueId + "/comment")
                    .build();
            HttpPost httpPost = new HttpPost(uri);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", authHeader);
            StringEntity entity = new StringEntity(jsonPayload, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 201) {
                throw new RuntimeException("Failed : HTTP error code : " + statusCode);
            }
            System.out.println("Comments added");

            return EntityUtils.toString(response.getEntity());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
