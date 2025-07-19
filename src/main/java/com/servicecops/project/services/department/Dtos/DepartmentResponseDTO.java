package com.servicecops.project.services.department.Dtos;

import lombok.Data;
import java.time.Instant;

public class DepartmentResponseDTO {

  private Integer id;
  private String name;
  private Long institutionId;
  private String description;
  private Integer noOfEmployees;
  private String managerName;
  private Instant createdAt;
  private boolean active;

  // Constructors
  public DepartmentResponseDTO() {}

  public DepartmentResponseDTO(
    Integer id,
    String name,
    Long institutionId,
    String description,
    Integer noOfEmployees,
    String managerName,
    Instant createdAt,
    boolean active
  ) {
    this.id = id;
    this.name = name;
    this.institutionId = institutionId;
    this.description = description;
    this.noOfEmployees = noOfEmployees;
    this.managerName = managerName;
    this.createdAt = createdAt;
    this.active = active;
  }

  // Getters and Setters (can use Lombok @Getter/@Setter here too)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getInstitutionId() {
    return institutionId;
  }

  public void setInstitutionId(Long institutionId) {
    this.institutionId = institutionId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getNoOfEmployees() {
    return noOfEmployees;
  }

  public void setNoOfEmployees(Integer noOfEmployees) {
    this.noOfEmployees = noOfEmployees;
  }

  public String getManagerName() {
    return managerName;
  }

  public void setManagerName(String managerName) {
    this.managerName = managerName;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
