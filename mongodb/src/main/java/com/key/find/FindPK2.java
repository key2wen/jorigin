package com.key.find;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FindPK2 {

    public static void handle(List<String> dbNames, String taskName, String startPKJson, int fetchSize) {

        Map<String, MongoCursor<Document>> cursors = new ConcurrentHashMap<>();
        try {
            while (taskNotFinished(taskName)) {
                Set<String> pkSet = new HashSet();
                for (String dbName : dbNames) {
                    int fetchCount = 0;
                    List<String> pkFields = getPKFields();
                    List<String> pkValues = getPKValues(startPKJson);
                    MongoCursor<Document> cursor = cursors.computeIfAbsent(dbName, key -> {
                                MongoClient mongoClient = getFromMongoDynamicDataSource(key);
                                MongoDatabase database = mongoClient.getDatabase(getDatabaseName(taskName));
                                MongoCollection<Document> collection = database.getCollection(getTableName(taskName));
                                return collection.find(buildFilters(pkFields, pkValues))
                                        .projection(Projections.include(pkFields))
                                        .sort(Sorts.ascending(pkFields))
                                        .cursor();
                            }
                    );
                    while (cursor.hasNext()) {
                        if (fetchCount >= fetchSize) {
                            continue;
                        }
                        pkSet.add(getPK(cursor.next()));
                        fetchCount++;
                    }
                    if (!cursor.hasNext()) {
                        setThisDbFinished(dbName, taskName);
                    }
                }
                handle(pkSet);
            }
        } catch (Exception e) {
            //todo: log and alarm
        } finally {
            close(cursors);
        }
    }

    private static void setThisDbFinished(String dbName, String taskName) {
    }

    private static boolean taskNotFinished(String taskName) {
        return false;
    }


    private static void close(Map<String, MongoCursor<Document>> cursors) {

    }

    private static String getPK(Document doc) {
        return doc.getString("_id");
    }

    private static Bson buildFilters(List<String> pkFields, List<String> pkValues) {
        int startIndex = 0;
        List<Bson> filters = new ArrayList<>();
        return recursion(pkFields, pkValues, startIndex);
    }

    private static Bson recursion(List<String> pkFields, List<String> pkValues, int startIndex) {
        if (pkFields.size() - startIndex == 1) {
            return Filters.gt(pkFields.get(startIndex), pkValues.get(startIndex));
        } else {
            return recursion(pkFields, pkValues, ++startIndex);
        }
    }

    private static List<String> getPKValues(String startPKJson) {
        return null;
    }

    private static void handle(Set<String> pkSet) {
        //todo
    }

    private static List<String> getPKFields() {
        //todo
        return null;
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
