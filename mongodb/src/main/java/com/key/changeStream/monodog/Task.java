package com.key.changeStream.monodog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    protected Long id;
    protected String name;
    protected String description;
    protected String taskType;
    protected String type;
    protected List<String> sourceDbs;
    protected List<String> targetDbs;
    protected String status;
    protected String tableMapping;
    protected String filterPolicy;
    protected String nodeAddress;
    /**
     * save the mongo change stream current position
     */
    protected String incrementalPosition;
    protected String zmCluster;
    protected String creator;
    protected String team;
    protected Instant createdTimeUtc;
    protected Instant lastModifiedTimeUtc;

    /**
     * the mq topics of this task. Json type
     *  {"topic1":"datasourceName1", "topic2":"datasourceName2"}
     */
    protected Map<String, String> mqTopics;
}
