package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "institution")
public class Institution {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "code", nullable = false, unique = true) // Added unique=true for code
  private String code;

  // New field: Description of the institution
  @Column(name = "description")
  private String description;

  // New field: Name of the owner of the institution
  @Column(name = "owner_name")
  private String ownerName;

  // New field: Physical location of the institution
  @Column(name = "location")
  private String location;

  // New field: Registration number of the institution (assumed unique)
  @Column(name = "reg_no", unique = true)
  private String regNo;

  // New field: Year the institution was established
  @Column(name = "year_established")
  private String yearEstablished;

  // New field: Type of institution (e.g., University, College, School)
  @Column(name = "institution_type")
  private String institutionType;

  @ColumnDefault("now()")
  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "active")
  private boolean active;

}
