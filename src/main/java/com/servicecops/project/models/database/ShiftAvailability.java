package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "shift_availability")
public class ShiftAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    @Column(name = "available_from")
    private Instant availableFrom;

    @Column(name = "available_to")
    private Instant availableTo;

}