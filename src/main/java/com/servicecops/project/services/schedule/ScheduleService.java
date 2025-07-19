package com.servicecops.project.services.schedule;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.Employee;
import com.servicecops.project.models.database.Schedule;
import com.servicecops.project.models.database.ScheduleRecord;
import com.servicecops.project.models.jpahelpers.enums.AppDomains;
import com.servicecops.project.repositories.*;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.AuthorizationRequiredException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;


import static java.time.LocalTime.now;


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
    SCHEDULE_ID("scheduleId"),
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
      case "getMySchedules" -> this.getMySchedules(request);
      case "edit" -> this.editSchedule(request);
      case "delete" -> this.delete(request);
//      case "findByDepartmentIdAndInstitutionId" -> this.findByDepartmentIdAndInstitutionId(request);

      default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
    };
  }



  private LocalDate parseLocalDate(String dateString) {
    try {
      return LocalDate.parse(dateString);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date format for startTime or endTime. Expected YYYY-MM-DD. Error: " + e.getMessage());
    }
  }

  public OperationReturnObject createSchedule(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    Integer institutionId = null;

    if(getUserDomain() == AppDomains.BACK_OFFICE){
      requires(data, Params.INSTITUTION_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
        Params.START_TIME.getLabel(), Params.END_TIME.getLabel());
      institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    }

    if(getUserDomain() == AppDomains.INSTITUTION){
      requires(data, Params.DEPARTMENT_ID.getLabel(),
        Params.START_TIME.getLabel(), Params.END_TIME.getLabel());
      institutionId = authenticatedUser().getInstitutionId();
    }




    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());
    Integer finalInstitutionId = institutionId;


    institutionRepository.findById(institutionId.longValue())
      .orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + finalInstitutionId));

    departmentRepository.findById(departmentId.longValue())
      .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + departmentId));

    LocalDate startDateRequest = parseLocalDate(data.getString(Params.START_TIME.getLabel()));
    LocalDate endDateRequest = parseLocalDate(data.getString(Params.END_TIME.getLabel()));

    Schedule newSchedule = new Schedule();
    newSchedule.setDepartmentId(departmentId);
    newSchedule.setInstitutionId(institutionId);
    newSchedule.setStartDate(Timestamp.from(startDateRequest.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    newSchedule.setEndDate(Timestamp.from(endDateRequest.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()));
    scheduleRepository.save(newSchedule);

    Integer createdScheduleId = newSchedule.getId();

    Optional<List<Employee>> employeesOptional = employeeRepository.findByDepartmentAndArchivedTrue(departmentId);
    List<Employee> employees = employeesOptional.orElse(new ArrayList<>());

    if (employees.isEmpty()) {
      OperationReturnObject res = new OperationReturnObject();
      res.setCodeAndMessageAndReturnObject(200, "Schedule created, but no active employees found for this department to assign shifts.", newSchedule);
      return res;
    }

    List<ScheduleRecord> createdScheduleRecords = new ArrayList<>();

    for (LocalDate currentDate = startDateRequest; !currentDate.isAfter(endDateRequest); currentDate = currentDate.plusDays(1)) {
      for (Employee employee : employees) {
        Instant dailyStartTime = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dailyEndTime = currentDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

        ScheduleRecord scheduleRecord = new ScheduleRecord();
        scheduleRecord.setScheduleId(createdScheduleId);
        scheduleRecord.setEmployeeId(employee.getId());
        scheduleRecord.setActive(true);
        scheduleRecord.setStartTime(Timestamp.from(dailyStartTime));
        scheduleRecord.setEndTime(Timestamp.from(dailyEndTime));
        scheduleRecord.setDateCreated(Timestamp.from(Instant.now()));
        scheduleRecord.setDateUpdated(Timestamp.from(Instant.now()));

        createdScheduleRecords.add(scheduleRecord);
      }
    }

    scheduleRecordRepository.saveAll(createdScheduleRecords);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, "Schedule successfully created with ID: " + createdScheduleId + " and " + createdScheduleRecords.size() + " employee shifts assigned.", newSchedule);
    return res;
  }

  public OperationReturnObject getMySchedules(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());

    Integer institutionId = null;

    if(getUserDomain() == AppDomains.BACK_OFFICE){
      requires(data, Params.DEPARTMENT_ID.getLabel(),
       Params.INSTITUTION_ID.getLabel());
      institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    }

    if(getUserDomain() == AppDomains.INSTITUTION){
      requires(data, Params.DEPARTMENT_ID.getLabel());
      institutionId = authenticatedUser().getInstitutionId();
    }

    if (institutionId == null) {
      throw new IllegalArgumentException("Institution not found with id: " + institutionId);
    }
    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());

    Optional<List<Schedule>> institutionSchedules = scheduleRepository.findAllByInstitutionIdAndDepartmentId(institutionId,departmentId);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, "returned successful ", institutionSchedules);
    return res;

  }

  public OperationReturnObject findSchedulesByInstitutionId(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());

    Integer institutionId = null;

    if(getUserDomain() == AppDomains.BACK_OFFICE){
      requires(data, Params.DEPARTMENT_ID.getLabel(),
        Params.INSTITUTION_ID.getLabel());
      institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    }

    if(getUserDomain() == AppDomains.INSTITUTION){
      requires(data, Params.DEPARTMENT_ID.getLabel());
      institutionId = authenticatedUser().getInstitutionId();
    }

    if (institutionId == null) {
      throw new IllegalArgumentException("Institution not found with id: " + institutionId);
    }
    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());

    Optional<List<Schedule>> institutionSchedules = scheduleRepository.findAllByInstitutionIdAndDepartmentId(institutionId,departmentId);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, "returned successful ", institutionSchedules);
    return res;

  }

  public OperationReturnObject editSchedule(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();

    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data,Params.SCHEDULE_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
      Params.START_TIME.getLabel(), Params.END_TIME.getLabel());

    Integer scheduleId = data.getInteger(Params.SCHEDULE_ID.getLabel());
    Integer institutionId = null;

    if(getUserDomain() == AppDomains.BACK_OFFICE){
      requires(data, Params.INSTITUTION_ID.getLabel());
      institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    }

    if(getUserDomain() == AppDomains.INSTITUTION){
      institutionId = authenticatedUser().getInstitutionId();
    }

    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());
    Timestamp startTime = null;
    Timestamp endTime = null;
    if (institutionId == null || departmentId == null || startTime == null || endTime == null) {
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
