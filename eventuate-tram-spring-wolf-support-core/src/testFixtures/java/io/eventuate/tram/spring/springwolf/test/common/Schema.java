package io.eventuate.tram.spring.springwolf.test.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Schema {
    private String title;
    private String type;
    private Map<String, SchemaProperty> properties;
    private List<Map<String, Object>> examples;
    @JsonProperty("$ref")
    private String ref;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, SchemaProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, SchemaProperty> properties) {
        this.properties = properties;
    }

    public List<Map<String, Object>> getExamples() {
        return examples;
    }

    public void setExamples(List<Map<String, Object>> examples) {
        this.examples = examples;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}