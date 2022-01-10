package com.key.changeStream;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * @author zhangwenhui
 */
public class DiffIncrDataCreator {

    String uri = "mongodb://localhost:27030";
    String collectionName = "test_incremental_1";

    @Test
    public void insert_int_id() {
        insert(uri, collectionName, RandomUtils.nextInt());
    }

    @Test
    public void insert_objectId() {
        insert(uri, collectionName, new ObjectId());
    }

    @Test
    public void insert_String_id() {
        insert(uri, collectionName, UUID.randomUUID().toString());
    }

    @Test
    public void insert_long_id() {
        insert(uri, collectionName, RandomUtils.nextLong());
    }

    @Test
    public void insert_double_id() {
        insert(uri, collectionName, RandomUtils.nextDouble());
    }

    @Test
    public void insert_date_id() {
        insert(uri, collectionName, new Date());
    }

    @Test
    public void insert_timestamp_id() {
        insert(uri, collectionName, new Timestamp(System.currentTimeMillis()));
    }

    private static void insert(String uri, String collectionName, Object _id) {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("real_test");
            MongoCollection collection = database.getCollection(collectionName);
            try {
                collection.insertOne(new Document()
                        .append("_id", _id)
                        .append("title", "title_" + RandomUtils.nextDouble())
                        .append("genres", Arrays.asList(RandomUtils.nextFloat(), RandomUtils.nextFloat())));
            } catch (MongoException me) {
                System.err.println("Unable to insert due to an error: " + me);
            }
        }
    }

    /**
     * resume token
     * {"_data": "82617256D2000000012B022C0100296E5A1004820D46C9E475484991403FB14ECF4ABF461E6964002B171CAC083126E980645F69640064617256D2D34E7914E0B064640004", "_typeBits": {"$binary": "QA==", "$type": "00"}}
     * {"_data": "82617256D2000000012B022C0100296E5A1004820D46C9E475484991403FB14ECF4ABF461E6964002B171CAC083126E980645F69640064617256D2D34E7914E0B064640004", "_typeBits": {"$binary": "QA==", "$type": "00"}}
     * {"_data": "8261725980000000012B022C0100296E5A1004820D46C9E475484991403FB14ECF4ABF461E6964002B171CED916872B000645F6964006461725980D34E7914E0B073190004", "_typeBits": {"$binary": "QA==", "$type": "00"}}
     * {"_data": "82617259D5000000012B022C0100296E5A1004820D46C9E475484991403FB14ECF4ABF461E6964002B171D2F1A9FBE7700645F69640064617259D5D34E7914E0B0750A0004", "_typeBits": {"$binary": "QA==", "$type": "00"}}
     */
    /**
     * int
     * {"_data": "8261726089000000022B022C0100296E5A100450F75BB399654BBF970A0E4599C27A84461E5F6964002EC78F4FF20004"}
     *
     * long
     * {"_data": "8261726160000000012B022C0100296E5A100450F75BB399654BBF970A0E4599C27A84461E5F69640032BDDB724E9E8980000004", "_typeBits": {"$binary": "gYA=", "$type": "00"}}
     * {"_data": "826172621B000000022B022C0100296E5A100450F75BB399654BBF970A0E4599C27A84461E5F696400328801293A505608000004", "_typeBits": {"$binary": "gYA=", "$type": "00"}}
     *
     * double
     * {"_data": "826172625E000000012B022C0100296E5A100450F75BB399654BBF970A0E4599C27A84461E5F696400337FCBA9483502A2500004", "_typeBits": {"$binary": "QA==", "$type": "00"}}
     *
     *
     * date:
     * {"_data": "8261726345000000012B022C0100296E5A100450F75BB399654BBF970A0E4599C27A8446785F696400788000017CDA728B010004"}
     *
     * timestamp
     * {"_data": "8261726552000000012B022C0100296E5A100450F75BB399654BBF970A0E4599C27A8446785F696400788000017CDA7A8ADB0004"}
     *
     * ObjectId:
     * {"_data": "82617266A5000000022B022C0100296E5A100450F75BB399654BBF970A0E4599C27A8446645F69640064617F9DA01DA66808C21BE6CD0004"}
     *
     * String
     * {"_data": "8261726779000000012B022C0100296E5A100450F75BB399654BBF970A0E4599C27A84463C5F6964003C64613134316564622D356664372D343337302D626437382D643438353331663039653535000004"}
     */

}

