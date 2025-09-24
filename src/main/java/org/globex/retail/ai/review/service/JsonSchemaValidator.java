package org.globex.retail.ai.review.service;

import com.networknt.schema.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class JsonSchemaValidator {

    private static final Logger log = LoggerFactory.getLogger(JsonSchemaValidator.class);

    @ConfigProperty(name = "json.schema")
    private String schemaFile;

    private JsonSchema schema;

    @PostConstruct
    public void loadSchema() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(schemaFile)) {
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
            schema = factory.getSchema(inputStream);
        } catch (IOException e) {
            log.error("Exception loading json schema", e);
            throw new RuntimeException(e);
        }
    }

    public void validate(String json) {
        log.info("json to validate:{}", json);
        Set<ValidationMessage> result = schema.validate(json, InputFormat.JSON);
        if (!result.isEmpty()) {
            throw new JsonSchemaException(result.stream().findFirst().get());
        }
    }
}
