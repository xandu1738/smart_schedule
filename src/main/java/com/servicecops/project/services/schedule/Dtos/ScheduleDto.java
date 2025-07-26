package com.servicecops.project.services.schedule.Dtos;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.servicecops.project.models.database.Department;
import com.servicecops.project.models.database.Schedule;
import lombok.*;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleDto {

  private Integer scheduleId;
  private java.sql.Timestamp StartTime;
  private java.sql.Timestamp EndTime;
  private Integer departmentId;
  private String departmentName;

  public static ScheduleDto fromScheduleAndDepartment(Schedule schedule, Department department) {
    return new ScheduleDto(schedule.getId(), schedule.getStartDate(),schedule.getEndDate(), schedule.getDepartmentId(), department.getName());
  }

  private static ScheduleDto fromSchedule(Schedule schedule) {
    return new ScheduleDto(schedule.getId(), schedule.getStartDate(),schedule.getEndDate(), schedule.getDepartmentId(),null);
  }

  public static List<ScheduleDto> fromSchedules(List<Schedule> schedules) {
    List<ScheduleDto> scheduleDtos = new ArrayList<>();
    for  (Schedule schedule : schedules) {
      ScheduleDto dto = fromSchedule(schedule);
      scheduleDtos.add(dto);
    }
    return scheduleDtos;
  }

}
