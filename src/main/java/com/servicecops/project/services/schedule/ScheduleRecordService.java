package com.servicecops.project.services.schedule;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.AuthorizationRequiredException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ScheduleRecordService extends BaseWebActionsService {


  @RequiredArgsConstructor
  @Getter
  private enum Params {
    ID("id"),
    SCHEDULE_ID("schedule_id"),
    EMPLOYEE_ID("employee_id"),
    SHIFT_ID("shift_id"),
    DATE_CREATED("date_created"),
    TIME_OFF_ID("time_off_id"),
    DATA("data"),

    ;
    private final String label;
  }

  @Override
  public OperationReturnObject switchActions(String action, JSONObject request) throws AuthorizationRequiredException {
    return switch (action) {
      case "save" -> this.addScheduleRecord(request);
//      case "edit" -> this.addScheduleRecord(request);
//      case "delete" -> this.addScheduleRecord(request);
//      case "save" -> this.addScheduleRecord(request);
//      case "save" -> this.addScheduleRecord(request);


      default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
    };
  }


  public OperationReturnObject addScheduleRecord(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request, "data");
    JSONObject data = request.getJSONObject("data");
    requires(data, "id", "schedule_id", "employee_id", "shift_id", "date_created" ,"time_off_id");

    // Logic to create a schedule record
    // ...

    return new OperationReturnObject("Schedule created successfully");
  }
//
//  public OperationReturnObject addScheduleRecord(JSONObject request) {
//    requiresAuth();
//    requires(request, "data");
//    JSONObject data = request.getJSONObject("data");
//    requires(data, "id", "schedule_id", "employee_id", "shift_id", "date_created" ,"time_off_id");
//
//    // Logic to create a schedule record
//    // ...
//
//    return new OperationReturnObject("Schedule created successfully");
//  }
//
//  public OperationReturnObject addScheduleRecord(JSONObject request) {
//    requiresAuth();
//    requires(request, "data");
//    JSONObject data = request.getJSONObject("data");
//    requires(data, "id", "schedule_id", "employee_id", "shift_id", "date_created" ,"time_off_id");
//
//    // Logic to create a schedule record
//    // ...
//
//    return new OperationReturnObject("Schedule created successfully");
//  }
//
//  public OperationReturnObject addScheduleRecord(JSONObject request) {
//    requiresAuth();
//    requires(request, "data");
//    JSONObject data = request.getJSONObject("data");
//    requires(data, "id", "schedule_id", "employee_id", "shift_id", "date_created" ,"time_off_id");
//
//    // Logic to create a schedule record
//    // ...
//
//    return new OperationReturnObject("Schedule created successfully");
//  }

}
