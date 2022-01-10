package com.key.changeStream.diffdog;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.*;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


/**
 * BsonCvrtUtil util
 *
 * @author real
 */
public class BsonCvrtUtils {

    public static String getStringValueByBsonType(BsonValue bsonValue, BsonType type) {
        switch (type) {
            case OBJECT_ID:
                return bsonValue.asObjectId().getValue().toHexString();
            case STRING:
                return bsonValue.asString().getValue();
            case DOUBLE:
                return String.valueOf(bsonValue.asDouble().getValue());
            case INT32:
                return String.valueOf(bsonValue.asInt32().getValue());
            case INT64:
                return String.valueOf(bsonValue.asInt64().getValue());
            case DECIMAL128:
                return bsonValue.asDecimal128().getValue().toString();
            case TIMESTAMP:
                return String.valueOf(bsonValue.asTimestamp().getValue());
            case DATE_TIME:
                return String.valueOf(bsonValue.asDateTime().getValue());
            case BOOLEAN:
                return Boolean.toString(bsonValue.asBoolean().getValue());
            case BINARY:
                return bsonValue.asBinary().asUuid().toString();
            default:
                throw new RuntimeException("don't support bson type : " + type.name());
        }
    }

    public static BsonValue getDbObjectByString(String value, String type) {
        return null;
    }

    public static BsonValue getBsonValueObjByString(String value, String type) {
        BsonType bsonType = BsonType.valueOf(type);
        switch (bsonType) {
            case OBJECT_ID:
                return new BsonObjectId(new ObjectId(value));
            case STRING:
                return new BsonString(value);
            case DOUBLE:
                return new BsonDouble(Double.valueOf(value));
            case INT32:
                return new BsonInt32(Integer.valueOf(value));
            case INT64:
                return new BsonInt64(Long.parseLong(value));
            case DECIMAL128:
                return new BsonDecimal128(Decimal128.parse(value));
            case TIMESTAMP:
                return new BsonTimestamp(Long.parseLong(value));
            case DATE_TIME:
                return new BsonDateTime(Long.parseLong(value));
            case BOOLEAN:
                return new BsonBoolean(Boolean.valueOf(value));
            case BINARY:
                return new BsonBinary(UUID.fromString(value));
            default:
                throw new RuntimeException("don't support bson type : " + type);
        }
    }

    public static Pair<String, BsonType> getValueByJavaBaseType(Object obj) {
        if (obj instanceof Character) {
            return Pair.of(obj.toString(), BsonType.STRING);
        } else if (obj instanceof String) {
            return Pair.of((String) obj, BsonType.STRING);
        } else if (obj instanceof Number) {
            return handleNumberType(obj);
        } else if (obj instanceof Date) {
            return Pair.of(String.valueOf(((Date) obj).getTime()), BsonType.DATE_TIME);
        } else if (obj instanceof Timestamp) {
            return Pair.of(String.valueOf(((Timestamp) obj).getTime()), BsonType.TIMESTAMP);
        } else {
            throw new RuntimeException("don't support java object type : " + obj.getClass());
        }
    }

    private static Pair<String, BsonType> handleNumberType(Object obj) {
        String val = obj.toString();
        BsonType type;
        if (obj instanceof Integer) {
            type = BsonType.INT32;
        } else if (obj instanceof Long) {
            type = BsonType.INT64;
        } else if (obj instanceof Double) {
            type = BsonType.DOUBLE;
        } else if (obj instanceof Float) {
            type = BsonType.DOUBLE;
        } else if (obj instanceof Short) {
            type = BsonType.INT32;
        } else {
            throw new RuntimeException("don't support number object: " + obj.getClass());
        }
        return Pair.of(val, type);
    }

}
