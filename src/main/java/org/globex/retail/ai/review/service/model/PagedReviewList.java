package org.globex.retail.ai.review.service.model;

import java.util.List;

public class PagedReviewList {

    private List<Review> reviews;

    private int totalElements;

    private int totalPages;

    private int numberOfElements;

    private int size;

    private int number;

    public List<Review> getReviews() {
        return reviews;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final PagedReviewList pagedReviewList;

        public Builder() {
            this.pagedReviewList = new PagedReviewList();
        }

        public Builder withReviews(List<Review> reviews) {
            this.pagedReviewList.reviews= reviews;
            return this;
        }

        public Builder withTotalElements(int totalElements) {
            this.pagedReviewList.totalElements = totalElements;
            return this;
        }

        public Builder withTotalPages(int totalPages) {
            this.pagedReviewList.totalPages = totalPages;
            return this;
        }

        public Builder withNumberOfElements(int numberOfElements) {
            this.pagedReviewList.numberOfElements = numberOfElements;
            return this;
        }

        public Builder withSize(int size) {
            this.pagedReviewList.size = size;
            return this;
        }

        public Builder withNumber(int number) {
            this.pagedReviewList.number = number;
            return this;
        }

        public PagedReviewList build() {
            return this.pagedReviewList;
        }
    }

}
