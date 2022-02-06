package com.key.changeStream.monodog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;


@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class MessageKey {

    public String tableName;
    public String taskName;
    public String dbName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageKey that = (MessageKey) o;
        return Objects.equals(tableName, that.tableName) &&
                Objects.equals(taskName, that.taskName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, taskName);
    }
}
