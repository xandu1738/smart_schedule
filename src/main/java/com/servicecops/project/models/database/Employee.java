package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "employee")
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "department", nullable = false)
  private Integer department;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "status", nullable = false)
  private String status;

  @ColumnDefault("now()")
  @Column(name = "created_at")
  private Timestamp createdAt;

  @Column(name = "days_off_used", nullable = false)
  private Integer daysOffUsed;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "active")
  private boolean active;

  @Column(name = "updated_at")
  private Timestamp updatedAt;

  @Column(name = "updated_by")
  private Long updatedBy;

}
