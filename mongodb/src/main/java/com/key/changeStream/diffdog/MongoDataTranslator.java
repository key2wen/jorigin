package com.key.changeStream.diffdog;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.key.changeStream.diffdog.Constants.OBJECT_ID_FIELD_NAME;


/**
 * ChangeStreamMsgTranslator
 *
 * @author real
 */
public class MongoDataTranslator implements Translator<ChangeStreamDocument<Document>, CollectData> {

    @Override
    public List<CollectData> convert2List(MessageKey messageKey, ChangeStreamDocument<Document> streamDocument) {
        List<CollectData> collectDatas = new ArrayList<>();

        String position = streamDocument.getResumeToken().toJson();

        BsonValue pk = streamDocument.getDocumentKey().get(OBJECT_ID_FIELD_NAME);
        BsonType type = pk.getBsonType();
        String value = getValueByBsonType(pk, type);

        collectDatas.add(buildCollectData(messageKey, position, type, value));

        if (streamDocument.getOperationType().equals(OperationType.REPLACE)) {
            Object newPK = streamDocument.getFullDocument().get(OBJECT_ID_FIELD_NAME);
            String newValue;
            BsonType newType;
            if (newPK instanceof BsonValue) {
                BsonValue newBsonPK = (BsonValue) newPK;
                newType = newBsonPK.getBsonType();
                newValue = getValueByBsonType(newBsonPK, newType);
            } else {
                Pair<String, BsonType> newValueAndType = BsonCvrtUtils.getValueByJavaBaseType(newPK);
                newType = newValueAndType.getRight();
                newValue = newValueAndType.getLeft();
            }
            //replace operate: use new _id replace old _id, need compare new _id
            if (!value.equals(newValue)) {
                collectDatas.add(buildCollectData(messageKey, position, newType, newValue));
            }
        }
        return collectDatas;
    }

    @Override
    public CollectData convert2Obj(MessageKey messageKey, ChangeStreamDocument<Document> data) {
        throw new RuntimeException("dont support convert2Obj");
    }


    private CollectData buildCollectData(MessageKey messageKey, String position, BsonType type, String value) {
        PrimaryKey key = PrimaryKey.builder()
                .attributes(Arrays.asList(Attribute.builder().name(OBJECT_ID_FIELD_NAME).type(type.name()).value(value).build()))
                .build();
        return CollectData.builder().primaryKey(key).position(position).key(messageKey).build();
    }

    private String getValueByBsonType(BsonValue bsonValue, BsonType type) {
        return BsonCvrtUtils.getStringValueByBsonType(bsonValue, type);
    }

}
