package com.servicecops.project.services;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.*;
import com.servicecops.project.models.jpahelpers.enums.AppDomains;
import com.servicecops.project.models.jpahelpers.enums.OffRequestStatus;
import com.servicecops.project.models.jpahelpers.enums.ShiftSwapStatus;
import com.servicecops.project.repositories.*;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.AuthorizationRequiredException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ShiftManagementService extends BaseWebActionsService {
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final ShiftSwapRepository shiftSwapRepository;
    private final ShiftRepository shiftRepository;
    private final TimeOffRepository timeOffRepository;
    private final ScheduleRecordRepository scheduleRecordRepository;
    private final EmployeeRepository employeeRepository;

    public ShiftManagementService(ShiftAssignmentRepository shiftAssignmentRepository, ShiftSwapRepository shiftSwapRepository, ShiftRepository shiftRepository, TimeOffRepository timeOffRepository, ScheduleRecordRepository scheduleRecordRepository, EmployeeRepository employeeRepository) {
        super();
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.shiftSwapRepository = shiftSwapRepository;
        this.shiftRepository = shiftRepository;
        this.timeOffRepository = timeOffRepository;
        this.scheduleRecordRepository = scheduleRecordRepository;
        this.employeeRepository = employeeRepository;
    }

    private OperationReturnObject createShift(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();

        requires(request, "data");
        JSONObject data = request.getJSONObject("data");
        requires(data, "department_id", "shift_type", "name", "end_time", "start_time", "max_people");

        Integer departmentId = data.getInteger("department_id");
        String shiftType = data.getString("shift_type");
        String name = data.getString("name");
        String endTime = data.getString("end_time");
        String startTime = data.getString("start_time");
        Integer maxPeople = data.getInteger("max_people");

        Department department = getDepartment(departmentId.longValue());

        Shift shift = new Shift();
        shift.setName(Objects.requireNonNull(name));
        shift.setMaxPeople(maxPeople);
        shift.setType(Objects.requireNonNull(shiftType));
        shift.setDepartmentId(department.getId());

        if (StringUtils.isNotBlank(startTime)) {
            shift.setStartTime(stringToTimestamp(startTime));
        }
        if (StringUtils.isNotBlank(endTime)) {
            shift.setEndTime(stringToTimestamp(startTime));
        }

        shift.setCreatedAt(getCurrentTimestamp());
        shift.setCreatedBy(authenticatedUser.getId());

        shiftRepository.save(shift);

        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnMessage(0, "Shift created successfully");
        res.setReturnObject(shift);

        return res;
    }

    private OperationReturnObject assignEmployeesToShift(JSONObject request) throws AuthorizationRequiredException {
//        SystemUserModel authenticatedUser = authenticatedUser();
        requiresAuth();
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");

        requires(data, "schedule_id", "shifts");

        Integer scheduleId = data.getInteger("schedule_id");
        if (scheduleId == null) {
            throw new IllegalArgumentException("Schedule ID is required");
        }

        List<JSONObject> shifts = data.getJSONArray("shifts").toJavaList(JSONObject.class);

        if (shifts.isEmpty()) {
            throw new IllegalArgumentException("At least one shift must be specified");
        }

        shifts.forEach(shift -> {
            requires(shift, "shift_id", "employees");

            Integer shiftId = shift.getInteger("shift_id");
            List<Long> employees = shift.getJSONArray("employees").toJavaList(Long.class);

            if (shiftId == null) {
                throw new IllegalArgumentException("Shift ID is required");
            }

            Shift s = getShift(shiftId);

            if (employees.isEmpty()) {
                throw new IllegalArgumentException("At least one employee must be specified for shift " + s.getName());
            }

            employees.forEach(emp -> {
                try {
                    Employee employee = getEmployee(emp);
                    List<ScheduleRecord> scheduleRecords = getEmployeeScheduleRecords(employee.getId(), scheduleId);
                    if (scheduleRecords.isEmpty()) {
                        throw new IllegalArgumentException("No schedule records found for employee ID: " + emp);
                    }

                    scheduleRecords.forEach(sr -> {
                        sr.setShiftId(shiftId);
                        sr.setDateUpdated(getCurrentTimestamp());
                        scheduleRecordRepository.save(sr);
                    });
                } catch (AuthorizationRequiredException e) {
                    throw new RuntimeException(e.getMessage());
                }

            });

        });

        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnMessage(0, "Shift assigned successfully");
        return res;
    }

    private OperationReturnObject makeSwapRequest(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");
        requires(data, "from_employee", "to_employee", "shift_id");
        Long fromEmployee = data.getLong("from_employee");
        Long toEmployee = data.getLong("to_employee");
        Integer shiftId = data.getInteger("shift_id");

        SystemUserModel from = getUserById(fromEmployee);
        SystemUserModel to = getUserById(toEmployee);

        Shift shift = getShift(shiftId);

        ShiftSwapRequest swapRequest = new ShiftSwapRequest();
        swapRequest.setStatus(ShiftSwapStatus.PENDING.name());
        swapRequest.setFromEmployee(from.getId().intValue());
        swapRequest.setToEmployee(to.getId().intValue());
        swapRequest.setShiftId(shift.getId());
        swapRequest.setRequestedBy(authenticatedUser.getId());

        shiftSwapRepository.save(swapRequest);

        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnMessage(0, "Swap request created successfully");
        res.setReturnObject(swapRequest);
        return res;
    }

    private OperationReturnObject getSwapRequests(JSONObject request) {
        try {
            requiresAuth();
            JSONObject search = request.getJSONObject("search");
            if (search == null) {
                search = new JSONObject();
            }
            Integer employeeId = search.getInteger("from_employee");
            Integer id = search.getInteger("id");

            if (id != null) {
                var swapRequest = shiftSwapRepository.getEmployeeSwapRequestById(id);

                if (swapRequest.isEmpty()) {
                    throw new IllegalStateException("No Swap request matches selected ID");
                }

                OperationReturnObject res = new OperationReturnObject();
                res.setReturnCodeAndReturnMessage(200, "Swap request fetched successfully");
                res.setReturnObject(swapRequest);
                return res;
            }

            OperationReturnObject res = new OperationReturnObject();
            List<Map<String, Object>> swapRequests;
            if (employeeId != null) {
                swapRequests = shiftSwapRepository.getEmployeeSwapRequests(employeeId);
                res.setReturnCodeAndReturnMessage(200, "Swap requests fetched successfully");
                res.setReturnObject(swapRequests);
                return res;
            }

            swapRequests = shiftSwapRepository.getAllSwapRequests();
            res.setReturnCodeAndReturnMessage(200, "Swap requests fetched successfully");
            res.setReturnObject(swapRequests);
            return res;
        } catch (AuthorizationRequiredException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    public OperationReturnObject approveSwapRequest(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");
        requires(data, "action", "swap_id");
        String action = data.getString("action");

        if (!EnumUtils.isValidEnum(ShiftSwapStatus.class, action)) {
            throw new IllegalArgumentException("Invalid action provided");
        }

        OperationReturnObject res = new OperationReturnObject();
        Integer swapId = data.getInteger("swap_id");
        ShiftSwapRequest shiftSwapRequest = shiftSwapRepository.findById(swapId).orElseThrow(
                () -> new IllegalStateException("No Swap request matches selected ID")
        );

        if (ShiftSwapStatus.PENDING.name().equals(action)) {
            res.setReturnCodeAndReturnMessage(400, "Invalid action provided");
            return res;
        }

        if (!Objects.equals(shiftSwapRequest.getStatus(), ShiftSwapStatus.PENDING.name())) {
            throw new IllegalStateException("Swap request is not in pending state");
        }

        if (ShiftSwapStatus.REJECTED.name().equals(action)) {
            shiftSwapRequest.setStatus(ShiftSwapStatus.REJECTED.name());
            shiftSwapRequest.setApprovedBy(authenticatedUser.getId());
            shiftSwapRequest.setApprovedOn(getCurrentTimestamp());
            shiftSwapRepository.save(shiftSwapRequest);
            res.setReturnCodeAndReturnMessage(200, "Swap request rejected successfully");
            return res;
        }

        Shift shift = getShift(shiftSwapRequest.getShiftId());
        shiftAssignmentRepository.findFirstByShiftIdAndEmployeeId(shift.getId(), shiftSwapRequest.getFromEmployee())
                .ifPresent(assignment -> {
                    assignment.setEmployeeId(shiftSwapRequest.getToEmployee());
                    assignment.setUpdatedBy(authenticatedUser.getId().intValue());
                    assignment.setUpdatedAt(getCurrentTimestamp());
                    shiftAssignmentRepository.save(assignment);
                });

        shiftAssignmentRepository.findFirstByShiftIdAndEmployeeId(shift.getId(), shiftSwapRequest.getToEmployee())
                .ifPresent(assignment -> {
                    assignment.setEmployeeId(shiftSwapRequest.getFromEmployee());
                    assignment.setUpdatedBy(authenticatedUser.getId().intValue());
                    assignment.setUpdatedAt(getCurrentTimestamp());
                    shiftAssignmentRepository.save(assignment);
                });

        res.setReturnCodeAndReturnMessage(200, "Swap request approved successfully");
        return res;
    }

    private OperationReturnObject timeOffRequest(JSONObject request) throws AuthorizationRequiredException {
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");
        requires(data, "employee_id", "start_date", "end_date");
        Long employeeId = data.getLong("employee_id");
        String startDate = data.getString("start_date");
        String endDate = data.getString("end_date");
        String reason = data.getString("reason");

        Employee employee = getEmployee(employeeId);
        TimeOffRequest offRequest = new TimeOffRequest();
        offRequest.setEmployeeId(employee.getId());
        offRequest.setStartDate(stringToTimestamp(startDate));
        offRequest.setEndDate(stringToTimestamp(endDate));
        offRequest.setRequestedOn(getCurrentTimestamp());
        offRequest.setRequestedBy(authenticatedUser.getId().intValue());
        offRequest.setReason(reason);
        offRequest.setStatus(OffRequestStatus.PENDING.name());
        timeOffRepository.save(offRequest);

        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnMessage(200, "Time off request created successfully");
        return res;
    }

    private OperationReturnObject timeOffApproval(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");
        requires(data, "action", "request_id");

        String action = data.getString("action");
        if (!EnumUtils.isValidEnum(OffRequestStatus.class, action)) {
            throw new IllegalArgumentException("Invalid action provided");
        }
        Integer requestId = data.getInteger("request_id");
        TimeOffRequest timeOffRequest = timeOffRepository.findById(requestId).
                orElseThrow(() -> new IllegalStateException("No Time off request matches selected ID"));

        if (OffRequestStatus.PENDING.name().equals(action)) {
            throw new IllegalArgumentException("Invalid action provided");
        }

        if (!Objects.equals(timeOffRequest.getStatus(), OffRequestStatus.PENDING.name())) {
            throw new IllegalStateException("Time off request is not in pending state");
        }

        switch (action) {
            case "REJECTED" -> {
                timeOffRequest.setStatus(OffRequestStatus.REJECTED.name());
                timeOffRequest.setApprovedBy(authenticatedUser.getId().intValue());
                timeOffRequest.setApprovedOn(getCurrentTimestamp());
                timeOffRepository.save(timeOffRequest);
                OperationReturnObject res = new OperationReturnObject();
                res.setReturnCodeAndReturnMessage(200, "Time off request rejected successfully");
                return res;
            }

            case "APPROVED" -> {
                timeOffRequest.setStatus(OffRequestStatus.APPROVED.name());
                timeOffRequest.setApprovedBy(authenticatedUser.getId().intValue());
                timeOffRequest.setApprovedOn(getCurrentTimestamp());
                timeOffRepository.save(timeOffRequest);
                OperationReturnObject res = new OperationReturnObject();
                res.setReturnCodeAndReturnMessage(200, "Time off request approved successfully");
                return res;
            }
            default -> throw new IllegalArgumentException("Invalid action provided");
        }
    }

    private OperationReturnObject getTimeOffRequests(JSONObject request) {
        try {
            requiresAuth();
            JSONObject search = request.getJSONObject("search");
            if (search == null) {
                search = new JSONObject();
            }
            Integer employeeId = search.getInteger("employee_id");

            OperationReturnObject res = new OperationReturnObject();
            List<Map<String, Object>> timeOffRequests;
            if (employeeId != null) {
                timeOffRequests = timeOffRepository.getEmployeeTimeOffRequests(employeeId);
                res.setReturnCodeAndReturnMessage(200, "Time off requests fetched successfully");
                res.setReturnObject(timeOffRequests);
                return res;
            }

            timeOffRequests = timeOffRepository.getTimeOffRequests();
            res.setReturnCodeAndReturnMessage(200, "Time off requests fetched successfully");
            res.setReturnObject(timeOffRequests);
            return res;
        } catch (AuthorizationRequiredException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private OperationReturnObject myTimeOffRequests(JSONObject request) throws AuthorizationRequiredException {
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");
        requires(data, "employee_id");

        Long employeeId = data.getLong("employee_id");

        //todo: Get Employee profile for this user --expecting employee table to bear user ID
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee not specified.");
        }

        Employee employee = getEmployee(employeeId);

        List<Map<String, Object>> mySchedules = timeOffRepository.getEmployeeTimeOffRequests(employee.getId());
        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnMessage(200, "My time off requests fetched successfully");
        res.setReturnObject(mySchedules);
        return res;
    }

    private OperationReturnObject getShifts(JSONObject request) throws AuthorizationRequiredException {
        SystemUserModel authenticatedUser = authenticatedUser();
        JSONObject search = request.getJSONObject("search");
        if (search == null) {
            search = new JSONObject();
        }
        Integer departmentId = search.getInteger("department_id");

        if (authenticatedUser.getInstitutionId() != null) {
            Department department = getDepartment(departmentId.longValue());
            if (!Objects.equals(department.getInstitutionId(), authenticatedUser.getInstitutionId().longValue())) {
                throw new AuthorizationRequiredException("You are not authorized to view shifts for this department");
            }
        }

        if (departmentId == null) {
            throw new IllegalArgumentException("Department ID is required");
        }

        List<Shift> shifts = shiftRepository.findAllByDepartmentId(departmentId);
        OperationReturnObject returnObject = new OperationReturnObject();
        returnObject.setReturnCodeAndReturnMessage(200, "Shifts fetched successfully");
        returnObject.setReturnObject(shifts);
        return returnObject;
    }

    private OperationReturnObject getShiftDetails(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        JSONObject search = request.getJSONObject("search");
        if (search == null) {
            search = new JSONObject();
        }

        Long shiftId = search.getLong("shift_id");
        if (shiftId == null) {
            throw new IllegalArgumentException("Shift ID is required");
        }

        Optional<Map<String, Object>> shiftDetails = shiftRepository.getShiftDetails(shiftId);

        OperationReturnObject returnObject = new OperationReturnObject();
        returnObject.setReturnCodeAndReturnMessage(200, "Shift details fetched successfully");
        returnObject.setReturnObject(shiftDetails);
        return returnObject;
    }

    private OperationReturnObject deleteShift(JSONObject request) {
        belongsTo(AppDomains.INSTITUTION);
        requires(request, "search");

        JSONObject search = request.getJSONObject("search");
        if (search == null) {
            search = new JSONObject();
        }
        requires(search, "shift_id");
        Integer shiftId = search.getInteger("shift_id");

        Shift shift = getShift(shiftId);
        shiftRepository.delete(shift);

        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnMessage(200, "Shift deleted successfully");
        return res;
    }

    private OperationReturnObject shiftSimulation(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        JSONObject data = request.getJSONObject("data");
        if (data == null) {
            throw new IllegalArgumentException("Data is required for shift simulation");
        }

        Integer departmentId = data.getInteger("department_id");
        Department department = getDepartment(departmentId.longValue());

//        Integer shiftId = data.getInteger("shift_id");
//        Shift shift = getShift(shiftId);
        List<Shift> shifts = shiftRepository.findAllByDepartmentId(departmentId);

        if (shifts.isEmpty()) {
            throw new IllegalArgumentException("No shifts found for the specified department");
        }

        if (!Objects.equals(department.getInstitutionId(), authenticatedUser.getInstitutionId().longValue())) {
            throw new IllegalArgumentException("You are not authorized to view shifts for this department");
        }

        List<Employee> employees = employeeRepository.findAllByDepartmentAndArchived(department.getId(), false);
        //split employees into n groups where n=number of shifts
//        List<JSONObject> shiftAssignments = shifts.stream().map(shift -> {
//            JSONObject o = JSON.parseObject(JSON.toJSONString(shift));
//            Map<Boolean, List<Employee>> collected = employees.stream()
//                    .filter(employee -> employee.getStatus().equals(EmployeeStatus.AVAILABLE.name()))
////                    .filter(employee -> employee.getId() % shifts.size() == shift.getId() % shifts.size())
//                    .collect(Collectors.partitioningBy(employee -> employee.getId() % shifts.size() == shift.getId() % shifts.size()));
//
//            return o;
//        }).toList();
        Map<String, ArrayList<Object>> sims = assignEmployeesToShifts(employees, shifts);
        OperationReturnObject op = new OperationReturnObject();
        op.setCodeAndMessageAndReturnObject(200, "Shift simulation completed successfully", sims);

        return op;
    }

    public Map<String, ArrayList<Object>> assignEmployeesToShifts(List<Employee> employees, List<Shift> shifts) {

        var shiftsMap = shifts.stream()
                .collect(Collectors.toMap(Shift::getName, b -> new ArrayList<>(), (a, b) -> b, LinkedHashMap::new));

        Iterator<Shift> shiftCycle = Stream.generate(() -> shifts)
                .flatMap(List::stream)
                .iterator();

        for (Employee employee : employees) {
            boolean assigned = false;

            // Try assigning the employee to the next available shift
            Set<Shift> tried = new HashSet<>();
            while (!assigned && tried.size() < shifts.size()) {
                var candidate = shiftCycle.next();
                tried.add(candidate);

                var applesInBasket = shiftsMap.get(candidate.getName());
                if (applesInBasket.size() < candidate.getMaxPeople()) {
                    applesInBasket.add(employee);
                    assigned = true;
                }
            }

            if (!assigned) {
                log.error("Could not assign {}: all baskets full", employee.getName());
            }
        }

        return shiftsMap;
    }

    @Transactional
    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) throws AuthorizationRequiredException {
        return switch (action) {
            case "createShift" -> createShift(request);
            case "deleteShift" -> deleteShift(request);
            case "simAssignment" -> shiftSimulation(request);
            case "shiftDetails" -> getShiftDetails(request);
            case "shifts" -> getShifts(request);
            case "assignToShift" -> assignEmployeesToShift(request);
            case "swapRequest" -> makeSwapRequest(request);
            case "swapRequests" -> getSwapRequests(request);
            case "approveSwap" -> approveSwapRequest(request);
            case "offRequest" -> timeOffRequest(request);
            case "offRequests" -> getTimeOffRequests(request);
            case "myOffRequests" -> myTimeOffRequests(request);
            case "offApproval" -> timeOffApproval(request);
            default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
        };
    }
}
