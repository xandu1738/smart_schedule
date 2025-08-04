package com.servicecops.project.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JdbcHelperService {
    private final JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 0 0 26 * ?")
    public void createPartition() {
        log.info("Creating new partition for Schedule Records");
        String sql = "SELECT auto_create_schedule_record_partition()";
        jdbcTemplate.execute(sql);
    }
    @Scheduled(cron = "0 0 0 * * ?")
    public void resolveTimeOffStatus() {
        log.info("Updating employees status");
        String sql = "SELECT check_time_off_status()";
        jdbcTemplate.execute(sql);
    }
}
