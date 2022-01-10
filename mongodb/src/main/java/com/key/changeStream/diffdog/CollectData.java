package com.key.changeStream.diffdog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author real
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CollectData {

    public PrimaryKey primaryKey;
    public MessageKey key;
    public String position;

}
