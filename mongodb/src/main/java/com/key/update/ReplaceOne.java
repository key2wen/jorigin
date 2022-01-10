package com.key.update;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;

/**
 * You can replace a single document using the replaceOne() method on a MongoCollection object.
 * This method removes all the existing fields and values from a document (except the _id field) and substitutes it with your replacement document.
 * <p>
 * The replaceOne() method accepts a query filter that matches the document you want to replace and a replacement document that contains the data you want to save in place of the matched document.
 * The replaceOne() method only replaces the first document that matches the filter.
 */
public class ReplaceOne {

    public static void main(String[] args) {
        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "<connection string uri>";

        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
            MongoCollection<Document> collection = database.getCollection("movies");

            Bson query = eq("title", "Music of the Heart");

            Document replaceDocument = new Document().
                    append("title", "50 Violins").
                    append("fullplot", " A dramatization of the true story of Roberta Guaspari who co-founded the Opus 118 Harlem School of Music");

            ReplaceOptions opts = new ReplaceOptions().upsert(true);

            UpdateResult result = collection.replaceOne(query, replaceDocument, opts);

            System.out.println("Modified document count: " + result.getModifiedCount());
            System.out.println("Upserted id: " + result.getUpsertedId()); // only contains a value when an upsert is performed

        } catch (MongoException me) {
            System.err.println("Unable to replace due to an error: " + me);
        }
    }
}
