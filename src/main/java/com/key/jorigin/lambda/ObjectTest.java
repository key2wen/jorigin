package com.key.jorigin.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObjectTest {

    private String name = null;
    private int    age;
    private double score;

    public ObjectTest(){

    }

    public ObjectTest(String string, int i, double nextDouble){
        this.name = string;
        this.age = i;
        this.score = nextDouble;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public static List<ObjectTest> getListObjects() {
        List<ObjectTest> list = new ArrayList<ObjectTest>();

        for (int i = 0; i < 10; i++) {
            ObjectTest o = new ObjectTest("name" + (i%2==0?"":i), i + 10, new Random().nextDouble());
            list.add(o);
        }

        return list;
    }

    @Override
    public String toString() {
        return "ObjectTest [name=" + name + ", age=" + age + ", score=" + score + "]";
    }
    
}
