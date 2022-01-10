package com.key.changeStream;

import com.mongodb.Block;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.FullDocument;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * https://docs.mongodb.com/manual/reference/change-events/
 * <p>
 * Starting in MongoDB 4.0, you can specify a startAtOperationTime to open the cursor at a particular point in time.
 * If the specified starting point is in the past, it must be in the time range of the oplog.
 * <p>
 * Change streams only notify on data changes that have persisted to a majority of data-bearing members in the replica set.
 * This ensures that notifications are triggered only by majority-committed changes that are durable in failure scenarios.
 * <p>
 * For example, consider a 3-member replica set with a change stream cursor opened against the primary.
 * If a client issues an insert operation, the change stream only notifies the application of the data change once that insert has persisted to a majority of data-bearing members.
 * <p>
 * If an operation is associated with a transaction, the change event document includes the txnNumber and the lsid.
 * <p>
 * Change stream response documents must adhere to the 16MB BSON document limit.
 * <p>
 * Change streams provide a total ordering of changes across shards by utilizing a global logical clock.
 * <p>
 * If a sharded collection has high levels of activity,
 * the mongos may not be able to keep up with the changes across all of the shards.
 * Consider utilizing notification filters for these types of collections.
 * For example, passing a $match pipeline configured to filter only insert operations.
 * <p>
 * The documentKey field includes both the _id and the userName field.
 * This indicates that the engineering.users collection is sharded, with a shard key on userName and _id.
 */
public class Watch {
    public static void main(String[] args) {

        // Replace the uri string with your MongoDB deployment's connection string
        String uri = "<connection string uri>";

        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
            MongoCollection<Document> collection = database.getCollection("movies");

            List<Bson> pipeline = Arrays.asList(
                    Aggregates.match(Filters.in("operationType",
                            Arrays.asList("insert", "update"))));

            ChangeStreamIterable<Document> changeStream = database.watch(pipeline)
                    .fullDocument(FullDocument.UPDATE_LOOKUP);
            // variables referenced in a lambda must be final; final array gives us a mutable integer
            final int[] numberOfEvents = {0};

            changeStream.forEach((Consumer<? super ChangeStreamDocument<Document>>) event -> {
                System.out.println("Received a change to the collection: " + event);
                if (++numberOfEvents[0] >= 2) {
                    System.exit(0);
                }
            });

            //...
            MongoCursor<ChangeStreamDocument<Document>> cursor = changeStream.iterator();
            while (cursor.hasNext()) {
                ChangeStreamDocument<Document> doc = cursor.next();
            }
        }
    }
/**
 * MongoClient mongoClient = new MongoClient( new MongoClientURI("mongodb://host1:port1,host2:port2..."));
 *
 * // Select the MongoDB database and collection to open the change stream against
 *
 * MongoDatabase db = mongoClient.getDatabase("myTargetDatabase");
 *
 * MongoCollection<Document> collection = db.getCollection("myTargetCollection");
 *
 * // Create $match pipeline stage.
 * List<Bson> pipeline = singletonList(Aggregates.match(Filters.or(
 *    Document.parse("{'fullDocument.username': 'alice'}"),
 *    Filters.in("operationType", asList("delete")))));
 *
 * // Create the change stream cursor, passing the pipeline to the
 * // collection.watch() method
 *
 * MongoCursor<Document> cursor = collection.watch(pipeline).iterator();
 */
}
