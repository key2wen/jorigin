package com.key.changeStream.monodog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author real
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CollectFullData {

    public Set<PrimaryKey> primaryKeys;
    public MessageKey key;

}
