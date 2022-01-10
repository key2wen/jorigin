package com.key.find;

import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class FindPKs {

    public static void handle(List<String> dbNames, String taskName, String startPKJson, int fetchSize) {
        while (true) {
            Set<String> pkSet = new HashSet();
            try {
                for (String dbName : dbNames) {
                    MongoClient mongoClient = getFromMongoDynamicDataSource(dbName);
                    MongoDatabase database = mongoClient.getDatabase(getDatabaseName(taskName));
                    MongoCollection<Document> collection = database.getCollection(getTableName(taskName));

                    List<String> pkFields = getPKFields();
                    List<String> pkValues = getPKValues();
                    collection.find(buildFilters(pkFields, pkValues))
                            .projection(Projections.include(pkFields))
                            .sort(Sorts.ascending("_id"))
                            .limit(fetchSize)
                            .batchSize(fetchSize)
                            .forEach((Consumer<? super Document>) doc -> pkSet.add(doc.getString("_id")));
                }
                handle(pkSet);
            } catch (Exception e) {
                //todo: log and alarm
            }
        }
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

    private static List<String> getPKValues() {
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
