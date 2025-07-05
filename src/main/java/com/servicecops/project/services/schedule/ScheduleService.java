package com.servicecops.project.services.schedule;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.Schedule;
import com.servicecops.project.repositories.ScheduleRepository;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturn;
import com.servicecops.project.utils.OperationReturnObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;


@RequiredArgsConstructor
@Service
public class ScheduleService extends BaseWebActionsService {

    private final ScheduleRepository scheduleRepository;


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
            case "edit" -> this.edit(request);
            case "delete" -> this.delete(request);
            case "findByInstitution" -> this.findByInstitutionId(request);
            case "findByDepartmentIdAndInstitutionId" -> this.findByDepartmentIdAndInstitutionId(request);

            default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
        };
    }


    //    .orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + request.getLong("id")));
    public OperationReturnObject createSchedule(JSONObject request) {
        requiresAuth();

        requires(request, Params.DATA.getLabel());
        JSONObject data = request.getJSONObject(Params.DATA.getLabel());
        requires(data, Params.INSTITUTION_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
                Params.START_TIME.getLabel(), Params.END_TIME.getLabel(), Params.MAX_PEOPLE.getLabel());
        Integer institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
        Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());
        Timestamp startTime = Timestamp.valueOf(data.getString(Params.START_TIME.getLabel()));
        Timestamp endTime = Timestamp.valueOf(data.getString(Params.END_TIME.getLabel()));
        Integer maxPeople = data.getInteger(Params.MAX_PEOPLE.getLabel());
        if (institutionId == null || departmentId == null || startTime == null || endTime == null || maxPeople == null) {
            OperationReturnObject res = new OperationReturnObject();
            res.setCodeAndMessageAndReturnObject(200, "missing fields required", null);
            return res;
        }

        Schedule newSchedule = new Schedule();
        newSchedule.setDepartmentId(departmentId);
        newSchedule.setInstitutionId(institutionId);
        newSchedule.setStartDate(startTime.toInstant());
        newSchedule.setEndDate(endTime.toInstant());

        scheduleRepository.save(newSchedule);

        OperationReturnObject res = new OperationReturnObject();
        res.setCodeAndMessageAndReturnObject(200, " successfully with id: " + newSchedule.getId() + "added", newSchedule);
        return res;

    }

    public OperationReturnObject findByInstitutionId(JSONObject request) {
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

    public OperationReturnObject findByDepartmentIdAndInstitutionId(JSONObject request) {
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

    public OperationReturnObject edit(JSONObject request) {
        requiresAuth();

        requires(request, Params.DATA.getLabel());
        JSONObject data = request.getJSONObject(Params.DATA.getLabel());
        requires(data, Params.INSTITUTION_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
                Params.START_TIME.getLabel(), Params.END_TIME.getLabel(), Params.MAX_PEOPLE.getLabel());
        Integer institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
        Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());
        Timestamp startTime = Timestamp.valueOf(data.getString(Params.START_TIME.getLabel()));
        Timestamp endTime = Timestamp.valueOf(data.getString(Params.END_TIME.getLabel()));
        Integer maxPeople = data.getInteger(Params.MAX_PEOPLE.getLabel());
        if (institutionId == null || departmentId == null || startTime == null || endTime == null || maxPeople == null) {
            OperationReturnObject res = new OperationReturnObject();
            res.setCodeAndMessageAndReturnObject(200, "missing fields required", null);
            return res;
        }

        Schedule newSchedule = new Schedule();
        newSchedule.setDepartmentId(departmentId);
        newSchedule.setInstitutionId(institutionId);
        newSchedule.setStartDate(startTime.toInstant());
        newSchedule.setEndDate(endTime.toInstant());

        scheduleRepository.save(newSchedule);

        OperationReturnObject res = new OperationReturnObject();
        res.setCodeAndMessageAndReturnObject(200, " successfully with id: " + newSchedule.getId() + "added", newSchedule);
        return res;

    }

    public OperationReturnObject delete(JSONObject request) {
        requiresAuth();

        requires(request, Params.DATA.getLabel());
        JSONObject data = request.getJSONObject(Params.DATA.getLabel());
        requires(data, Params.INSTITUTION_ID.getLabel(), Params.DEPARTMENT_ID.getLabel(),
                Params.START_TIME.getLabel(), Params.END_TIME.getLabel(), Params.MAX_PEOPLE.getLabel());
        Integer institutionId = data.getInteger(Params.INSTITUTION_ID.getLabel());
        Integer departmentId = data.getInteger(Params.DEPARTMENT_ID.getLabel());
        Timestamp startTime = Timestamp.valueOf(data.getString(Params.START_TIME.getLabel()));
        Timestamp endTime = Timestamp.valueOf(data.getString(Params.END_TIME.getLabel()));
        Integer maxPeople = data.getInteger(Params.MAX_PEOPLE.getLabel());
        if (institutionId == null || departmentId == null || startTime == null || endTime == null || maxPeople == null) {
            OperationReturnObject res = new OperationReturnObject();
            res.setCodeAndMessageAndReturnObject(200, "missing fields required", null);
            return res;
        }

        Schedule newSchedule = new Schedule();
        newSchedule.setDepartmentId(departmentId);
        newSchedule.setInstitutionId(institutionId);
        newSchedule.setStartDate(startTime.toInstant());
        newSchedule.setEndDate(endTime.toInstant());

        scheduleRepository.save(newSchedule);

        OperationReturnObject res = new OperationReturnObject();
        res.setCodeAndMessageAndReturnObject(200, " successfully with id: " + newSchedule.getId() + "added", newSchedule);
        return res;

    }


}
