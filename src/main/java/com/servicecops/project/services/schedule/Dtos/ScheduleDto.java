package com.servicecops.project.services.schedule.Dtos;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.servicecops.project.models.database.Department;
import com.servicecops.project.models.database.Schedule;
import lombok.*;
import java.sql.Timestamp;
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
  private Timestamp StartTime;
  private Timestamp EndTime;
  private Integer departmentId;
  private String departmentName;
  private JSONObject summary;
  private Boolean is_active; // Add the is_active field for the Schedule

  // This constructor is designed to include the summary AND the Schedule's is_active status
  public static ScheduleDto fromScheduleAndDepartmentWithSummary(Schedule schedule, Department department, JSONObject summary) {
    return new ScheduleDto(
      schedule.getId(),
      schedule.getStartDate(),
      schedule.getEndDate(),
      schedule.getDepartmentId(),
      department.getName(),
      summary,
      schedule.getIs_active() // Pass the calculated is_active from the Schedule entity
    );
  }

  // Existing constructors/methods, updated to accommodate the new is_active field if they are still used
  public static ScheduleDto fromScheduleAndDepartment(Schedule schedule, Department department) {
    return new ScheduleDto(
      schedule.getId(),
      schedule.getStartDate(),
      schedule.getEndDate(),
      schedule.getDepartmentId(),
      department.getName(),
      null, // No summary in this case
      schedule.getIs_active() // Pass the calculated is_active
    );
  }

  private static ScheduleDto fromSchedule(Schedule schedule) {
    return new ScheduleDto(
      schedule.getId(),
      schedule.getStartDate(),
      schedule.getEndDate(),
      schedule.getDepartmentId(),
      null,
      null,
      schedule.getIs_active() // Pass the calculated is_active
    );
  }

  public static List<ScheduleDto> fromSchedules(List<Schedule> schedules) {
    List<ScheduleDto> scheduleDtos = new ArrayList<>();
    for (Schedule schedule : schedules) {
      ScheduleDto dto = fromSchedule(schedule);
      scheduleDtos.add(dto);
    }
    return scheduleDtos;
  }
}
