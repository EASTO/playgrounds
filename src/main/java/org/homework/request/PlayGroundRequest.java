package org.homework.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PlayGroundRequest(
        @NotBlank(message = "Playground name cannot be null or blank") String name,
        @NotEmpty(message = "Attractions list cannot be null or empty") List<@Valid AttractionRequest> attractions
) {
}
