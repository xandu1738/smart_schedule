package com.servicecops.project.models.dtos.mapper;

import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.models.dtos.UserDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserDtoMapper implements Function<SystemUserModel, UserDto> {
    @Override
    public UserDto apply(SystemUserModel systemUserModel) {
        return new UserDto(
                systemUserModel.getId(),
                systemUserModel.getInstitutionId(),
                systemUserModel.getFirstName(),
                systemUserModel.getLastName(),
                systemUserModel.getEmail(),
                systemUserModel.getRoleCode(),
                systemUserModel.getCreatedAt(),
                systemUserModel.getLastLoggedInAt(),
                systemUserModel.getIsActive(),
                systemUserModel.getUsername()
        );
    }
}
