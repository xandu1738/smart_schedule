package com.servicecops.project.services.Employee;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.Employee;
import com.servicecops.project.repositories.EmployeeRepository;
import com.servicecops.project.services.Department.DepartmentService;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
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
            case "getEmployees" -> this.getAll(request);
            case "edit" -> this.edit(request);
            case "delete" -> this.delete(request);
            case "getEmployee" -> this.findById(request);

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
        newEmployee.setCreatedAt(Timestamp.from(Instant.now()));
        newEmployee.setDaysOffUsed(data.getInteger(Params.DAYS_OFF_USED.getLabel()));
        newEmployee.setCreatedBy(authenticatedUser().getId());
        employeeRepository.save(newEmployee);

        OperationReturnObject res = new OperationReturnObject();
        res.setCodeAndMessageAndReturnObject(200,newEmployee.getEmail() + " successfully added", newEmployee);

        return res;
    }

    public OperationReturnObject getAll(JSONObject request) {
        requiresAuth();
        requires(request, EmployeeService.Params.DATA.getLabel());
        JSONObject data = request.getJSONObject(EmployeeService.Params.DATA.getLabel());
        requires(data, Params.DEPARTMENT_ID.getLabel());

        Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());
        List<Employee> employees = employeeRepository.findByDepartmentAndActive(departmentId, true);

        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnObject(200, employees);
        return res;
    }

    public OperationReturnObject edit(JSONObject request) {
        requiresAuth();
        requires(request,Params.DATA.getLabel());
        JSONObject data = request.getJSONObject(Params.DATA.getLabel());
        requires(data,Params.ID.getLabel(), Params.NAME.getLabel(), Params.EMAIL.getLabel(),
                Params.DEPARTMENT_ID.getLabel(), Params.STATUS.getLabel(),
                Params.DAYS_OFF_USED.getLabel());

        Long employeeId = data.getLong(Params.ID.getLabel());
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + request.getLong("id")));

        if (data.containsKey("name") && data.getString("name") != null) {
            employee.setName(data.getString("name"));
        }

        if (data.containsKey("email") && data.getString("email") != null) {
            employee.setEmail(data.getString("email"));
        }

        if (data.containsKey("department") && data.getInteger("department") != null) {
            employee.setDepartment(data.getInteger("department"));
        }

        if (data.containsKey("status") && data.getString("status") != null) {
            employee.setStatus(data.getString("status"));
        }

        if (data.containsKey("days_off_used") && data.getInteger("days_off_used") != null) {
            employee.setDaysOffUsed(data.getInteger("days_off_used"));
        }

        if (data.containsKey("active") && data.getBoolean("active") != null) {
            employee.setActive(data.getBoolean("active"));
        }

        employee.setUpdatedAt(Timestamp.from(Instant.now()));
        employee.setUpdatedBy(authenticatedUser().getId());
        employeeRepository.save(employee);

        OperationReturnObject res = new OperationReturnObject();
        res.setCodeAndMessageAndReturnObject(200,"edited successfully" ,employee);

        return res;
    }

    public OperationReturnObject delete(JSONObject request) {
        requiresAuth();
        requires(request,Params.DATA.getLabel());
        JSONObject data = request.getJSONObject(Params.DATA.getLabel());
        requires(data,Params.ID.getLabel());

        Long employeeId = data.getLong(Params.ID.getLabel());

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + request.getLong("id")));

        employee.setActive(false);
        employeeRepository.save(employee);

        OperationReturnObject res = new OperationReturnObject();
        res.setCodeAndMessageAndReturnObject(200,"deleted successfully" ,employee);

        return res;
    }

    public OperationReturnObject findById(JSONObject request) {
        requiresAuth();
        requires(request,Params.DATA.getLabel());
        JSONObject data = request.getJSONObject(Params.DATA.getLabel());
        requires(data,Params.ID.getLabel());

        Long employeeId = data.getLong(Params.ID.getLabel());

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + request.getLong("id")));

        OperationReturnObject res = new OperationReturnObject();
        res.setCodeAndMessageAndReturnObject(200, "", employee);

        return res;
    }
}
