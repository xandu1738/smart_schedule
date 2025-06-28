package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "schedule_record")
public class ScheduleRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "schedule_id", nullable = false)
  private Integer scheduleId;

  @Column(name = "employee_id", nullable = false)
  private Integer employeeId;

  @Column(name = "shift_id", nullable = false)
  private Integer shiftId;

  @Column(name = "time_off_id", nullable = false)
  private Integer timeOffId;

  @Column(name = "date_created", nullable = false)
  private Timestamp dateCreated;

}
