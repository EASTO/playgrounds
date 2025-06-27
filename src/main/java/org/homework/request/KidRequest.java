package org.homework.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record KidRequest(
        @NotBlank(message = "Name cannot be null or blank") String name,
        @Min(value = 0, message = "Age cannot be negative") int age,
        @NotBlank(message = "Ticket number cannot be null or blank") String ticketNumber,
        boolean acceptQueue
) {

//    public KidRequest {
//        if (name == null || name.isBlank()) {
//            throw new IllegalArgumentException("Name cannot be null or blank");
//        }
//        if (age < 0) {
//            throw new IllegalArgumentException("Age cannot be negative");
//        }
//        if (ticketNumber == null || ticketNumber.isBlank()) {
//            throw new IllegalArgumentException("Ticket number cannot be null or blank");
//        }
//    }
}
