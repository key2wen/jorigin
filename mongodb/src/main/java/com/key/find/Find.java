package com.key.find;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import java.util.Arrays;

public class Find {
    public static void main(String[] args) {

        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "<connection string uri>";

        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
            MongoCollection<Document> collection = database.getCollection("movies");

            Bson projectionFields = Projections.fields(
                    Projections.include("title", "imdb"),
                    Projections.excludeId());

            MongoCursor<Document> cursor = collection.find(Filters.lt("runtime", 15))
                    .projection(projectionFields)
                    .sort(Sorts.descending("title")).iterator();

            database.listCollections().iterator().next();

            collection.find(Filters.in("x", Arrays.asList()));

            try {
                while (cursor.hasNext()) {
                    System.out.println(cursor.next().toJson());
                }
            } finally {
                cursor.close();
            }
        }
    }
}
