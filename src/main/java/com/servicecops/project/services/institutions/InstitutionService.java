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

    ;
    private final String label;
  }


  @Override
  public OperationReturnObject switchActions(String action, JSONObject request) {
    return switch (action){
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
    requires(request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data,Params.NAME.getLabel());

    String name = data.getString(Params.NAME.getLabel());
    String code = name.replaceAll(" ", "_").toUpperCase();

    Institution institution = institutionRepository.findByCode(code);

    if (institution != null) {
      throw new IllegalArgumentException("Institution with code " + code + " already exists.");
    }

    Institution newInstitution = new Institution();
    newInstitution.setName(data.getString("name"));
    newInstitution.setCode(code);
    newInstitution.setCreatedAt(Instant.now());
    newInstitution.setCreatedBy(1L);
    newInstitution.setActive(true);

    institutionRepository.save(newInstitution);

    System.out.println("New Institution: " + newInstitution.getName() + " with code: " + newInstitution.getCode());
    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,newInstitution.getName() + " successfully added", newInstitution);

    return res;
  }

  public OperationReturnObject getAll(JSONObject request) {
    requiresAuth();

    List institutions = institutionRepository.findAll();

    OperationReturnObject res = new OperationReturnObject();
    res.setReturnCodeAndReturnObject(200, institutions);

    return res;
  }

  public OperationReturnObject findById(JSONObject request) {
    requiresAuth();

    Institution institutions = institutionRepository.findById(request.getLong("id")).orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + request.getLong("id")));

    OperationReturnObject res = new OperationReturnObject();
    res.setReturnCodeAndReturnObject(200, institutions);

    return res;
  }

  public OperationReturnObject edit(JSONObject request) {
    requiresAuth();

    requires(request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data,Params.NAME.getLabel(),Params.ID.getLabel());

    Long institutionId = data.getLong("id");

    Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + request.getLong("id")));

    if (data.containsKey("code") && data.getString("code") != null) {
      institution.setCode(data.getString("code"));
    }

    if (data.containsKey("name") && data.getString("name") != null) {
      institution.setName(data.getString("name"));
    }

    institutionRepository.save(institution);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,institution.getName() + "edited successfully" ,institution);

    return res;
  }

  public OperationReturnObject delete(JSONObject request) {
    requiresAuth();
    requires(request,Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data,Params.ID.getLabel());
    Long institutionId = data.getLong(Params.ID.getLabel());

    Institution institution = institutionRepository.findById(institutionId).orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + request.getLong("id")));

    institution.setActive(false);
    institutionRepository.save(institution);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200,institution.getName() + " deleted successfully" ,institution);

    return res;
  }


}
