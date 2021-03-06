
/**
 * Author:  Koby
 * Created: Sep 25, 2018
 */

-- DROP TABLE app_task_results IF EXISTS;
-- DROP TABLE app_log_entries IF EXISTS;

CREATE TABLE IF NOT EXISTS app_task_results
(
STD_OUTPUT VARCHAR(512),
DATE_CREATED TIMESTAMP
);

CREATE TABLE IF NOT EXISTS app_log_entries
(
STD_OUTPUT VARCHAR(2048),
DATE_CREATED TIMESTAMP,
LOG_LEVEL VARCHAR(10)
);