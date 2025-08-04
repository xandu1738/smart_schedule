package com.servicecops.project.models.dtos.mapper;

import com.alibaba.fastjson2.JSON;
import com.servicecops.project.models.dtos.ShiftDetailsDto;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.function.Function;

@Service
public class ShiftDetailsDtoMapper implements Function<Map<String, Object>, ShiftDetailsDto> {
    @Override
    public ShiftDetailsDto apply(Map<String, Object> row) {

        var scheduleRecords = JSON.parseArray(row.get("schedule_records").toString());
        return new ShiftDetailsDto(
                ((Number) row.get("id")).longValue(),
                (String) row.get("name"),
                (String) row.get("department_name"),
                (String) row.get("type"),
                ((Timestamp) row.get("start_time")),
                ((Timestamp) row.get("end_time")),
                ((Timestamp) row.get("created_at")),
                ((Number) row.get("created_by")).intValue(),
                ((Number) row.get("max_people")).intValue(),
                scheduleRecords
        );
    }
}
