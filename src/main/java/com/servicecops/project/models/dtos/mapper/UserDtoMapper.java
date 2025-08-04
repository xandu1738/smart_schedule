package com.servicecops.project.models.dtos.mapper;

import com.servicecops.project.models.database.Institution;
import com.servicecops.project.models.database.SystemRoleModel;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.models.dtos.UserDto;
import com.servicecops.project.repositories.InstitutionRepository;
import com.servicecops.project.repositories.SystemRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class UserDtoMapper implements Function<SystemUserModel, UserDto> {
    private final InstitutionRepository institutionRepository;
    private final SystemRoleRepository systemRoleRepository;

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

        SystemRoleModel role = systemRoleRepository.findByRoleCode(systemUserModel.getRoleCode())
                .orElseThrow(() -> new IllegalStateException("Role not found for code: " + systemUserModel.getRoleCode()));

        return new UserDto(
                systemUserModel.getId(),
                name,
                systemUserModel.getFirstName(),
                systemUserModel.getLastName(),
                systemUserModel.getEmail(),
                systemUserModel.getRoleCode(),
                role.getRoleDomain().name(),
                systemUserModel.getCreatedAt(),
                systemUserModel.getLastLoggedInAt(),
                systemUserModel.getIsActive(),
                systemUserModel.getUsername()
        );
    }
}
