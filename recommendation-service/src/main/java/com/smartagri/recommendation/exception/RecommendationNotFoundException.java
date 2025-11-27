package com.smartagri.recommendation.exception;

public class RecommendationNotFoundException extends RuntimeException {

    public RecommendationNotFoundException(String message) {
        super(message);
    }

    public RecommendationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}