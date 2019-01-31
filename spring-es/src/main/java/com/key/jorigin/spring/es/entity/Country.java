package com.key.jorigin.spring.es.entity;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class Country {

    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String name;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String usName;

    @Field(type = FieldType.String, index = FieldIndex.analyzed)
    private String desc;

    @Field(type = FieldType.String, index = FieldIndex.analyzed)
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsName() {
        return usName;
    }

    public void setUsName(String usName) {
        this.usName = usName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", usName='" + usName + '\'' +
                ", desc='" + desc + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
