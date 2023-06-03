CREATE TABLE if not exists member
(
    id   BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10) NOT NULL UNIQUE
);

CREATE TABLE if not exists attendance_record
(
    id              BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id       BIGINT NOT NULL,
    attendance_code INT,
    date            DATE DEFAULT CURRENT_DATE
);

CREATE TABLE if not exists slack_log
(
    id   BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ts   VARCHAR(255) NOT NULL,
    date DATE DEFAULT CURRENT_DATE
);
