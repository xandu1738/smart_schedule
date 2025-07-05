package com.servicecops.project.services.Employee;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.Employee;
import com.servicecops.project.repositories.EmployeeRepository;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmployeeService extends BaseWebActionsService {
    @RequiredArgsConstructor
    @Getter
    private enum Params {
        ID("id"),
        NAME("name"),
        DEPARTMENT_ID("department"),
        EMAIL("email"),
        STATUS("status"),
        DAYS_OFF_USED("days_off_used"),
        CREATED_BY("created_by"),
        ACTIVE("active"),
        DATA("data"),;

        private final String label;
    }

    final EmployeeRepository employeeRepository;

    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch(action){
            case "save" -> this.save(request);

            default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
        };
    }

    private OperationReturnObject save(JSONObject request) {
        requiresAuth();
        requires(request,Params.DATA.getLabel());
        JSONObject data = request.getJSONObject(Params.DATA.getLabel());
        requires(data,Params.NAME.getLabel(), Params.EMAIL.getLabel(),
                Params.DEPARTMENT_ID.getLabel(), Params.STATUS.getLabel(),
                Params.DAYS_OFF_USED.getLabel());

        String email = data.getString(Params.EMAIL.getLabel());
        Optional<Employee> employee = employeeRepository.findByEmail(email);

        if (employee.isPresent()) {
            throw new IllegalArgumentException("Employee with email " + email + " already exists.");
        }

        Employee newEmployee = new Employee();
        newEmployee.setName(data.getString(Params.NAME.getLabel()));
        newEmployee.setDepartment(data.getInteger(Params.DEPARTMENT_ID.getLabel()));
        newEmployee.setEmail(data.getString(Params.EMAIL.getLabel()));
        newEmployee.setStatus(data.getString(Params.STATUS.getLabel()));
        newEmployee.setCreatedAt(Instant.now());
        newEmployee.setDaysOffUsed(data.getInteger(Params.DAYS_OFF_USED.getLabel()));
        newEmployee.setCreatedBy(authenticatedUser().getId());
        employeeRepository.save(newEmployee);

        OperationReturnObject res = new OperationReturnObject();
        res.setCodeAndMessageAndReturnObject(200,newEmployee.getEmail() + " successfully added", newEmployee);

        return res;


    }
}
