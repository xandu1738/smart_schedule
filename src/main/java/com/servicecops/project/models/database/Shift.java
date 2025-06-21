package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "shift")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "shift_duration")
    private Integer shiftDuration;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "max_people")
    private Integer maxPeople;

}