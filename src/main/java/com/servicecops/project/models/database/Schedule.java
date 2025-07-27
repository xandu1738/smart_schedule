package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime; // Use java.time for modern date/time API
import java.time.ZoneOffset; // To handle UTC offset if needed for comparison
import java.time.ZoneId; // For specific time zones

@Getter
@Setter
@Entity
@Table(name = "schedule")
public class Schedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "start_date", nullable = false)
  private Timestamp startDate;

  @Column(name = "end_date", nullable = false)
  private Timestamp endDate;

  @Column(name = "department_id", nullable = false)
  private Integer departmentId;

  @Column(name = "institution_id", nullable = false)
  private Integer institutionId;

  @Transient
  private Boolean is_active;
  public Boolean getIs_active() {

    LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
    LocalDateTime scheduleStartUtc = this.startDate.toLocalDateTime();
    LocalDateTime scheduleEndUtc = this.endDate.toLocalDateTime();


    this.is_active = !nowUtc.isBefore(scheduleStartUtc) && !nowUtc.isAfter(scheduleEndUtc);
    return this.is_active;
  }
}
