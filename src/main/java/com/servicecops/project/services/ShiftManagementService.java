package com.servicecops.project.services;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.*;
import com.servicecops.project.models.jpahelpers.enums.OffRequestStatus;
import com.servicecops.project.models.jpahelpers.enums.ShiftSwapStatus;
import com.servicecops.project.models.jpahelpers.enums.ShitStatus;
import com.servicecops.project.repositories.ShiftAssignmentRepository;
import com.servicecops.project.repositories.ShiftRepository;
import com.servicecops.project.repositories.ShiftSwapRepository;
import com.servicecops.project.repositories.TimeOffRepository;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ShiftManagementService extends BaseWebActionsService {
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final ShiftSwapRepository shiftSwapRepository;
    private final ShiftRepository shiftRepository;
    private final TimeOffRepository timeOffRepository;

    public ShiftManagementService(ShiftAssignmentRepository shiftAssignmentRepository, ShiftSwapRepository shiftSwapRepository, ShiftRepository shiftRepository, TimeOffRepository timeOffRepository) {
        super();
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.shiftSwapRepository = shiftSwapRepository;
        this.shiftRepository = shiftRepository;
        this.timeOffRepository = timeOffRepository;
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

    private OperationReturnObject assignToShift(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");

        requires(data, "shift_id", "user_id");

        Integer shiftId = data.getInteger("shift_id");
        Long userId = data.getLong("user_id");

        SystemUserModel user = getUserById(userId);
        Shift shift = getShift(shiftId);

        ShiftAssignment assignment = new ShiftAssignment();
        assignment.setShiftId(shift.getId());
        assignment.setEmployeeId(user.getId().intValue());
        assignment.setUpdatedAt(getCurrentTimestamp());
        assignment.setStatus(ShitStatus.PENDING.name());
        assignment.setAssignedBy(authenticatedUser.getId().intValue());

        shiftAssignmentRepository.save(assignment);

        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnMessage(0, "Shift assigned successfully");
        res.setReturnObject(assignment);
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
        requiresAuth();
        JSONObject search = request.getJSONObject("search");
        if (search == null) {
            search = new JSONObject();
        }
        Integer employeeId = search.getInteger("from_employee");
        Integer id = search.getInteger("id");

        if (id != null) {
            Map<String, Object> swapRequest = shiftSwapRepository.getEmployeeSwapRequestById(id);

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

    private OperationReturnObject timeOffRequest(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");
        requires(data, "employee_id", "start_date", "end_date");
        Integer employeeId = data.getInteger("employee_id");
        String startDate = data.getString("start_date");
        String endDate = data.getString("end_date");
        String reason = data.getString("reason");

        //todo: Get Employee
        TimeOffRequest offRequest = new TimeOffRequest();
        offRequest.setEmployeeId(employeeId);
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
        requiresAuth();
        JSONObject search = request.getJSONObject("search");
        if (search == null) {
            search = new JSONObject();
        }
        Integer employee_id = search.getInteger("employee_id");

        OperationReturnObject res = new OperationReturnObject();
        List<Map<String, Object>> timeOffRequests;
        if (employee_id != null) {
            timeOffRequests = timeOffRepository.getEmployeeTimeOffRequests(employee_id);
            res.setReturnCodeAndReturnMessage(200, "Time off requests fetched successfully");
            res.setReturnObject(timeOffRequests);
            return res;
        }

        timeOffRequests = timeOffRepository.getTimeOffRequests();
        res.setReturnCodeAndReturnMessage(200, "Time off requests fetched successfully");
        res.setReturnObject(timeOffRequests);
        return res;
    }

    private OperationReturnObject myTimeOffRequests(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();

        //todo: Get Employee profile for logged in user
        Integer employeeId = 1;// Placeholder for employee ID, replace it with actual logic to get employee ID from the authenticated user
        List<Map<String, Object>> mySchedules = timeOffRepository.getEmployeeTimeOffRequests(employeeId);
        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnMessage(200, "My time off requests fetched successfully");
        res.setReturnObject(mySchedules);
        return res;
    }

    @Transactional
    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch (action) {
            case "createShift" -> createShift(request);
            case "assignToShift" -> assignToShift(request);
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
