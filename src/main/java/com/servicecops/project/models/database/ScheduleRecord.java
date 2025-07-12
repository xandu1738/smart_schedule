package com.servicecops.project.models.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "schedule_record")
public class ScheduleRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "schedule_id", nullable = false)
    private Integer scheduleId;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "shift_id", nullable = true)
    private Integer shiftId;

    @Column(name = "time_off_request_id", nullable = true)
    private Integer timeOffRequestId;

    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;

    @Column(name = "end_time", nullable = false)
    private Timestamp endTime;

    @Column(name = "date_created", nullable = false)
    private Timestamp dateCreated;

    @Column(name = "date_updated", nullable = false)
    private Timestamp dateUpdated;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

}
