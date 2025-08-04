package com.servicecops.project.models.jpahelpers.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmployeeStatus {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable");
    private final String status;
}
