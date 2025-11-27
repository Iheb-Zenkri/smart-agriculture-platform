package com.smartagri.recommendation.exception;

public class InvalidRecommendationInputException extends RuntimeException {

    public InvalidRecommendationInputException(String message) {
        super(message);
    }

    public InvalidRecommendationInputException(String message, Throwable cause) {
        super(message, cause);
    }
}