package org.homework.domain;

public record Attraction(Type type, int capacity) {
    public enum Type {DOUBLE_SWINGS, CAROUSEL, SLIDE, BALL_PIT}

    public Attraction {
        if (type == null) {
            throw new IllegalArgumentException("Attraction type cannot be null");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Attraction capacity must be greater than zero");
        }
    }
}