package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "time_off_requests")
public class TimeOffRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "start_date", nullable = false)
    private Timestamp startDate;

    @Column(name = "end_date", nullable = false)
    private Timestamp endDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "reason")
    private String reason;

    @Column(name = "approved_by")
    private Integer approvedBy;

    @Column(name = "requested_by")
    private Integer requestedBy;

    @CreationTimestamp
    @Column(name = "requested_on")
    private Timestamp requestedOn;

    @Column(name = "approved_on")
    private Timestamp approvedOn;

}