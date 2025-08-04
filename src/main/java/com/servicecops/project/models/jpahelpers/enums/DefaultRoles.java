package com.servicecops.project.models.jpahelpers.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
public enum DefaultRoles {
    EMPLOYEE("EMPLOYEE", "Employee", AppDomains.INSTITUTION,
            Collections.emptyList()),
    INSTITUTION_ADMIN("INSTITUTION_ADMIN", "Institution Admin", AppDomains.INSTITUTION, Collections.emptyList()),
    DEPARTMENT_ADMIN("DEPARTMENT_ADMIN", "Department Admin", AppDomains.INSTITUTION,Collections.emptyList()),
    SUPER_ADMIN("SUPER_ADMIN", "Super Admin", AppDomains.BACK_OFFICE,
            List.of(DefaultPermissions.ASSIGNS_PERMISSIONS, DefaultPermissions.ADMINISTRATOR));

    final String code;
    final String roleName;
    final AppDomains domain;
    final List<DefaultPermissions> permissions;
}
