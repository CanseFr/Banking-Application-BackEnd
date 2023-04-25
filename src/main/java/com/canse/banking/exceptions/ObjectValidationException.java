package com.canse.banking.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
public class ObjectValidationException extends RuntimeException {
    @Getter
    private final Set<String> violations;
    @Getter
    private final String violationSource;

}
