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

  public static ScheduleDto fromScheduleAndDepartment(Schedule schedule, Department department) {
    return new ScheduleDto(
      schedule.getId(),
      schedule.getStartDate(),
      schedule.getEndDate(),
      schedule.getDepartmentId(),
      department.getName(),
      null // No summary in this case
    );
  }

  // This constructor is designed to include the summary
  public static ScheduleDto fromScheduleAndDepartmentAndSummary(Schedule schedule, Department department, JSONObject summary) {
    return new ScheduleDto(
      schedule.getId(),
      schedule.getStartDate(),
      schedule.getEndDate(),
      schedule.getDepartmentId(),
      department.getName(),
      summary // Pass the summary here
    );
  }

  private static ScheduleDto fromSchedule(Schedule schedule) {
    return new ScheduleDto(
      schedule.getId(),
      schedule.getStartDate(),
      schedule.getEndDate(),
      schedule.getDepartmentId(),
      null,
      null
    );
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
