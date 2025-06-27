package org.homework.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record KidRequest(
        @NotBlank(message = "Name cannot be null or blank") String name,
        @Min(value = 0, message = "Age cannot be negative") int age,
        @NotBlank(message = "Ticket number cannot be null or blank") String ticketNumber,
        boolean acceptQueue
) {}
