package com.servicecops.project.services;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.services.department.DepartmentService;
import com.servicecops.project.services.Employee.EmployeeService;
import com.servicecops.project.services.auth.AuthService;
import com.servicecops.project.services.institutions.InstitutionService;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.AuthorizationRequiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebActionsService {

    private final AuthService authService;
    private final InstitutionService institutionService;
    private final DepartmentService departmentService;
    private final ShiftManagementService shiftManagementService;
    private final EmployeeService employeeService;

    public OperationReturnObject processAction(String service, String action, JSONObject payload) throws AuthorizationRequiredException {
        return switch (service) {
            case "Auth" -> authService.process(action, payload);
            case "Institution" -> institutionService.process(action, payload);
            case "Department" -> departmentService.process(action, payload);
            case "Shift" -> shiftManagementService.process(action, payload);
            case "Employee" -> employeeService.process(action, payload);

            default -> {
                OperationReturnObject res = new OperationReturnObject();
                res.setReturnCodeAndReturnMessage(404, "UNKNOWN SERVICE");
                yield res;
            }
        };
    }
}
