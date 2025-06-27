package org.homework.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AttractionRequest(
        @NotNull(message = "Attraction type cannot be null") org.homework.domain.Attraction.Type type,
        @Min(value = 1, message = "Attraction capacity must be greater than zero") int capacity
) {}
