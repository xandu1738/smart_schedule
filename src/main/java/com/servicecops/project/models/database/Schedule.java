package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

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
  private Boolean isAssigned;


}
