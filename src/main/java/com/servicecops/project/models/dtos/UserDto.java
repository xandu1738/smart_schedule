package com.servicecops.project.models.dtos;

import java.sql.Timestamp;

public record UserDto(
        Long id,
        String institution,
        String firstName,
        String lastName,
        String email,
        String roleCode,
        String domain,
        Timestamp createdAt,
        Timestamp lastLoggedInAt,
        Boolean isActive,
        String username

) {
}
