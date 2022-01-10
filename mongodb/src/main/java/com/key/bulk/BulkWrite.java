package com.key.bulk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.client.model.*;
import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class BulkWrite {
    public static void main(String[] args) {
        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "<connection string uri>";

        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
            MongoCollection<Document> collection = database.getCollection("movies");

            try {

                List<WriteModel<? extends Document>> list2 = new ArrayList<>();
                list2.add(new InsertOneModel<>(new Document("name", "A Sample Movie")));
//                list2.add(new InsertOneModel<>(Filters.eq("name", "A Sample Movie")));
                collection.bulkWrite(list2);

                BulkWriteResult result = collection.bulkWrite(Arrays.asList(
                        new InsertOneModel<>(new Document("name", "A Sample Movie")),
                        new InsertOneModel<>(new Document("name", "Another Sample Movie")),
                        new InsertOneModel<>(new Document("name", "Yet Another Sample Movie")),
                        new UpdateOneModel<>(new Document("name", "A Sample Movie"),
                                new Document("$set", new Document("name", "An Old Sample Movie")),
                                new UpdateOptions().upsert(true)),
                        new DeleteOneModel<>(new Document("name", "Yet Another Sample Movie")),
                        new ReplaceOneModel<>(new Document("name", "Yet Another Sample Movie"),
                                new Document("name", "The Other Sample Movie").append("runtime", "42"))
                ));

                System.out.println("Result statistics:" +
                        "\ninserted: " + result.getInsertedCount() +
                        "\nupdated: " + result.getModifiedCount() +
                        "\ndeleted: " + result.getDeletedCount());

            } catch (MongoException me) {
                System.err.println("The bulk write operation failed due to an error: " + me);
            }
        }
    }
}

