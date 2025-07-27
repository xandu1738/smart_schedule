package com.servicecops.project.services.schedule;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.*;
import com.servicecops.project.models.jpahelpers.enums.AppDomains;
import com.servicecops.project.repositories.*;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.services.schedule.Dtos.ScheduleDto;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.AuthorizationRequiredException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


import static java.time.LocalTime.now;


@RequiredArgsConstructor
@Service
public class ScheduleService extends BaseWebActionsService {

  private final ScheduleRepository scheduleRepository;
  private final ScheduleRecordRepository scheduleRecordRepository;
  private final ShiftRepository shiftRepository;
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
      case "getSingleSchedule" -> this.getSingleSchedule(request);
      case "edit" -> this.editSchedule(request);
      case "delete" -> this.delete(request);
//      case "findDistinctEmployeeIdsForSchedule" -> this.findDistinctEmployeeIdsForSchedule(request);


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

    if (getUserDomain() == AppDomains.BACK_OFFICE) {
      requires(data, Params.INSTITUTION_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
        Params.START_TIME.getLabel(), Params.END_TIME.getLabel());
      institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    }

    if (getUserDomain() == AppDomains.INSTITUTION) {
      requires(data, Params.DEPARTMENT_ID.getLabel(),
        Params.START_TIME.getLabel(), Params.END_TIME.getLabel());
      institutionId = authenticatedUser().getInstitutionId();
    }
    if (institutionId == null) {
      throw new IllegalArgumentException("Institution not found with id: " + institutionId);
    }
    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());
    Integer finalInstitutionId = institutionId;


    institutionRepository.findById(institutionId.longValue())
      .orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + finalInstitutionId));

    Department department = departmentRepository.findById(departmentId.longValue())
      .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + departmentId));

    List<Shift> shiftArray = shiftRepository.findAllByDepartmentId(departmentId);

    if (shiftArray.size() <= 1) {
      throw new IllegalArgumentException("No shift records found for department ID: " + departmentId);
    }

    LocalDate startDateRequest = parseLocalDate(data.getString(Params.START_TIME.getLabel()));
    LocalDate endDateRequest = parseLocalDate(data.getString(Params.END_TIME.getLabel()));

    Schedule newSchedule = new Schedule();
    newSchedule.setDepartmentId(departmentId);
    newSchedule.setInstitutionId(institutionId);
    newSchedule.setStartDate(Timestamp.from(startDateRequest.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    newSchedule.setEndDate(Timestamp.from(endDateRequest.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()));
    scheduleRepository.save(newSchedule);

    Integer createdScheduleId = newSchedule.getId();

    List<Employee> employees = employeeRepository.findAllByDepartmentAndArchivedFalse(department.getId().intValue());

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

//  public OperationReturnObject findDistinctEmployeeIdsForSchedule(JSONObject request) {
//
//    Integer scheduleId = request.getInteger(Params.SCHEDULE_ID.getLabel());
//
//    List<Long> employees = scheduleRecordRepository.findDistinctEmployeeIdsForSchedule(scheduleId);
//
//
//    OperationReturnObject res = new OperationReturnObject();
//    res.setCodeAndMessageAndReturnObject(200, "Schedule successfully created with ID: ", employees);
//    return res;
//
//  }

//  public OperationReturnObject getMySchedules(JSONObject request) throws AuthorizationRequiredException {
//    requiresAuth();
//    // Ensure DATA parameter exists and is a JSONObject
//    requires(request, Params.DATA.getLabel());
//    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
//
//    Integer institutionId;
//
//    if (getUserDomain() == AppDomains.BACK_OFFICE) {
//      // For BACK_OFFICE, both DEPARTMENT_ID and INSTITUTION_ID are required in data
//      requires(data, Params.DEPARTMENT_ID.getLabel(), Params.INSTITUTION_ID.getLabel());
//      institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
//    } else if (getUserDomain() == AppDomains.INSTITUTION) {
//      // For INSTITUTION, only DEPARTMENT_ID is required in data, institutionId is from authenticated user
//      requires(data, Params.DEPARTMENT_ID.getLabel());
//      institutionId = authenticatedUser().getInstitutionId();
//    } else {
//      // If the user domain is neither, institutionId cannot be determined.
//      // Throwing an IllegalArgumentException is appropriate here.
//      throw new IllegalArgumentException("Institution ID cannot be determined for the current user domain: " + getUserDomain());
//    }
//
//    // Fetch schedules for the determined institutionId
//    List<Schedule> institutionSchedules = scheduleRepository.findAllByInstitutionId(institutionId)
//      .orElseGet(ArrayList::new);
//
//    if (institutionSchedules.isEmpty()) {
//      OperationReturnObject res = new OperationReturnObject();
//      res.setCodeAndMessageAndReturnObject(200, "No schedules found for institution ID: " + institutionId, new ArrayList<>());
//      return res;
//    }
//
//    // Process each schedule to create ScheduleDto objects
//    List<ScheduleDto> scheduleDtos = institutionSchedules.stream()
//      .map(schedule -> {
//        JSONObject scheduleSummary = new JSONObject(); // Initialize scheduleSummary for each schedule
//
//        // Fetch department; throw if not found (data inconsistency)
//        Department department = departmentRepository.findById(schedule.getDepartmentId().longValue())
//          .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + schedule.getDepartmentId() + " for schedule ID: " + schedule.getId()));
//
//        // --- Shift Related Information ---
//        // Assuming findDistinctShifts can return a List<Long> that might contain nulls.
//        List<Long> shiftsInSchedule = scheduleRecordRepository.findDistinctShifts(schedule.getId());
//        List<JSONObject> shiftSummaries = new ArrayList<>();
//
//        if (shiftsInSchedule != null && !shiftsInSchedule.isEmpty()) { // Ensure the list itself isn't null
//          for (Long shiftId : shiftsInSchedule) {
//            // **FIX: Explicitly check for null shiftId before processing**
//            if (Objects.nonNull(shiftId)) { // Use Objects.nonNull for null-safe check
//              Optional<Shift> shiftOptional = shiftRepository.findById(shiftId.intValue());
//              if (shiftOptional.isPresent()) {
//                JSONObject currentShiftSummary = new JSONObject();
//                Shift shift = shiftOptional.get();
//                List<Long> employeesInShift = scheduleRecordRepository.findEmployeesInDistinctShiftsForSingleSchedule(schedule.getId(), shiftId.intValue());
//
//                currentShiftSummary.put("shift", shift);
//                currentShiftSummary.put("employees", employeesInShift);
//                shiftSummaries.add(currentShiftSummary);
//              } else {
//                // Log or handle the case where a shiftId exists but the actual Shift object is not found.
//                // This indicates a data inconsistency.
//                System.err.println("Warning: Shift with ID " + shiftId + " found in schedule records but not in shift repository for schedule " + schedule.getId());
//              }
//            } else {
//              // Log or handle the case where a null shiftId is found in schedule records.
//              System.err.println("Warning: Null shift ID found in schedule records for schedule " + schedule.getId());
//            }
//          }
//        }
//        scheduleSummary.put("shifts", shiftSummaries);
//
//        // --- Employee Related Information ---
//        List<Long> employeesInSchedule = scheduleRecordRepository.findDistinctEmployeeIdsForSingleSchedule(schedule.getId());
//        scheduleSummary.put("employeesInSchedule", employeesInSchedule.size());
//
//        return ScheduleDto.fromScheduleAndDepartmentAndSummary(schedule, department, scheduleSummary);
//      })
//      .collect(Collectors.toList());
//
//    OperationReturnObject res = new OperationReturnObject();
//    res.setCodeAndMessageAndReturnObject(200, "Schedules returned successfully", scheduleDtos);
//    return res;
//  }




  public OperationReturnObject getMySchedules(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());

    Integer institutionId;

    if (getUserDomain() == AppDomains.BACK_OFFICE) {
      requires(data, Params.DEPARTMENT_ID.getLabel(), Params.INSTITUTION_ID.getLabel());
      institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    } else if (getUserDomain() == AppDomains.INSTITUTION) {
      requires(data, Params.DEPARTMENT_ID.getLabel());
      institutionId = authenticatedUser().getInstitutionId();
    } else {
      throw new IllegalArgumentException("Institution ID cannot be determined for the current user domain: " + getUserDomain());
    }

    List<Schedule> institutionSchedules = scheduleRepository.findAllByInstitutionId(institutionId)
      .orElseGet(ArrayList::new);

    if (institutionSchedules.isEmpty()) {
      OperationReturnObject res = new OperationReturnObject();
      res.setCodeAndMessageAndReturnObject(200, "No schedules found for institution ID: " + institutionId, new ArrayList<>());
      return res;
    }

    // Get current time in UTC once for efficiency
    LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);

    List<ScheduleDto> scheduleDtos = institutionSchedules.stream()
      .map(schedule -> {
        JSONObject scheduleSummary = new JSONObject();
        JSONArray shiftArray = new JSONArray();

        Department department = departmentRepository.findById(schedule.getDepartmentId().longValue())
          .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + schedule.getDepartmentId() + " for schedule ID: " + schedule.getId()));

        // --- Shift Related Information ---
        List<Long> shiftsInSchedule = scheduleRecordRepository.findDistinctShifts(schedule.getId());

        if (shiftsInSchedule != null && !shiftsInSchedule.isEmpty()) {
          for (Long shiftId : shiftsInSchedule) {
            if (Objects.nonNull(shiftId) && shiftId > 0) { // Check for null and validity
              int shiftIdAsInt = shiftId.intValue();

              Optional<Shift> shiftOptional = shiftRepository.findById(shiftIdAsInt);
              if (shiftOptional.isPresent()) {
                Shift shift = shiftOptional.get();

                // Calculate is_active for the individual shift
                boolean isShiftActive = false;
                if (shift.getStartTime() != null && shift.getEndTime() != null) {
                  LocalDateTime shiftStartUtc = shift.getStartTime().toLocalDateTime();
                  LocalDateTime shiftEndUtc = shift.getEndTime().toLocalDateTime();
                  isShiftActive = !nowUtc.isBefore(shiftStartUtc) && !nowUtc.isAfter(shiftEndUtc);
                }

                List<Long> employeesInShift = scheduleRecordRepository
                  .findEmployeesInDistinctShiftsForSingleSchedule(schedule.getId(), shiftIdAsInt);

                // Ensure employeesInShift is not null to avoid NullPointerException if repository returns null
                if (employeesInShift == null) {
                  employeesInShift = new ArrayList<>();
                }

                JSONObject shiftSummary = new JSONObject();
                shiftSummary.put("shift", shift);
                shiftSummary.put("employees", employeesInShift);
                shiftSummary.put("is_active", isShiftActive); // Add is_active for the shift

                shiftArray.add(shiftSummary);
              } else {
                System.err.println("Warning: Shift with ID " + shiftId + " found in schedule records but not in shift repository for schedule " + schedule.getId());
              }
            } else {
              System.err.println("Warning: Null or invalid shift ID (" + shiftId + ") found in schedule records for schedule " + schedule.getId() + ". Skipping.");
            }
          }
        }
        scheduleSummary.put("shifts", shiftArray);

        // --- Employee Related Information ---
        List<Long> employeesInSchedule = scheduleRecordRepository.findDistinctEmployeeIdsForSingleSchedule(schedule.getId());
        scheduleSummary.put("employeesInSchedule", employeesInSchedule.size());


        // Pass schedule and department, the ScheduleDto will call schedule.getIs_active()
        return ScheduleDto.fromScheduleAndDepartmentAndSummary(schedule, department, scheduleSummary);
      })
      .collect(Collectors.toList());

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, "Schedules returned successfully", scheduleDtos);
    return res;
  }
//  public OperationReturnObject getMySchedules(JSONObject request) throws AuthorizationRequiredException {
//    requiresAuth();
//    requires(request, Params.DATA.getLabel());
//    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
//
//    Integer institutionId = null;
//
//    if (getUserDomain() == AppDomains.BACK_OFFICE) {
//      requires(data, Params.DEPARTMENT_ID.getLabel(), Params.INSTITUTION_ID.getLabel());
//      institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
//    }
//
//    if (getUserDomain() == AppDomains.INSTITUTION) {
//      requires(data, Params.DEPARTMENT_ID.getLabel());
//      institutionId = authenticatedUser().getInstitutionId();
//    }
//
//    if (institutionId == null) {
//      throw new IllegalArgumentException("Institution ID is null. Cannot fetch schedules.");
//    }
//
//    Optional<List<Schedule>> institutionSchedulesOptional = scheduleRepository.findAllByInstitutionId(institutionId);
//    List<Schedule> institutionSchedules = institutionSchedulesOptional.orElse(new ArrayList<>());
//
//    if (institutionSchedules.isEmpty()) {
//
//      throw new AuthorizationRequiredException("No schedules found for institution ID: " + institutionId);
//    }
//
//    List<ScheduleDto> scheduleDtos = institutionSchedules.stream()
//      .map(schedule -> {
//        Optional<Department> scheduleDepartment = departmentRepository.findById(schedule.getDepartmentId().longValue());
//
//        Department department = scheduleDepartment.orElseThrow(() ->
//          new IllegalArgumentException("Department not found with ID: " + schedule.getDepartmentId())
//        );
//
//        List<String> employeeShiftSummaryStrings = scheduleRecordRepository.findEmployeeShiftSummariesByScheduleId(schedule.getId());
//        List<Long> employeeIdsAssignedToShift = scheduleRecordRepository.findDistinctEmployeeIdsForSingleSchedule(schedule.getId());
//
//        // shift related information
//
//
//        JSONObject summaryForAllEmployees = new JSONObject();
//
//        if (employeeIdsAssignedToShift.size() < 1) {
//
//          summaryForAllEmployees.put("employeesInSchedule", 0);
//
//        } else {
//          summaryForAllEmployees.put("employeesInSchedule", employeeIdsAssignedToShift.size());
//        }
//
//        for (String summaryString : employeeShiftSummaryStrings) {
//          JSONObject employeeSummary = JSON.parseObject(summaryString);
//          if (employeeSummary.containsKey("employeeId")) {
//            Employee employee = employeeRepository.findById(employeeSummary.getLong("employeeId")).orElseThrow(() -> new IllegalArgumentException("Employee ID not found: " + employeeSummary.getLong("employeeId")));
//
//            summaryForAllEmployees.put(employee.getName(), employeeSummary);
//          }
//        }
//
//        return ScheduleDto.fromScheduleAndDepartmentAndSummary(schedule, department, summaryForAllEmployees);
//      })
//      .collect(Collectors.toList());
//
//
//    OperationReturnObject res = new OperationReturnObject();
//    res.setCodeAndMessageAndReturnObject(200, "Schedules returned successfully", scheduleDtos);
//    return res;
//  }
  public OperationReturnObject getSingleSchedule(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data, Params.SCHEDULE_ID.getLabel());
    Integer scheduleId = data.getInteger(Params.SCHEDULE_ID.getLabel());
    Department fetchedDepartment = null;


    Schedule fetchedSchedule = scheduleRepository.findById(scheduleId).orElse(null);
    if (fetchedSchedule == null) {
      throw new IllegalArgumentException("Schedule not found with ID: " + scheduleId);
    }


    Optional<Department> departmentOptional = departmentRepository.findById(fetchedSchedule.getDepartmentId().longValue());

    if (!departmentOptional.isEmpty()) {
      fetchedDepartment = departmentOptional.get();
    }

    ScheduleDto response = new ScheduleDto().fromScheduleAndDepartment(fetchedSchedule, fetchedDepartment);
    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, "schedule return successfully: ", response);
    return res;

  }


  public OperationReturnObject findSchedulesByInstitutionId(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());

    Integer institutionId = null;

    if (getUserDomain() == AppDomains.BACK_OFFICE) {
      requires(data, Params.DEPARTMENT_ID.getLabel(),
        Params.INSTITUTION_ID.getLabel());
      institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    }

    if (getUserDomain() == AppDomains.INSTITUTION) {
      requires(data, Params.DEPARTMENT_ID.getLabel());
      institutionId = authenticatedUser().getInstitutionId();
    }

    if (institutionId == null) {
      throw new IllegalArgumentException("Institution not found with id: " + institutionId);
    }
    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());

    Optional<List<Schedule>> institutionSchedules = scheduleRepository.findAllByInstitutionIdAndDepartmentId(institutionId, departmentId);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, "returned successful ", institutionSchedules);
    return res;

  }


  @Transactional
  public OperationReturnObject editSchedule(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();

    requires(request, Params.DATA.getLabel());
    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data, Params.SCHEDULE_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
      Params.START_TIME.getLabel(), Params.END_TIME.getLabel());

    Integer scheduleId = data.getInteger(Params.SCHEDULE_ID.getLabel());
    Integer institutionId = null;

    if (getUserDomain() == AppDomains.BACK_OFFICE) {
      requires(data, Params.INSTITUTION_ID.getLabel());
      institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
    }

    if (getUserDomain() == AppDomains.INSTITUTION) {
      institutionId = authenticatedUser().getInstitutionId();
    }

    Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());
    Timestamp startTime = null;
    Timestamp endTime = null;


    if (StringUtils.isNotBlank(data.getString(Params.START_TIME.getLabel()))) {
      startTime = stringToTimestamp(data.getString(Params.START_TIME.getLabel()));
    }

    if (StringUtils.isNotBlank(data.getString(Params.END_TIME.getLabel()))) {
      endTime = stringToTimestamp(data.getString(Params.END_TIME.getLabel()));
    }

    Schedule scheduleToUpdate = scheduleRepository.findById(scheduleId).orElse(null);


    if (scheduleToUpdate == null) {
      throw new IllegalArgumentException("Schedule not found with id: " + scheduleId);
    }

    scheduleToUpdate.setStartDate(startTime);
    scheduleToUpdate.setEndDate(endTime);


    Schedule updatedSchedule = scheduleRepository.save(scheduleToUpdate);

//  delete old schedule records
    scheduleRecordRepository.deleteById(updatedSchedule.getId());
    scheduleRecordRepository.flush();

//    create new scheduleRecord
    Integer updatedScheduleId = updatedSchedule.getId();

    List<Employee> employees = employeeRepository.findAllByDepartmentAndArchived(departmentId, false);

    if (employees.isEmpty()) {
      OperationReturnObject res = new OperationReturnObject();
      res.setReturnCodeAndReturnMessage(200, "Schedule created, but no active employees found for this department to assign shifts.");
      return res;
    }

    List<ScheduleRecord> createdScheduleRecords = new ArrayList<>();

    LocalDate startDateRequest = Instant.ofEpochMilli(startTime.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate endDateRequest = Instant.ofEpochMilli(endTime.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();


    for (LocalDate currentDate = startDateRequest; !currentDate.isAfter(endDateRequest); currentDate = currentDate.plusDays(1)) {
      for (Employee employee : employees) {
        Instant dailyStartTime = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dailyEndTime = currentDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

        ScheduleRecord scheduleRecord = new ScheduleRecord();
        scheduleRecord.setScheduleId(updatedScheduleId);
        scheduleRecord.setEmployeeId(employee.getId());
        scheduleRecord.setActive(true);
        scheduleRecord.setStartTime(Timestamp.from(dailyStartTime));
        scheduleRecord.setEndTime(Timestamp.from(dailyEndTime));
        scheduleRecord.setDateCreated(Timestamp.from(Instant.now()));
        scheduleRecord.setDateUpdated(Timestamp.from(Instant.now()));

        createdScheduleRecords.add(scheduleRecord);
      }
    }

    List<ScheduleRecord> savedScheduleRecords = scheduleRecordRepository.saveAll(createdScheduleRecords);

    OperationReturnObject res = new OperationReturnObject();
    res.setCodeAndMessageAndReturnObject(200, " successfully created new schedule records for: " + employees.size() + " employees", savedScheduleRecords);
    return res;

  }

  public OperationReturnObject delete(JSONObject request) throws AuthorizationRequiredException {
    requiresAuth();
    requires(request, Params.DATA.getLabel());

    JSONObject data = request.getJSONObject(Params.DATA.getLabel());
    requires(data, Params.SCHEDULE_ID.getLabel());
    Integer scheduleId = data.getInteger(Params.SCHEDULE_ID.getLabel());

    scheduleRepository.deleteById(scheduleId);

    OperationReturnObject res = new OperationReturnObject();
    res.setReturnCodeAndReturnMessage(200, "schedule successfully removed");
    return res;

  }


}
