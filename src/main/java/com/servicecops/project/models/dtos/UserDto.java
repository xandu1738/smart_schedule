package com.servicecops.project.models.dtos;

import java.sql.Timestamp;

public record UserDto(
        Long id,
        Integer institutionId,
        String firstName,
        String lastName,
        String email,
        String roleCode,
        Timestamp createdAt,
        Timestamp lastLoggedInAt,
        Boolean isActive,
        String username

) {
}
