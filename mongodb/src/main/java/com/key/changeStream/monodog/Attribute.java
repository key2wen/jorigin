package com.key.changeStream.monodog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Attribute {
    private String name;
    private String value;
    private String type;
}
