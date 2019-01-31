package com.key.jorigin.spring.es.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(indexName = "springes", type = "prod", shards = 1, replicas = 0)
public class Prod implements Serializable {

    @Id
    private Long id;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String name;

    private Long price;

    @Field(type = FieldType.String, index = FieldIndex.analyzed)
    private String desc;

    @Field(type = FieldType.Date)
//    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
//    @JsonProperty(value = "@timestamp")
//    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd'T'HH:mm:ss")
//    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private Date createTime;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private String createTime2;

    @Field(type = FieldType.Date)
    private Date updateTime;

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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime2() {
        return createTime2;
    }

    public void setCreateTime2(String createTime2) {
        this.createTime2 = createTime2;
    }
}
