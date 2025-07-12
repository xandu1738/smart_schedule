package com.servicecops.project.services.institutions;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.Institution;
import com.servicecops.project.repositories.InstitutionRepository;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class InstitutionService extends BaseWebActionsService {

  private final InstitutionRepository institutionRepository;


  @RequiredArgsConstructor
  @Getter
  private enum Params {
    ID("id"),
    CODE("code"),
    NAME("name"),
    DATA("data"),
    DESCRIPTION("description"),
    OWNER_NAME("ownerName"),
    LOCATION("location"),
    REG_NO("regNo"),
    YEAR_ESTABLISHED("yearEstablished"),
    INSTITUTION_TYPE("institutionType"),
    ;
    private final String label;
  }


  @Override
  public OperationReturnObject switchActions(String action, JSONObject request) {
    return switch (action) {
      case "save" -> this.save(request);
      case "getAll" -> this.getAll(request);
      case "getSingle" -> this.findById(request);
      case "edit" -> this.edit(request);
      case "delete" -> this.delete(request);

      default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
    };
  }


  public OperationReturnObject save(JSONObject request) {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());

    requires(data, Params.NAME.getLabel());
    requires(data, Params.DESCRIPTION.getLabel());
    requires(data, Params.OWNER_NAME.getLabel());
    requires(data, Params.LOCATION.getLabel());
    requires(data, Params.REG_NO.getLabel());
    requires(data, Params.YEAR_ESTABLISHED.getLabel());
    requires(data, Params.INSTITUTION_TYPE.getLabel());

    String name = data.getString(Params.NAME.getLabel());
    String description = data.getString(Params.DESCRIPTION.getLabel());
    String ownerName = data.getString(Params.OWNER_NAME.getLabel());
    String location = data.getString(Params.LOCATION.getLabel());
    String regNo = data.getString(Params.REG_NO.getLabel());
    String yearEstablished = data.getString(Params.YEAR_ESTABLISHED.getLabel());
    String institutionType = data.getString(Params.INSTITUTION_TYPE.getLabel());

    String code = name.replaceAll(" ", "_").toUpperCase();

    if (institutionRepository.findByName(name).isPresent()) {
      throw new IllegalArgumentException("Institution with name '" + name + "' already exists.");
    }

    if (institutionRepository.findByCode(code).isPresent()) {
      throw new IllegalArgumentException("Institution with code '" + code + "' already exists.");
    }

    if (institutionRepository.findByRegNo(regNo).isPresent()) {
      throw new IllegalArgumentException("Institution with registration number '" + regNo + "' already exists.");
    }

    Institution newInstitution = new Institution();
    newInstitution.setName(name);
    newInstitution.setCode(code);
    newInstitution.setDescription(description);
    newInstitution.setOwnerName(ownerName);
    newInstitution.setLocation(location);
    newInstitution.setRegNo(regNo);
    newInstitution.setYearEstablished(yearEstablished);
    newInstitution.setInstitutionType(institutionType);
    newInstitution.setCreatedAt(Instant.now());
    newInstitution.setCreatedBy(1L);
    newInstitution.setActive(true);

    institutionRepository.save(newInstitution);

    System.out.println("Successfully added new institution: " + newInstitution.getName() + " (Code: " + newInstitution.getCode() + ")");

    OperationReturnObject res = new OperationReturnObject();
    JSONObject responseObject = new JSONObject();
    responseObject.put("id", newInstitution.getId());
    responseObject.put("name", newInstitution.getName());
    responseObject.put("code", newInstitution.getCode());
    responseObject.put("description", newInstitution.getDescription());
    responseObject.put("ownerName", newInstitution.getOwnerName());
    responseObject.put("location", newInstitution.getLocation());
    responseObject.put("regNo", newInstitution.getRegNo());
    responseObject.put("yearEstablished", newInstitution.getYearEstablished());
    responseObject.put("institutionType", newInstitution.getInstitutionType());

    res.setCodeAndMessageAndReturnObject(200, newInstitution.getName() + " successfully added", responseObject);

    return res;
  }

  public OperationReturnObject getAll(JSONObject request) {
    requiresAuth();

    List<Institution> institutions = institutionRepository.findAll(); // Explicitly type List

    OperationReturnObject res = new OperationReturnObject();
    res.setReturnCodeAndReturnObject(200, institutions); // Returns list of Institution objects

    return res;
  }

  public OperationReturnObject findById(JSONObject request) {
    requiresAuth();
    requires(request, Params.ID.getLabel()); // Ensure ID is provided

    Long institutionId = request.getLong(Params.ID.getLabel());
    Institution institution = institutionRepository.findById(institutionId)
      .orElseThrow(() -> new IllegalArgumentException("Institution not found with ID: " + institutionId));

    OperationReturnObject res = new OperationReturnObject();
    res.setReturnCodeAndReturnObject(200, institution); // Returns single Institution object

    return res;
  }

  public OperationReturnObject edit(JSONObject request) {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data, Params.ID.getLabel()); // Ensure ID is present within 'data'

    Long institutionId = data.getLong(Params.ID.getLabel());

    Institution existingInstitution = institutionRepository.findById(institutionId)
      .orElseThrow(() -> new IllegalArgumentException("Institution not found with ID: " + institutionId));

    String newName = null;
    String newCode = null;
    String newDescription = null;
    String newOwnerName = null;
    String newLocation = null;
    String newRegNo = null;
    String newYearEstablished = null;
    String newInstitutionType = null;

    if (data.containsKey(Params.NAME.getLabel()) && data.getString(Params.NAME.getLabel()) != null) {
      newName = data.getString(Params.NAME.getLabel());
      newCode = newName.replaceAll(" ", "_").toUpperCase(); // Always derive code from name if name changes

      if (institutionRepository.findByName(newName).filter(inst -> !inst.getId().equals(institutionId)).isPresent()) {
        throw new IllegalArgumentException("Another institution with name '" + newName + "' already exists.");
      }
    }


    if (data.containsKey(Params.CODE.getLabel()) && data.getString(Params.CODE.getLabel()) != null) {
      newCode = data.getString(Params.CODE.getLabel());

      if (institutionRepository.findByCode(newCode).filter(inst -> !inst.getId().equals(institutionId)).isPresent()) {
        throw new IllegalArgumentException("Another institution with code '" + newCode + "' already exists.");
      }
    }

    if (data.containsKey(Params.REG_NO.getLabel()) && data.getString(Params.REG_NO.getLabel()) != null) {
      newRegNo = data.getString(Params.REG_NO.getLabel());
      if (institutionRepository.findByRegNo(newRegNo).filter(inst -> !inst.getId().equals(institutionId)).isPresent()) {
        throw new IllegalArgumentException("Another institution with registration number '" + newRegNo + "' already exists.");
      }
    }

    if (data.containsKey(Params.DESCRIPTION.getLabel()) && data.getString(Params.DESCRIPTION.getLabel()) != null) {
      newDescription = data.getString(Params.DESCRIPTION.getLabel());
    }

    if (data.containsKey(Params.OWNER_NAME.getLabel()) && data.getString(Params.OWNER_NAME.getLabel()) != null) {
      newOwnerName = data.getString(Params.OWNER_NAME.getLabel());
    }

    if (data.containsKey(Params.LOCATION.getLabel()) && data.getString(Params.LOCATION.getLabel()) != null) {
      newLocation = data.getString(Params.LOCATION.getLabel());
    }

    if (data.containsKey(Params.YEAR_ESTABLISHED.getLabel()) && data.getInteger(Params.YEAR_ESTABLISHED.getLabel()) != null) {
      newYearEstablished = data.getString(Params.YEAR_ESTABLISHED.getLabel()); // Corrected to getInteger
    }

    if (data.containsKey(Params.INSTITUTION_TYPE.getLabel()) && data.getString(Params.INSTITUTION_TYPE.getLabel()) != null) {
      newInstitutionType = data.getString(Params.INSTITUTION_TYPE.getLabel());
    }

    if (newName != null) {
      existingInstitution.setName(newName);
    }
    if (newCode != null) {
      existingInstitution.setCode(newCode);
    }
    if (newDescription != null) {
      existingInstitution.setDescription(newDescription);
    }
    if (newOwnerName != null) {
      existingInstitution.setOwnerName(newOwnerName);
    }
    if (newLocation != null) {
      existingInstitution.setLocation(newLocation);
    }
    if (newRegNo != null) {
      existingInstitution.setRegNo(newRegNo);
    }
    if (newYearEstablished != null) {
      existingInstitution.setYearEstablished(newYearEstablished);
    }
    if (newInstitutionType != null) {
      existingInstitution.setInstitutionType(newInstitutionType);
    }

    institutionRepository.save(existingInstitution);

    OperationReturnObject res = new OperationReturnObject();
    JSONObject responseObject = new JSONObject();
    responseObject.put("id", existingInstitution.getId());
    responseObject.put("name", existingInstitution.getName());
    responseObject.put("code", existingInstitution.getCode());
    responseObject.put("description", existingInstitution.getDescription());
    responseObject.put("ownerName", existingInstitution.getOwnerName());
    responseObject.put("location", existingInstitution.getLocation());
    responseObject.put("regNo", existingInstitution.getRegNo());
    responseObject.put("yearEstablished", existingInstitution.getYearEstablished());
    responseObject.put("institutionType", existingInstitution.getInstitutionType());

    res.setCodeAndMessageAndReturnObject(200, "Institution with ID " + institutionId + " successfully updated", responseObject);

    return res;
  }
  public OperationReturnObject delete(JSONObject request) {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data, Params.ID.getLabel());
    Long institutionId = data.getLong(Params.ID.getLabel());

    Institution institution = institutionRepository.findById(institutionId)
      .orElseThrow(() -> new IllegalArgumentException("Institution not found with ID: " + institutionId)); // Use ID from validated param

    institution.setActive(false);
    institutionRepository.save(institution);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, "Institution with ID " + institutionId + " successfully deactivated", null); // Return null for object on delete/deactivate

    return res;
  }
}
