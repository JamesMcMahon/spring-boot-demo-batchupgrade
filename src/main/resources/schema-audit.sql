CREATE TABLE audit_job
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name   VARCHAR(100),
    start_time TIMESTAMP,
    end_time   TIMESTAMP,
    status     VARCHAR(20)
);
