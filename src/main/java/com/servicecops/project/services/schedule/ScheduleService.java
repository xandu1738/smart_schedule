package com.servicecops.project.services.schedule;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturn;
import com.servicecops.project.utils.OperationReturnObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class ScheduleService extends BaseWebActionsService {


  @RequiredArgsConstructor
  @Getter
  private enum Params {
    ID("id"),
    INSTITUTION_ID("institution_id"),
    DEPARTMENT_ID("department_id"),
    NAME("name"),
    START_TIME("start_time"),
    END_TIME("end_time"),
    CREATED_BY("created_by"),
    MAX_PEOPLE("max_people"),
    DATA("data"),

    ;
    private final String label;
  }

  @Override
  public OperationReturnObject switchActions(String action, JSONObject request) {
    return switch (action) {
      case "save" -> this.createSchedule(request);

      default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
    };
  }

  public OperationReturnObject createSchedule(JSONObject request) {
    requiresAuth();

    return null;
  }


}
