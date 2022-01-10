package com.key.command;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;


public class RunCommand {
    public static void main(String[] args) {
        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "<connection string uri>";

        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase("sample_mflix");

            try {
                Bson command = new BsonDocument("dbStats", new BsonInt64(1));
                Document commandResult = database.runCommand(command);
                System.out.println("dbStats: " + commandResult.toJson());
            } catch (MongoException me) {
                System.err.println("An error occurred: " + me);
            }
        }
    }
}
/**
 * dbStats: {"db": "sample_mflix", "collections": 5, "views": 0, "objects": 75595, "avgObjSize": 692.1003770090614,
 * "dataSize": 52319328, "storageSize": 29831168, "numExtents": 0, "indexes": 9, "indexSize": 14430208, "fileSize": 0,
 * "nsSizeMB": 0, "ok": 1}
 */
