package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "shift_swap_request")
public class ShiftSwapRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "from_employee")
    private Integer fromEmployee;

    @Column(name = "to_employee")
    private Integer toEmployee;

    @Column(name = "shift_id")
    private Integer shiftId;

    @Column(name = "status")
    private String status;

    @Column(name = "approved_by")
    private Long approvedBy;

}