package org.globex.retail.ai.review.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.globex.retail.ai.review.service.model.PagedReviewList;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MongoService {

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String database;

    @ConfigProperty(name = "mongodb.reviews.collection")
    String reviewCollection;

    public PagedReviewList reviewsByProduct(String productCode, int page, int limit) {
        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(reviewCollection);
        Bson filter = Filters.eq("product_code", productCode);
        Bson sort = Sorts.orderBy(Sorts.descending("timestamp"));
        MongoIterable<String> iterable = collection
                .find(filter)
                .sort(sort)
                .limit(limit)
                .skip(page*limit)
                .map(Document::toJson);
        List<String> reviews = new ArrayList<>();
        iterable.into(reviews);
        long count = collection.countDocuments(filter);
        return PagedReviewList.builder()
                .withReviews(reviews)
                .withTotalElements(Math.toIntExact(count))
                .withTotalPages((int) Math.ceil((double) count / (double) limit))
                .withNumberOfElements(reviews.size())
                .withSize(limit)
                .withNumber(page)
                .build();
    }
}
