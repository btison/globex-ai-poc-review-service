package org.globex.retail.ai.review.service.model;

public class Review {

    private String id;

    private String user;

    private String productCode;

    private String product;

    private String review;

    private int stars;

    private long timestamp;

    private String created;

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProduct() {
        return product;
    }

    public String getReview() {
        return review;
    }

    public int getStars() {
        return stars;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCreated() {
        return created;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {

        private final Review review;

        public Builder(String id) {
            this.review = new Review();
            review.id = id;
        }

        public Builder withUser(String user) {
            this.review.user = user;
            return this;
        }

        public Builder withProductCode(String productCode) {
            this.review.productCode = productCode;
            return this;
        }

        public Builder withProduct(String product) {
            this.review.product = product;
            return this;
        }

        public Builder withReview(String review) {
            this.review.review = review;
            return this;
        }

        public Builder withTimestamp(Long timestamp) {
            this.review.timestamp = timestamp;
            return this;
        }

        public Builder withCreated(String created) {
            this.review.created = created;
            return this;
        }

        public Builder withStars(int stars) {
            this.review.stars = stars;
            return this;
        }

        public Review build() {
            return this.review;
        }

    }

}
