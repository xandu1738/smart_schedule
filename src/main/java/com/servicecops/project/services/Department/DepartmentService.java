package com.servicecops.project.services.Department;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.Department;
import com.servicecops.project.repositories.DepartmentRepository;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@RequiredArgsConstructor
@Service
public class DepartmentService extends BaseWebActionsService {

  private final DepartmentRepository departmentRepository;

  @RequiredArgsConstructor
  @Getter
  private enum Params {
    ID("id"),
    INSTITUTION_ID("institution_id"),
    DEPARTMENT_ID("department_id"),
    NAME("name"),
    TYPE("type"),
    CREATED_BY("created_by"),
    MAX_PEOPLE("max_people"),
    DATA("data"),

    ;
    private final String label;
  }


  @Override
  public OperationReturnObject switchActions(String action, JSONObject request) {
    return switch (action){
      case "save" -> this.add(request);
      case "getAll" -> this.getAll(request);
      case "getSingle" -> this.findById(request);
      case "edit" -> this.edit(request);
      case "delete" -> this.delete(request);

      default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
    };
  }


  public OperationReturnObject add(JSONObject request) {
    requiresAuth();
    requires(request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data,Params.NAME.getLabel(),Params.INSTITUTION_ID.getLabel());

    String departmentName = data.getString(Params.NAME.getLabel());
    Long institutionId = data.getLong(Params.INSTITUTION_ID.getLabel());

    Department department = departmentRepository.findByName(departmentName);

    if (department != null) {
      throw new IllegalArgumentException("department with name " + departmentName + " already exists.");
    }

    Department newDepartment = new Department();
    newDepartment.setName(data.getString("name"));
    newDepartment.setInstitutionId(institutionId);
    newDepartment.setCreatedAt(Instant.now());
    newDepartment.setActive(true);
    newDepartment.setCreatedBy(authenticatedUser().getId());

    departmentRepository.save(newDepartment);

    System.out.println("New department: " + newDepartment.getName() + "successfully created");
    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,newDepartment.getName() + " successfully added", newDepartment);

    return res;
  }

  public OperationReturnObject getAll(JSONObject request) {
    requiresAuth();
    requires(request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data,Params.INSTITUTION_ID.getLabel());

    Long institutionId = data.getLong(Params.INSTITUTION_ID.getLabel());

    List<Department> departments = departmentRepository.findByInstitutionId(institutionId);

    OperationReturnObject res = new OperationReturnObject();
    res.setReturnCodeAndReturnObject(200, departments);

    return res;
  }

  public OperationReturnObject findById(JSONObject request) {
    requiresAuth();

    Department departments = departmentRepository.findById(request.getLong("id")).orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + request.getLong("id")));

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,"request successful", departments);

    return res;
  }

  public OperationReturnObject edit(JSONObject request) {
    requiresAuth();

    requires(request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data,Params.NAME.getLabel(), Params.DEPARTMENT_ID.getLabel());

    Long departmentid = data.getLong(Params.DEPARTMENT_ID.getLabel());

    Department department = departmentRepository.findById(departmentid).orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + request.getLong("id")));

    if (data.containsKey("name") && data.getString("name") != null) {
      department.setName(data.getString("name"));
    }

    departmentRepository.save(department);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,"edited successfully" ,department);

    return res;
  }

  public OperationReturnObject delete(JSONObject request) {
    requiresAuth();
    requires(request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data,Params.DEPARTMENT_ID.getLabel());

    Long departmentId = data.getLong(Params.DEPARTMENT_ID.getLabel());

    Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + request.getLong("id")));

    department.setActive(false);
    departmentRepository.save(department);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,"edited successfully" ,department);

    return res;
  }


}
