package com.zyn.common.constant;

/**
 * Kafka主题常量
 */
public final class KafkaTopics {

    private KafkaTopics() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 主题名称
    public static final String DETECTION_TASKS = "video-detection-tasks";
    public static final String DETECTION_RESULTS = "video-detection-results";
    public static final String DETECTION_NOTIFICATIONS = "detection-notifications";

    // 死信队列
    public static final String DETECTION_TASKS_DLQ = "video-detection-tasks-dlq";

    // 消费者组ID
    public static final String CONSUMER_GROUP_WORKERS = "detection-workers";
    public static final String CONSUMER_GROUP_NOTIFICATIONS = "notification-workers";

    // 分区数
    public static final int PARTITIONS_DETECTION_TASKS = 3;
    public static final int PARTITIONS_DETECTION_RESULTS = 3;
    public static final int PARTITIONS_NOTIFICATIONS = 1;

    // 副本数
    public static final short REPLICATION_FACTOR = 1;

    // 消息配置
    public static final int MAX_RETRY_ATTEMPTS = 3;
    public static final long RETRY_BACKOFF_MS = 2000L;
}
