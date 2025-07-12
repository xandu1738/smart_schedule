package com.servicecops.project.services.schedule;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.Employee;
import com.servicecops.project.models.database.Institution;
import com.servicecops.project.models.database.Schedule;
import com.servicecops.project.models.database.ScheduleRecord;
import com.servicecops.project.repositories.*;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturn;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.AuthorizationRequiredException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class ScheduleService extends BaseWebActionsService {

  private final ScheduleRepository scheduleRepository;
  private final ScheduleRecordRepository scheduleRecordRepository;
  private final DepartmentRepository departmentRepository;
  private final InstitutionRepository institutionRepository;
  private final EmployeeRepository employeeRepository;



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
    START_TIME("startTime"),
    END_TIME("endTime"),
    SHIFT_ID("shiftId") // Add this
    ;
    private final String label;
  }

  @Override
  public OperationReturnObject switchActions(String action, JSONObject request) throws AuthorizationRequiredException {
    return switch (action) {
      case "save" -> this.createSchedule(request);
      case "edit" -> this.edit(request);
      case "delete" -> this.delete(request);
      case "findByInstitution" -> this.findByInstitutionId(request);
      case "findByDepartmentIdAndInstitutionId" -> this.findByDepartmentIdAndInstitutionId(request);

      default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
    };
  }


  public OperationReturnObject createSchedule(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();

    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data, Params.INSTITUTION_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
      Params.START_TIME.getLabel(), Params.END_TIME.getLabel());

    Integer institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());
    Timestamp startTimeRequest = null;
    Timestamp endTimeRequest = null;

    // Validate institution and department existence (optional but good practice)
    institutionRepository.findById(institutionId.longValue())
      .orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + institutionId));
    departmentRepository.findById(departmentId.longValue())
      .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + departmentId));


    if (StringUtils.isNotBlank(data.getString(Params.START_TIME.getLabel()))) {
      startTimeRequest = stringToTimestamp(data.getString(Params.START_TIME.getLabel()));
    }

    if (StringUtils.isNotBlank(data.getString(Params.END_TIME.getLabel()))) {
      endTimeRequest = stringToTimestamp(data.getString(Params.END_TIME.getLabel()));
    }


    Schedule newSchedule = new Schedule();
    newSchedule.setDepartmentId(departmentId);
    newSchedule.setInstitutionId(institutionId);
    newSchedule.setStartDate(startTimeRequest);
    newSchedule.setEndDate(endTimeRequest);

    scheduleRepository.save(newSchedule);

    Integer createdScheduleId = newSchedule.getId();

    List<Employee> employees = employeeRepository.findByDepartmentAndActiveTrue(departmentId)
      .orElse(new ArrayList<>());

    if (employees.isEmpty()) {

      OperationReturnObject res = new OperationReturnObject();
      res.setCodeAndMessageAndReturnObject(200, "Schedule created, but no active employees found for this department to assign shifts.", newSchedule);
      return res;

    }

    // 3. Iterate through each day within the schedule period and assign shifts
    LocalDate startLocalDate = LocalDate.ofInstant(startTimeRequest.toInstant(), ZoneId.systemDefault());
    LocalDate endLocalDate = LocalDate.ofInstant(endTimeRequest.toInstant(), ZoneId.systemDefault());

    List<ScheduleRecord> createdScheduleRecords = new ArrayList<>();

    for (LocalDate date = startLocalDate; !date.isAfter(endLocalDate); date = date.plusDays(1)) {
      for (Employee employee : employees) {
        ScheduleRecord scheduleRecord = new ScheduleRecord();
        scheduleRecord.setScheduleId(createdScheduleId);
        scheduleRecord.setEmployeeId(employee.getId());
        scheduleRecord.setShiftId(null);
        scheduleRecord.setTimeOffId(null);
        scheduleRecord.setActive(true);

        Instant dailyStartTime = date.atTime(startTimeRequest.toInstant().atZone(ZoneId.systemDefault()).toLocalTime())
          .atZone(ZoneId.systemDefault()).toInstant();
        Instant dailyEndTime = date.atTime(endTimeRequest.toInstant().atZone(ZoneId.systemDefault()).toLocalTime())
          .atZone(ZoneId.systemDefault()).toInstant();

        // overnight shifts
        if (dailyEndTime.isBefore(dailyStartTime)) {
          dailyEndTime = dailyEndTime.plus(1, ChronoUnit.DAYS);
        }


        scheduleRecord.setStartTime(Timestamp.from(dailyStartTime));
        scheduleRecord.setEndTime(Timestamp.from(dailyEndTime));

        createdScheduleRecords.add(scheduleRecord);
        scheduleRecordRepository.save(scheduleRecord);
      }
    }
    return null;
  }


  public OperationReturnObject findByInstitutionId(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data, Params.INSTITUTION_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
      Params.START_TIME.getLabel(), Params.END_TIME.getLabel(), Params.MAX_PEOPLE.getLabel());
    Integer institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());

    if (institutionId == null || departmentId == null) {
      OperationReturnObject res = new OperationReturnObject();
      res.setCodeAndMessageAndReturnObject(200, "missing fields required", null);
      return res;
    }


    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, "returned successfull ", null);
    return res;

  }

  public OperationReturnObject findByDepartmentIdAndInstitutionId(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data, Params.INSTITUTION_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
      Params.START_TIME.getLabel(), Params.END_TIME.getLabel(), Params.MAX_PEOPLE.getLabel());
    Integer institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());

    if (institutionId == null || departmentId == null) {
      OperationReturnObject res = new OperationReturnObject();
      res.setCodeAndMessageAndReturnObject(200, "missing fields required", null);
      return res;
    }

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, " successfull ", null);
    return res;

  }

  public OperationReturnObject edit(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();

    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data, Params.INSTITUTION_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
      Params.START_TIME.getLabel(), Params.END_TIME.getLabel(), Params.MAX_PEOPLE.getLabel());
    Integer institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());
    Timestamp startTime = null;
    Timestamp endTime = null;
    Integer maxPeople = data.getInteger(Params.MAX_PEOPLE.getLabel());
    if (institutionId == null || departmentId == null || startTime == null || endTime == null || maxPeople == null) {
      OperationReturnObject res = new OperationReturnObject();
      res.setCodeAndMessageAndReturnObject(200, "missing fields required", null);
      return res;
    }

    if (StringUtils.isNotBlank(data.getString(Params.START_TIME.getLabel()))) {
      startTime = stringToTimestamp(data.getString(Params.START_TIME.getLabel()));
    }

    if (StringUtils.isNotBlank(data.getString(Params.END_TIME.getLabel()))) {
      endTime = stringToTimestamp(data.getString(Params.END_TIME.getLabel()));
    }

    Schedule newSchedule = new Schedule();
    newSchedule.setDepartmentId(departmentId);
    newSchedule.setInstitutionId(institutionId);
    newSchedule.setStartDate(startTime);
    newSchedule.setEndDate(endTime);

    scheduleRepository.save(newSchedule);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, " successfully with id: " + newSchedule.getId() + "added", newSchedule);
    return res;

  }

  public OperationReturnObject delete(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();

    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data, Params.INSTITUTION_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
      Params.START_TIME.getLabel(), Params.END_TIME.getLabel(), Params.MAX_PEOPLE.getLabel());
    Integer institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());
    Timestamp startTime = null;
    Timestamp endTime = null;
    Integer maxPeople = data.getInteger(Params.MAX_PEOPLE.getLabel());
    if (institutionId == null || departmentId == null || startTime == null || endTime == null || maxPeople == null) {
      OperationReturnObject res = new OperationReturnObject();
      res.setCodeAndMessageAndReturnObject(200, "missing fields required", null);
      return res;
    }

    if (StringUtils.isNotBlank(data.getString(Params.START_TIME.getLabel()))) {
      startTime = stringToTimestamp(data.getString(Params.START_TIME.getLabel()));
    }

    if (StringUtils.isNotBlank(data.getString(Params.END_TIME.getLabel()))) {
      endTime = stringToTimestamp(data.getString(Params.END_TIME.getLabel()));
    }

    Schedule newSchedule = new Schedule();
    newSchedule.setDepartmentId(departmentId);
    newSchedule.setInstitutionId(institutionId);
    newSchedule.setStartDate(startTime);
    newSchedule.setEndDate(endTime);

    scheduleRepository.save(newSchedule);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, " successfully with id: " + newSchedule.getId() + "added", newSchedule);
    return res;

  }


}
