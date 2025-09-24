package org.globex.retail.ai.review.service;

import com.networknt.schema.JsonSchemaException;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Path("/review")
public class RestResource {

    private static final Logger log = LoggerFactory.getLogger(RestResource.class);

    @Inject
    JsonSchemaValidator validator;

    @Inject
    KafkaService kafkaService;

    @POST
    @Path("/submit")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> submitReview(String payload) {
        return Uni.createFrom().item(() -> payload).onItem().invoke(p -> validator.validate(p))
                .onItem().invoke(p -> {
                    JsonObject json = new JsonObject(p);
                    kafkaService.emit(json.getString("id"), p);
                })
                .onItem().transform(p -> Response.status(200).build())
                .onFailure().recoverWithItem(throwable -> {
                    if (throwable instanceof JsonSchemaException) {
                        log.error("Exception validating payload", throwable);
                        return Response.status(400, "Payload validation error").build();
                    } else {
                        log.error("Exception when processing payload", throwable);
                        return Response.status(500, "Processing error").build();
                    }
                });

    }
}
