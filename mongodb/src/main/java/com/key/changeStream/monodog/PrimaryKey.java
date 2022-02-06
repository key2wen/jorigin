package com.key.changeStream.monodog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PrimaryKey {
    private List<Attribute> attributes;

    public boolean addAttribute(Attribute attribute) {
        return attributes.add(attribute);
    }
}
