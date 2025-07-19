package com.servicecops.project.utils;

import com.servicecops.project.models.database.SystemDomainModel;
import com.servicecops.project.models.database.SystemPermissionModel;
import com.servicecops.project.models.database.SystemRoleModel;
import com.servicecops.project.models.database.SystemRolePermissionAssignmentModel;
import com.servicecops.project.models.jpahelpers.enums.AppDomains;
import com.servicecops.project.models.jpahelpers.enums.DefaultPermissions;
import com.servicecops.project.models.jpahelpers.enums.DefaultRoles;
import com.servicecops.project.repositories.SystemDomainRepository;
import com.servicecops.project.repositories.SystemPermissionRepository;
import com.servicecops.project.repositories.SystemRolePermissionRepository;
import com.servicecops.project.repositories.SystemRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

/**
 * This class runs on every app boot to set up all the defaults likes permissions, domains, etc.
 * To add more actions that shall always run on app start, create a method in here and
 * annotate it with @Bean
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SetUp {
    private final SystemRolePermissionRepository systemRolePermissionRepository;
    // Turn on or off system domains
    @Value("${USE_DOMAINS:true}")
    Boolean useDomains;

    @Value("${ADMIN_ROLE_NAME}")
    String adminRoleName;

    @Value("${ADMIN_ROLE_DOMAIN}")
    AppDomains adminDomain;

    private final SystemDomainRepository domainRepository;
    private final SystemPermissionRepository permissionRepository;
    private final SystemRolePermissionRepository permissionAssignmentRepository;
    private final SystemRoleRepository roleRepository;

    @PostConstruct
    protected void setupDomains() {
        if (Boolean.TRUE.equals(useDomains)) {
            log.info("Domains supported, setting them up.");
            domainRepository.deleteAll();
            for (AppDomains domain : AppDomains.values()) {
                // check if domain exists orElse create it
                log.info("Adding {} domain", domain.name());
                var md = SystemDomainModel.builder();
                md.domainName(String.valueOf(domain));
                domainRepository.save(md.build());
            }
            log.debug("Domains setup successfully");
        } else {
            log.info("Domains are currently inactive.");
        }
    }

    @PostConstruct
    public void setupRoles() {
        DefaultRoles[] values = DefaultRoles.values();
        //add roles to db
        for (DefaultRoles value : values) {
            Optional<SystemRoleModel> existingRole = roleRepository.findFirstByRoleCode(value.name());

            if (existingRole.isEmpty()) {
                SystemRoleModel role = SystemRoleModel.builder()
                        .roleName(value.name())
                        .roleCode(value.name())
                        .roleDomain(value.getDomain())
                        .build();
                roleRepository.save(role);
                log.info("Role {} added successfully", role.getRoleName());
                return;
            }

            SystemRoleModel role = existingRole.get();
            if (role.getRoleDomain() == null){
                role.setRoleDomain(value.getDomain());
                roleRepository.save(role);
            }
        }
    }

    @PostConstruct
    public void setupPermissions() {
        permissionRepository.deleteAll();
        DefaultPermissions[] perms = DefaultPermissions.values();

        for (DefaultPermissions perm : perms) {
            log.info("Adding {} permission", perm.name());
            SystemPermissionModel permissionsModel = new SystemPermissionModel();
            permissionsModel.setPermissionCode(perm.name());
            permissionsModel.setPermissionName(perm.name().replace("_", " "));
            permissionsModel.setPermissionDomain(AppDomains.BACK_OFFICE);
            if (Boolean.TRUE.equals(useDomains)) {
                permissionsModel.setPermissionDomain(perm.getDomain());
            }
            permissionRepository.save(permissionsModel);
            log.info("{} permission added successfully", perm.name());
        }
        setUpDefaultRolePerms();
    }

    /**
     * By default, the system creates the first role of ADMINISTRATOR; therefore, this method is to assign it its default permissions.
     * This will assign all the permissions that set 'shipWithAdmin' to true.
     */
    private void setUpDefaultRolePerms() {
        DefaultRoles[] roles = DefaultRoles.values();
        for (DefaultRoles role : roles) {
            role.getPermissions().forEach(permission -> {
                log.info("Assigning {} permission to {} role", permission.name(), role.getRoleName());
                if (!Objects.equals(role.getDomain(), permission.getDomain())) {
                    log.warn("Permission {} does not match role domain {}. Skipping assignment.", permission.name(), role.getDomain());
                    return; // Skip if the permission's domain does not match the role's domain
                }
                Optional<SystemRolePermissionAssignmentModel> rolePerms = systemRolePermissionRepository.findFirstByRoleCodeAndPermissionCode(role.name(), permission.name());

                if (rolePerms.isPresent()){
                    log.info("Permission {} already assigned to role {}", permission.name(), role.getRoleName());
                    return; // Skip if the permission is already assigned to the role
                }
                SystemRolePermissionAssignmentModel assignment = new SystemRolePermissionAssignmentModel();
                assignment.setRoleCode(role.getCode());
                assignment.setPermissionCode(permission.name());
                systemRolePermissionRepository.save(assignment);
                log.info("{} permission assigned to {} role successfully", permission.name(), role.getRoleName());
            });
        }

    }
}
