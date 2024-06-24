package io.getint.recruitment_task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
    private int statusCategoryId;
    private String statusCategoryName;

    @JsonProperty("statusCategory")
    public void setStatusCategoryData(JsonNode statusCategoryNode){
        statusCategoryId = statusCategoryNode.path("id").asInt();
        statusCategoryName = statusCategoryNode.path("name").asText();
    }
}
