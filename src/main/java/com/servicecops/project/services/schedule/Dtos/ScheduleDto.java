package com.servicecops.project.services.schedule.Dtos;


import com.servicecops.project.models.database.Department;
import com.servicecops.project.models.database.Schedule;
import lombok.*;

import java.security.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {

  private Integer scheduleId;
  private java.sql.Timestamp StartTime;
  private java.sql.Timestamp EndTime;
  private Integer departmentId;
  private String departmentName;

  public static ScheduleDto fromScheduleAndDepartment(Schedule schedule, Department department) {
    return new ScheduleDto(schedule.getId(), schedule.getStartDate(),schedule.getEndDate(), schedule.getDepartmentId(), department.getName());
  }

}
