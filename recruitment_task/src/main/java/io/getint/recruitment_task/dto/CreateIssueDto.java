package io.getint.recruitment_task.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.getint.recruitment_task.model.Fields;
import io.getint.recruitment_task.model.Issue;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateIssueDto {
    @JsonProperty("fields")
    private Fields fields;

    public CreateIssueDto(String projectKey, Issue issue) {
        this.fields = new Fields(projectKey, issue);
    }
}
