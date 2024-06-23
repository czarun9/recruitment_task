package io.getint.recruitment_task.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fields {

    private String summary;
    private String description;
    @JsonProperty("issuetype")
    private IssueType issueType;
//    private Comment comment;
    private Priority priority;
    private Project project;

    public Fields(String projectKey, Issue issue) {
        this.project = new Project(projectKey);
        this.summary = issue.getFields().getSummary();
        this.issueType = issue.getFields().getIssueType();
        this.description = issue.getFields().getDescription();
        this.priority = issue.getFields().getPriority();
    }

    @JsonCreator
    public Fields(@JsonProperty("summary") String summary,
                  @JsonProperty("description") String description,
                  @JsonProperty("issuetype") IssueType issueType,
                  @JsonProperty("priority") Priority priority) {
        this.summary = summary;
        this.description = description;
        this.issueType = issueType;
        this.priority = priority;
    }
}
