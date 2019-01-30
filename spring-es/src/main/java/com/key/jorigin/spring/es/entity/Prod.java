package com.key.jorigin.spring.es.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(indexName = "springes", type = "prod", shards = 1, replicas = 0)
public class Prod implements Serializable{

    @Id
    private Long id;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String name;

    private Long price;

    @Field(type = FieldType.String, index = FieldIndex.analyzed)
    private String desc;

    @Field(type = FieldType.Date)
    private Date createTime;

    private Long stock; //库存

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String country;

    @Field(type = FieldType.Nested)
    private List<Suit> suitList;

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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Suit> getSuitList() {
        return suitList;
    }

    public void setSuitList(List<Suit> suitList) {
        this.suitList = suitList;
    }
}
