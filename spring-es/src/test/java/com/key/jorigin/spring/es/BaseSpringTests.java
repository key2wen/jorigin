package com.key.jorigin.spring.es;

import com.key.jorigin.spring.es.entity.Prod;
import com.key.jorigin.spring.es.entity.Suit;
import com.key.jorigin.spring.es.repository.ProdRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Test
    public void save() {

        Prod prod = getProd(2);

        Prod res = prodRepository.save(prod);

        System.out.println(res);

    }

    @Test
    public void get(){
        Prod prod = prodRepository.findOne(1l);
        System.out.println(prod);
    }

    private Prod getProd(int p) {

        Prod prod = new Prod();
        prod.setCountry("美国");
        prod.setCreateTime(new Date());
        prod.setDesc("hah nice country haha i like it");
        prod.setId((long)p);
        prod.setName("美国7日游");
        prod.setPrice(1000l);
        prod.setStock(10l);

        List<Suit> suitList = new ArrayList<>(2);
        for(int i = 0; i < 2; i++){
            Suit suit = new Suit();
            suit.setId(i + 1l);
            suit.setDesc(i + "好东西真不错");
            suit.setName("套餐"+i);
            suit.setPrice(i + 1000l);
            suitList.add(suit);
        }

        prod.setSuitList(suitList);
        return prod;
    }

}
