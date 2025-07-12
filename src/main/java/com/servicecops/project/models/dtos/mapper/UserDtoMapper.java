package com.servicecops.project.models.dtos.mapper;

import com.servicecops.project.models.database.Institution;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.models.dtos.UserDto;
import com.servicecops.project.repositories.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class UserDtoMapper implements Function<SystemUserModel, UserDto> {
    private final InstitutionRepository institutionRepository;
    @Override
    public UserDto apply(SystemUserModel systemUserModel) {
        Integer institutionId = systemUserModel.getInstitutionId();
        String name = null;
        if (institutionId != null) {
            Optional<Institution> institution = institutionRepository.findById(institutionId.longValue());

            if (institution.isPresent()) {
                name = institution.get().getName();
            }
        }

        return new UserDto(
                systemUserModel.getId(),
                name,
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
