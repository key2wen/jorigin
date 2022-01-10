package com.key.find;

import com.mongodb.Block;
import com.mongodb.client.*;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.function.Consumer;

public class FindPK {

    public static void handle(List<String> dbNames, String taskName, String startPK, int fetchSize) {
        while (true) {
            Set<String> pkSet = new HashSet();
            try {
                for (String dbName : dbNames) {
                    MongoClient mongoClient = getFromMongoDynamicDataSource(dbName);
                    MongoDatabase database = mongoClient.getDatabase(getDatabaseName(taskName));
                    MongoCollection<Document> collection = database.getCollection(getTableName(taskName));

                    collection.find(Filters.gt("_id", startPK))
                            .projection(Projections.include("_id"))
                            .sort(Sorts.ascending("_id"))
                            .limit(fetchSize)
                            .batchSize(fetchSize)
                            .forEach((Consumer<? super Document>) doc -> pkSet.add(doc.getString("_id")));

                    collection.find(Filters.gt("_id", new ObjectId("sdfsfd")));
                }
                handle(pkSet);
            } catch (Exception e) {
                //todo: log and alarm
            }
        }
    }

    private static void handle(Set<String> pkSet) {
        //todo
    }

    private static String getTableName(String taskName) {
        return null;
    }

    private static String getDatabaseName(String taskName) {
        return null;
    }

    private static MongoClient getFromMongoDynamicDataSource(String dbName) {
        return null;
    }


}
