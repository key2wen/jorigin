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

    @Field(type = FieldType.Object)
    private Country countryInfo;


    //add
    private Long brandId;

    private Long categoryId;

    private Integer sale; //销量

    @Field(index = FieldIndex.analyzed, type = FieldType.String)
    private String searchName;

    @Field(index = FieldIndex.analyzed, type = FieldType.String)
    private String searchBran;

    //    @Field(analyzer = "ik_max_word",type = FieldType.String)
    @Field(index = FieldIndex.analyzed, type = FieldType.String)
    private String keywords;

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

    public Country getCountryInfo() {
        return countryInfo;
    }

    public void setCountryInfo(Country countryInfo) {
        this.countryInfo = countryInfo;
    }


    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSale() {
        return sale;
    }

    public void setSale(Integer sale) {
        this.sale = sale;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getSearchBran() {
        return searchBran;
    }

    public void setSearchBran(String searchBran) {
        this.searchBran = searchBran;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
