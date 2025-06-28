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
@Table(name = "shift_assignment")
public class ShiftAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "shift_id")
    private Integer shiftId;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "status")
    private String status;

    @Column(name = "assigned_by")
    private Integer assignedBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Timestamp updatedAt;

}