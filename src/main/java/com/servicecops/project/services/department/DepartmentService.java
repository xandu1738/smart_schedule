package com.servicecops.project.services.department;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.Department;
import com.servicecops.project.models.database.Institution;
import com.servicecops.project.models.jpahelpers.enums.AppDomains;
import com.servicecops.project.repositories.DepartmentRepository;
import com.servicecops.project.repositories.InstitutionRepository;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.services.department.Dtos.DepartmentResponseDTO;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.AuthorizationRequiredException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
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

    Integer institutionId = null;

    Department newDepartment = new Department();

    if (getUserDomain() == AppDomains.INSTITUTION) {
      institutionId = authenticatedUser().getInstitutionId();
    }

    if (getUserDomain() == AppDomains.BACK_OFFICE) {
      institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());

    }



    if (institutionId == null) {
      throw new IllegalArgumentException("institutionId is required");
    }

    Boolean departmentExists = departmentRepository.existsByNameAndInstitutionId(departmentName, institutionId.longValue());


    if (departmentExists) {
      throw new IllegalArgumentException("Department with name " + departmentName + " already exists");
    }

    newDepartment.setInstitutionId(institutionId.longValue());
    newDepartment.setName(data.getString("name"));
    newDepartment.setManagerName(managerName);
    newDepartment.setNoOfEmployees(noOfEmployees);
    newDepartment.setDescription(description);
    newDepartment.setCreatedAt(Instant.now());
    newDepartment.setActive(true);
    newDepartment.setCreatedBy(authenticatedUser().getId());

    departmentRepository.save(newDepartment);

    System.out.println("New department: " + newDepartment.getName() + "successfully created");


    DepartmentResponseDTO response = new DepartmentResponseDTO();
    response.setId(newDepartment.getId());
    response.setName(newDepartment.getName());
    response.setDescription(newDepartment.getDescription());
    response.setCreatedAt(newDepartment.getCreatedAt());
    response.setActive(newDepartment.isActive());

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, newDepartment.getName() + " successfully added", newDepartment);

    return res;
  }
  public OperationReturnObject getAll(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());

    Long institutionId = null;
    List<Department> departments = new ArrayList<>();

    if( getUserDomain() == AppDomains.BACK_OFFICE) {
//      requires(data,Params.INSTITUTION_ID.getLabel());
      departments = departmentRepository.findAll();
    }

    if (getUserDomain() == AppDomains.INSTITUTION) {
      institutionId = authenticatedUser().getInstitutionId().longValue();
      if(institutionId == null) {
        throw new IllegalArgumentException("institution id not found: " + institutionId);
      }
      departments = departmentRepository.findByInstitutionId(institutionId).orElseThrow(() -> new IllegalArgumentException("not found "));
    }

    List<DepartmentResponseDTO> response = departments.stream()
      .map(dep -> new DepartmentResponseDTO(
        dep.getId(),
        dep.getName(),
        dep.getInstitutionId(),
        dep.getDescription(),
        dep.getNoOfEmployees(),
        dep.getManagerName(),
        dep.getCreatedAt(),
        dep.isActive()
      ))
      .toList();

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,"data returned successfully", response);

    return res;
  }
  public OperationReturnObject findById(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires( request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data,Params.DEPARTMENT_ID.getLabel());

    Long departmentId = data.getLong(Params.DEPARTMENT_ID.getLabel());

    Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + request.getLong("id")));

    DepartmentResponseDTO response = new DepartmentResponseDTO().fromDepartment(department);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,"request successful", response);

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

    Department updatedDepartment = departmentRepository.save(department);


    DepartmentResponseDTO response = new DepartmentResponseDTO().fromDepartment(updatedDepartment);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,"Department edited successfully" ,response);

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

    DepartmentResponseDTO response = new DepartmentResponseDTO();
    response.setId(department.getId());
    response.setName(department.getName());
    response.setDescription(department.getDescription());
//    response.setCreatedAt(department.getCreatedAt());
    response.setActive(department.isActive());


    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,"edited successfully" ,response);

    return res;
  }


}
