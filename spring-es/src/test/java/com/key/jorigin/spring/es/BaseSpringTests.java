package com.key.jorigin.spring.es;

import com.alibaba.fastjson.JSON;
import com.key.jorigin.spring.es.component.EsComponent;
import com.key.jorigin.spring.es.entity.Country;
import com.key.jorigin.spring.es.entity.Prod;
import com.key.jorigin.spring.es.entity.Suit;
import com.key.jorigin.spring.es.repository.ProdRepository;
import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
public class BaseSpringTests extends AbstractJUnit4SpringContextTests {

    @Test
    public void start() {

        System.out.println("content starting....");

        CountDownLatch latch = new CountDownLatch(1);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    ProdRepository prodRepository;

    @Autowired
    EsComponent esComponent;

    @Test
    public void save() {

        Prod prod = getProd(2, null, null, null);

        Prod res = prodRepository.save(prod);

        System.out.println(res);

    }

    @Test
    public void saveList() {

        List<Prod> list = new ArrayList<>();
        String[] searchName = {
                "Typing a few words at random",
                "I like me. I'm glad you care about me.\n",
                "There are many fools in today's world. In fact, everyone is fools, but many people do not realize that they are fools.\n",
                "If you think people around you are silly, chances are you are",
                "A fool is not terrible. What is terrible is a fool who thinks he is in suspense",
                "Strive hard and stay away from fools",
                "Life is supposed to be like this, regardless of other people's eyes"};
        String[] searchBran = {
                "There is no end to learning",
                "Family love is their only treasure",
                "When you are unhappy, only your family can comfort you",
                "Life is short and life is long",
                "Be a kind person instead of a tolerant one",
                "Responsibility and responsibility",
                "Maybe your family will be happier if you do more",
        };

        String keyword[] = {
                "Ah, there's a keyword column",
                "In the field of hope",
                "The life you owe now will slowly let you repay it",
                "Healthy mindset is something you lack",
                "Make a year's general plan or the plan won't catch up with your changes",
                "Think more about the need for exercise in your mind",
                "He who has no talent and wisdom has to go ahead as fast as he can",
        };

        for (int i = 1; i <= 7; i++) {
            Prod prod = getProd(i, searchName[i - 1], searchBran[i - 1], keyword[i - 1]);
            list.add(prod);
        }
        Iterable<Prod> res = prodRepository.save(list);
        System.out.println(res);
    }

    @Test
    public void get() {
        Prod prod = prodRepository.findOne(1l);
        System.out.println(prod);
    }

    @Test
    public void del() {
        prodRepository.delete(1l);
        System.out.println("ok");
    }

    @Test
    public void update() {
        Prod prod = getProd(2, null, null, null); //已存在的对象

        Prod res = prodRepository.save(prod);

        System.out.println(res);
    }

    @Test
    public void simpleSearch() {

        String name = "美国10日游";
        String country = "泰";

        Pageable pageable = new PageRequest(0, 2);

        Page<Prod> res = prodRepository.findByNameAndCountry(name, country, pageable);

        System.out.println(res); //查不到，因为country是精确匹配字段

        Page<Prod> res1 = prodRepository.findByNameOrCountry(name, country, pageable);

        System.out.println(res1); //因为是or, 这能查出来
    }

    @Test
    public void search() {

        String keyword = "general";
        Long branId = 5l;
        Long categoryId = 5l;
        Integer sort = 1; //排序字段:0->按相关度；1->按新品；2->按销量；3->价格从低到高；4->价格从高到低",
        Page<Prod> list = esComponent.searchProd(keyword, branId, categoryId, 0, 3, sort);

        System.out.println(list);
    }


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testProdMapping() {

        boolean b = elasticsearchTemplate.putMapping(Prod.class);

        Map mapping = elasticsearchTemplate.getMapping(Prod.class);

        System.out.println(JSON.toJSONString(mapping));
    }


    private Prod getProd(int p, String searchName, String searchBran, String keyword) {

        Prod prod = new Prod();
        prod.setCountry("泰国");
        prod.setCreateTime(new Date());
        prod.setUpdateTime(new Date());
        prod.setCreateTime2(DateFormatUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));
        prod.setDesc("hah nice country haha i like it");
        prod.setId((long) p);
        prod.setName("美国10日游");
        prod.setPrice(new Random().nextLong());
        prod.setStock(new Random().nextLong());

        List<Suit> suitList = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            Suit suit = new Suit();
            suit.setId(i + 1l);
            suit.setDesc(i + "好东西真不错");
            suit.setName("套餐" + i);
            suit.setPrice(i + 1000l);
            suitList.add(suit);
        }

        prod.setSuitList(suitList);


        Country countryInfo = new Country();
        countryInfo.setId((long) p);
        countryInfo.setDesc("asdfasagaAGAL阿斯顿嘎嘎");
        countryInfo.setName("美国");
        countryInfo.setRemark("备注一下这个国家情况还不错");
        countryInfo.setUsName("America");
        prod.setCountryInfo(countryInfo);


        //add
        prod.setBrandId((long) p);
        prod.setCategoryId((long) p);
        prod.setSale(new Random().nextInt());
        prod.setSearchName(searchName);
        prod.setSearchBran(searchBran);
        prod.setKeywords(keyword);

        return prod;
    }

}
