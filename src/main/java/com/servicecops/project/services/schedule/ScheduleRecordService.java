package com.servicecops.project.services.schedule;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;

public class ScheduleRecordService extends BaseWebActionsService {
  @Override
  public OperationReturnObject switchActions(String action, JSONObject request) {
    return switch (action) {
      case "save" -> this.addScheduleRecord(request);
//      case "edit" -> this.addScheduleRecord(request);
//      case "delete" -> this.addScheduleRecord(request);
//      case "save" -> this.addScheduleRecord(request);
//      case "save" -> this.addScheduleRecord(request);


      default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
    };
  }


  public OperationReturnObject addScheduleRecord(JSONObject request) {
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
