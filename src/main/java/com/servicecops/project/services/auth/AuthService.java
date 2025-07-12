package com.servicecops.project.services.auth;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.config.ApplicationConf;
import com.servicecops.project.config.JwtUtility;
import com.servicecops.project.models.database.Institution;
import com.servicecops.project.models.database.SystemRolePermissionAssignmentModel;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.models.dtos.UserDto;
import com.servicecops.project.models.dtos.mapper.UserDtoMapper;
import com.servicecops.project.models.jpahelpers.enums.DefaultRoles;
import com.servicecops.project.repositories.SystemRolePermissionRepository;
import com.servicecops.project.repositories.SystemUserRepository;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.AuthorizationRequiredException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class AuthService extends BaseWebActionsService {
    private final AuthenticationManager authenticationManager;
    private final ApplicationConf userDetailService;
    private final JwtUtility jwtUtility;
    private final PasswordEncoder passwordEncoder;
    private final SystemUserRepository systemUserRepository;
    private final UserDtoMapper userDtoMapper;
    private final SystemRolePermissionRepository systemRolePermissionRepository;

    private OperationReturnObject login(JSONObject request) {
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");

        OperationReturnObject res = new OperationReturnObject();

        requires(data, "email", "password");

        String email = data.getString("email");
        String password = data.getString("password");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            res.setReturnCodeAndReturnMessage(401, "Invalid email or password");
            return res;
        }

        final SystemUserModel userDetails = userDetailService.loadUserByUsername(email);
        final String accessToken = jwtUtility.generateAccessToken(userDetails, "ACCESS");
        final String refreshToken = jwtUtility.generateAccessToken(userDetails, "REFRESH");

        UserDto profile = userDtoMapper.apply(userDetails);

        //For consistency with other user querying calls
        List<Map<String, Object>> permission = systemRolePermissionRepository.findPermissionsByRoleCode(userDetails.getRoleCode());
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken); // this is the jwt token the user can user from now on.
        response.put("refreshToken", refreshToken); // this is the jwt token the user can user from now on.
        response.put("user", profile);
        response.put("permissions", permission);
        res.setReturnCodeAndReturnMessage(200, "Welcome back " + userDetails.getUsername());
        res.setReturnObject(response);

        return res;
    }

    private OperationReturnObject signUp(JSONObject request) throws AuthorizationRequiredException {
        requiresAuth();
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");
        requires(data, "role", "first_name", "last_name", "email", "password");
        String role = data.getString("role");
        String firstName = data.getString("first_name");
        String lastName = data.getString("last_name");
        String email = data.getString("email");
        Long institution = data.getLong("institution");
        String password = data.getString("password");

        if (StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName)) {
            throw new IllegalArgumentException("First name and last name are required");
        }

//        if (!EnumUtils.isValidEnum(DefaultRoles.class, role)) {
//            throw new IllegalArgumentException("Role is not valid");
//        }

        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!Objects.equals(role, DefaultRoles.SUPER_ADMIN.name()) && institution == null) {
            throw new IllegalArgumentException("Institution is required");
        }

        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password is required");
        }

        Institution institutionDetails = null;
        if (!Objects.equals(role, DefaultRoles.SUPER_ADMIN.name())) {
            institutionDetails = getInstitution(institution);
        }
        SystemUserModel user = new SystemUserModel();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setInstitutionId(
                institutionDetails != null ? institutionDetails.getId() : null
        );
        user.setRoleCode(role);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(getCurrentTimestamp());
        user.setIsActive(true);

        systemUserRepository.save(user);

        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnMessage(200, "User created successfully");
        return res;
    }

    private OperationReturnObject usersList(JSONObject request) throws AuthorizationRequiredException {
        requiresAuth();
        JSONObject search = request.getJSONObject("search");

        if (search == null) {
            search = new JSONObject();
        }

//        List<Map<String,Object>> users = systemUserRepository.findAllUsers();
        List<UserDto> users = systemUserRepository.findAll().stream().map(userDtoMapper).toList();

        OperationReturnObject returnObject = new OperationReturnObject();
        returnObject.setReturnCodeAndReturnMessage(200, "Users list successfully");
        returnObject.setReturnObject(users);
        return returnObject;
    }

    private OperationReturnObject usersProfile(JSONObject request) throws AuthorizationRequiredException {
        requiresAuth();
        JSONObject search = request.getJSONObject("search");

        if (search == null) {
            search = new JSONObject();
        }

        Long id = search.getLong("id");
        if (id == null) {
            throw new IllegalStateException("Please specify user's ID");
        }

        var user = systemUserRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalFormatFlagsException("User profile does not exist")
                );

        UserDto userDto = userDtoMapper.apply(user);

        OperationReturnObject returnObject = new OperationReturnObject();
        returnObject.setReturnCodeAndReturnMessage(200, "Users list successfully");
        returnObject.setReturnObject(userDto);
        return returnObject;
    }

    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) throws AuthorizationRequiredException {
        return switch (action) {
            case "login" -> login(request);
            case "register" -> signUp(request);
            case "usersList" -> usersList(request);
            case "profile" -> usersProfile(request);
            default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
        };
    }
}
