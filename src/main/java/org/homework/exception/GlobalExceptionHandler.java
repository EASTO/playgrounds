package org.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            if (field.startsWith("attractions[")) {
                int idxStart = field.indexOf('[') + 1;
                int idxEnd = field.indexOf(']');
                int index = Integer.parseInt(field.substring(idxStart, idxEnd));
                String subField = field.substring(field.indexOf(']') + 2); // skip ].
                List<Map<String, String>> attrErrors = (List<Map<String, String>>) errors.computeIfAbsent("attractions", k -> new ArrayList<>());
                while (attrErrors.size() <= index) attrErrors.add(new HashMap<>());
                attrErrors.get(index).put(subField, error.getDefaultMessage());
            } else {
                errors.put(field, error.getDefaultMessage());
            }
        });
        return errors;
    }
}