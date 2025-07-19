package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "max_people")
    private Integer maxPeople;

}