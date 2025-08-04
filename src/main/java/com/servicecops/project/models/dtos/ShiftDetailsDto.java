package com.servicecops.project.models.dtos;

import com.alibaba.fastjson2.JSONArray;

import java.sql.Timestamp;


public record ShiftDetailsDto(
        Long id,
        String name,
        String departmentName,
        String type,
        Timestamp startTime,
        Timestamp endTime,
        Timestamp createdAt,
        Integer createdBy,
        Integer maxPeople,
        JSONArray scheduleRecords
) {}
