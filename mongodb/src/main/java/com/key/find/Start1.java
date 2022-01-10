package com.key.find;


import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Start1 {
    public static void main(String[] args) {

        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "mongodb://127.0.0.1:27017";

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
            MongoCollection<Document> collection = database.getCollection("movies");

            Document doc = collection.find(eq("title", "Back to the Future")).first();
            System.out.println(doc == null ? null : doc.toJson());

            collection.find(eq("title", "Back to the Future"));


        }
    }
}
