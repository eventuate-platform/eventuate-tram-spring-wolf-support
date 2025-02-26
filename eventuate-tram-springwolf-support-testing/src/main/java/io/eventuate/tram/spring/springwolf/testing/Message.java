package io.eventuate.tram.spring.springwolf.testing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    private MessagePayload payload;
    private String description;

    public MessagePayload getPayload() {
        return payload;
    }

    public void setPayload(MessagePayload payload) {
        this.payload = payload;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessagePayload {
        private String schemaFormat;
        private Schema schema;

        public String getSchemaFormat() {
            return schemaFormat;
        }

        public void setSchemaFormat(String schemaFormat) {
            this.schemaFormat = schemaFormat;
        }

        public Schema getSchema() {
            return schema;
        }

        public void setSchema(Schema schema) {
            this.schema = schema;
        }
    }
}