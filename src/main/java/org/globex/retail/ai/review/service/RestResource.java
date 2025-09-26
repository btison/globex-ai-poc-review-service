package org.globex.retail.ai.review.service;

import com.networknt.schema.JsonSchemaException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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

    @Inject
    MongoService mongoService;

    @ConfigProperty(name = "mongodb.reviews.query.limit")
    Integer queryLimit;

    @POST
    @Path("/submit")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> submitReview(String payload) {
        return Uni.createFrom().item(() -> payload).emitOn(Infrastructure.getDefaultWorkerPool())
                .onItem().invoke(p -> validator.validate(p))
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

    @GET
    @Path("/product/{id}")
    public Uni<Response> getReviewsByProductId(@PathParam("id") String productId, @QueryParam("page") Integer page, @QueryParam("limit") Integer limit) {
        return Uni.createFrom().item(() -> productId).emitOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transform(id -> {
                    int pageIndex;
                    if (page == null) {
                        pageIndex = 0;
                    } else {
                        pageIndex = page == 0? 0 : page-1;
                    }
                    int max;
                    if (limit == null || limit <= 0) {
                        max = queryLimit;
                    } else {
                        max = limit;
                    }
                    return mongoService.reviewsByProduct(id, pageIndex, max);
                })
                .onItem().transform(p -> Response.ok(p).build())
                .onFailure().recoverWithItem(throwable -> {
                    log.error("Exception while fetching paged review list", throwable);
                    return Response.serverError().build();
        });
    }

    @GET
    @Path("/summary/{id}")
    public Uni<Response> getReviewSummaryByProductId(@PathParam("id") String productId) {
        return Uni.createFrom().item(() -> productId).emitOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transform(id -> mongoService.reviewSummaryByProductId(id))
                .onItem().transform(s -> {
                    if (s == null || s.isBlank()) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    } else {
                        return Response.ok(s).build();
                    }
                })
                .onFailure().recoverWithItem(throwable -> {
                    log.error("Exception while fetching review summary", throwable);
                    return Response.serverError().build();
                });
    }
}
