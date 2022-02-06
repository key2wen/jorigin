package com.key.changeStream.monodog;

/**
 * @author real
 */
public class Constants {

    public static final int MAX_RETRY_COUNT = 3;

    public static final String BATCH_DATA_TABLE = "batch_data";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_DATA_SOURCE = "defaultDataSource";

    public static final String FILTER_POLICY_DELIMITER = "#";

    public static final String LEADER_LOCK_NAME = "ManagerLeaderLock";

    public static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    public static final String OK = "ok";

    public static final String PAUSE_TASK_URL_FORMAT = "http://%s/select/pause?taskName=%s";

    public static final String REQUEST_PARAM_DELIMITER = ":";

    public static final String RETRY_BATCH_DATA_TABLE = "retry_batch_data";

    public static final String RESUME_TASK_URL_FORMAT = "http://%s/select/resume?taskName=%s";

    public static final String SELECT_TASK_URL_FORMAT = "http://%s/select/start?taskName=%s";

    public static final String STOP_TASK_URL_FORMAT = "http://%s/select/shutdown?taskName=%s";

    public static final String FAIL = "failed";

    public static final String USERNAME = "username";

    public static final String ACCESS_TOKEN = "accessToken";

    public static final int MAX_ERROR_MESSAGE_LENGTH = 1000;

    public static final String OBJECT_ID_FIELD_NAME = "_id";

    public static final String POINT = ".";
}
