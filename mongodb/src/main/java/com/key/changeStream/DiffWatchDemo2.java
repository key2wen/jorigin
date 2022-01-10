package com.key.changeStream;

import com.key.changeStream.diffdog.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author real
 */
public class DiffWatchDemo2 {

    Translator translator = null;

    //    String pos = "{\"_data\": \"82617259D5000000012B022C0100296E5A1004820D46C9E475484991403FB14ECF4ABF461E6964002B171D2F1A9FBE7700645F69640064617259D5D34E7914E0B0750A0004\", \"_typeBits\": {\"$binary\": \"QA==\", \"$type\": \"00\"}}";
//    String pos = "{\"_data\": \"82617266A5000000022B022C0100296E5A100450F75BB399654BBF970A0E4599C27A8446645F69640064617F9DA01DA66808C21BE6CD0004\"}";
    String pos = "{\"_data\": \"xxxx82617266A5000000022B022C0100296E5A100450F75BB399654BBF970A0E4599C27A8446645F69640064617F9DA01DA66808C21BE6CD0004\"}";
    String taskName = "taskName1";
    String dbName = "dbName1";

    @Test
    public void start() {

        DiffWatchDemo2 watchDiffDemo = new DiffWatchDemo2();
        watchDiffDemo.translator = new MongoDataTranslator();
        Task task = getTask(taskName);
        watchDiffDemo.startTask(task, taskName, dbName);
    }

    @Test
    public void startWithPos() {
        DiffWatchDemo2 watchDiffDemo = new DiffWatchDemo2();
        watchDiffDemo.translator = new MongoDataTranslator();
        Task task = getTaskWithPos(taskName, pos);
        watchDiffDemo.startTask(task, taskName, dbName);
    }

    private void startTask(Task task, String taskName, String dbName) {

        try {

            MongoClient mongoClient = getFromMongoDynamicDataSource(taskName);

            List<Bson> pipeline = Arrays.asList(Aggregates.match(Filters.in("operationType", Arrays.asList("insert", "update", "delete", "replace"))));
            BsonDocument startBson = getPosition(task, dbName);
            ChangeStreamIterable<Document> changeStreams = mongoClient.watch(pipeline);
            if (startBson != null) {
                changeStreams = changeStreams.startAfter(startBson);
            }

            //failed point
            MongoCursor<ChangeStreamDocument<Document>> cursor = changeStreams.iterator();
            while (true) {
                while (cursor.hasNext()) {
                    ChangeStreamDocument<Document> doc = cursor.next();
                    System.out.println(doc.toString());
                    handleDoc(dbName, taskName, doc);
                }
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Task getTask(String taskName) {
        Task task = Task.builder().name("taskName1").incrementalPosition(null).build();
        return task;
    }

    private Task getTaskWithPos(String taskName, String pos) {
        Task task = Task.builder().name("taskName1").incrementalPosition(pos).build();
        return task;
    }


    private MongoClient getFromMongoDynamicDataSource(String taskName) {
//        String uri = "mongodb://localhost:27030";
        String uri = "mongodb://localhost:27050";
        return MongoClients.create(uri);
    }

    private String getDatabaseName(String taskName) {
        return "real_test";
    }


    private void handleDoc(String dbName, String taskName, ChangeStreamDocument<Document> doc) {

        MessageKey messageKey = MessageKey.builder()
                .taskName(taskName)
                .tableName(doc.getNamespace().getCollectionName())
                .dbName(dbName)
                .build();

        translator.convert2List(messageKey, doc);
    }


    private BsonDocument getPosition(Task task, String dbName) {

        //todo: verify
        String positionJson = task.getIncrementalPosition();
        if (positionJson != null) {
            return BsonDocument.parse(positionJson);
        }

        if (StringUtils.isBlank(positionJson)) {
            return null;
        }

        Map<String, Object> dbPosition = GsonUtil.deserialize(positionJson, Map.class);
        Object position = dbPosition.get(dbName);
        if (position == null) {
            return null;
        }
        return BsonDocument.parse(GsonUtil.serialize(position));
    }

}

