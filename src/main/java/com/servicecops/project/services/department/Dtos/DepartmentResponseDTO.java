package com.servicecops.project.services.department.Dtos;

import com.servicecops.project.models.database.Department;
import lombok.*;

import java.time.Instant;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponseDTO {

  private Integer id;
  private String name;
  private Long institutionId;
  private String description;
  private Integer noOfEmployees;
  private String managerName;
  private Instant createdAt;
  private boolean active;

  public static DepartmentResponseDTO fromDepartment(Department department) {
    return new DepartmentResponseDTO(department.getId(), department.getName(), department.getInstitutionId(),department.getDescription(),department.getNoOfEmployees(), department.getManagerName(), department.getCreatedAt(), department.isActive());
  }

}
