package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "departments")
public class Department {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "institution_id", nullable = false)
  private Long institutionId;

  @ColumnDefault("now()")
  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "active")
  private boolean active;

}
