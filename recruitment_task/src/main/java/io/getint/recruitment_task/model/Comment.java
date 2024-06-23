package io.getint.recruitment_task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    private List<String> comments;

    @JsonProperty("comments")
    public void setCommentsFromJsonNode(JsonNode commentsNode) {
        comments = new ArrayList<>();
        for (JsonNode commentNode : commentsNode) {
            String body = commentNode.path("body").asText();
            comments.add(body);
        }
    }
}
