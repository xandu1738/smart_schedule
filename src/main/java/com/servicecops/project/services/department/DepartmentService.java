package com.servicecops.project.services.department;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.Department;
import com.servicecops.project.models.database.Institution;
import com.servicecops.project.models.jpahelpers.enums.AppDomains;
import com.servicecops.project.repositories.DepartmentRepository;
import com.servicecops.project.repositories.InstitutionRepository;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.AuthorizationRequiredException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@RequiredArgsConstructor
@Service
public class DepartmentService extends BaseWebActionsService {

  private final DepartmentRepository departmentRepository;
  private final InstitutionRepository institutionRepository;

  @RequiredArgsConstructor
  @Getter
  private enum Params {
    ID("id"),
    INSTITUTION_ID("institutionId"),
    DEPARTMENT_ID("departmentId"),
    DESCRIPTION("description"),
    NO_OF_EMPLOYEES("noOfEmployees"),
    MANAGER_NAME("managerName"),

    NAME("name"),
    TYPE("type"),
    CREATED_BY("createdBy"),
    MAX_PEOPLE("maxPeople"),
    DATA("data"),

    ;
    private final String label;
  }


  @Override
  public OperationReturnObject switchActions(String action, JSONObject request) throws AuthorizationRequiredException {
    return switch (action){
      case "save" -> this.add(request);
      case "getAll" -> this.getAll(request);
      case "getSingle" -> this.findById(request);
      case "edit" -> this.edit(request);
      case "delete" -> this.delete(request);

      default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
    };
  }


  public OperationReturnObject add(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());

    requires(data, Params.NAME.getLabel(), Params.MANAGER_NAME.getLabel(),
      Params.NO_OF_EMPLOYEES.getLabel(), Params.DESCRIPTION.getLabel());

    String departmentName = data.getString(Params.NAME.getLabel());
    String managerName = data.getString(Params.MANAGER_NAME.getLabel());
    Integer noOfEmployees = data.getInteger(Params.NO_OF_EMPLOYEES.getLabel());
    String description = data.getString(Params.DESCRIPTION.getLabel());
    Long institutionId = data.getLong(Params.INSTITUTION_ID.getLabel());


    Department department = departmentRepository.findByNameAndInstitutionId(departmentName, institutionId)
      .orElse(null);

    Institution institution = null;
    if (institution == null) {
      throw new IllegalArgumentException("institution not found with id: " + institutionId);
    }


    Department newDepartment = new Department();
    newDepartment.setName(data.getString("name"));

    if (getUserDomain() == AppDomains.BACK_OFFICE) {
      institution = institutionRepository.findById(institutionId).orElseThrow(() -> new IllegalArgumentException("department with name " + departmentName + " already exists."));
      newDepartment.setInstitutionId(institutionId);
    }

    if (getUserDomain() == AppDomains.INSTITUTION) {
      newDepartment.setInstitutionId(authenticatedUser().getInstitutionId().longValue());
    }

    newDepartment.setManagerName(managerName);
    newDepartment.setNoOfEmployees(noOfEmployees);
    newDepartment.setDescription(description);
    newDepartment.setCreatedAt(Instant.now());
    newDepartment.setActive(true);
    newDepartment.setCreatedBy(authenticatedUser().getId());

    departmentRepository.save(newDepartment);

    System.out.println("New department: " + newDepartment.getName() + "successfully created");
    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, newDepartment.getName() + " successfully added", newDepartment);

    return res;
  }

  public OperationReturnObject getAll(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());

    Long institutionId = null;

    if( getUserDomain() == AppDomains.BACK_OFFICE) {
      requires(data,Params.INSTITUTION_ID.getLabel());
      institutionId = data.getLong(Params.INSTITUTION_ID.getLabel());
    }

    if (getUserDomain() == AppDomains.BACK_OFFICE) {
      institutionId = authenticatedUser().getInstitutionId().longValue();
    }

    if(institutionId == null) {
      throw new IllegalArgumentException("institution id not found: " + institutionId);
    }

    List<Department> departments = departmentRepository.findByInstitutionId(institutionId).orElseThrow(() -> new IllegalArgumentException("not found "));;

    OperationReturnObject res = new OperationReturnObject();
    res.setReturnCodeAndReturnObject(200, departments);

    return res;
  }

  public OperationReturnObject findById(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires( request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data,Params.DEPARTMENT_ID.getLabel());

    Long departmentId = data.getLong(Params.DEPARTMENT_ID.getLabel());

    Department departments = departmentRepository.findById(departmentId).orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + request.getLong("id")));

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,"request successful", departments);

    return res;
  }

  public OperationReturnObject edit(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();

    requires(request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data,Params.DEPARTMENT_ID.getLabel());


    Long departmentId = data.getLong(Params.DEPARTMENT_ID.getLabel());

    Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + departmentId));

    if (data.containsKey(Params.NAME.getLabel()) && data.getString(Params.NAME.getLabel()) != null) {
      department.setName(data.getString(Params.NAME.getLabel()));
    }
    if (data.containsKey(Params.INSTITUTION_ID.getLabel()) && data.getLong(Params.INSTITUTION_ID.getLabel()) != null) {
      Long newInstitutionId = data.getLong(Params.INSTITUTION_ID.getLabel());
      Institution institution = institutionRepository.findById(newInstitutionId).orElse(null);
      if (institution == null) {
        throw new IllegalArgumentException("Institution not found with id: " + newInstitutionId);
      }
      department.setInstitutionId(newInstitutionId);
    }
    if (data.containsKey(Params.MANAGER_NAME.getLabel()) && data.getString(Params.MANAGER_NAME.getLabel()) != null) {
      department.setManagerName(data.getString(Params.MANAGER_NAME.getLabel()));
    }
    if (data.containsKey(Params.NO_OF_EMPLOYEES.getLabel()) && data.getInteger(Params.NO_OF_EMPLOYEES.getLabel()) != null) {
      department.setNoOfEmployees(data.getInteger(Params.NO_OF_EMPLOYEES.getLabel()));
    }
    if (data.containsKey(Params.DESCRIPTION.getLabel()) && data.getString(Params.DESCRIPTION.getLabel()) != null) {
      department.setDescription(data.getString(Params.DESCRIPTION.getLabel()));
    }

    departmentRepository.save(department);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,"Department edited successfully" ,department);

    return res;
  }
  public OperationReturnObject delete(JSONObject request) throws AuthorizationRequiredException {
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
