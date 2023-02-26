package com.key.jorigin.java.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射：
 * https://github.com/lx1169732264/notes/blob/master/%E5%8F%8D%E5%B0%84.md
 * 反射
 * ==通过堆中的class对象访问到方法区中class文件== 即运行时动态获取类的方法
 * <p>
 * 动态获取类信息/调用对象方法 在运行时，对任意类，都能获取到这个类的所有属性和方法,对于任意对象，都能够调用它的任意方法和属性
 * <p>
 * 运行时对象存在两种类型：编译时类型和运行时类型。编译时的类型由声明对象时实用的类型来决定，运行时的类型由实际赋值给对象的类型决定
 * <p>
 * Person p=new Student(); //编译时类型Person，运行时类型Student
 * 反射解决了程序如何调用运行时类型方法的问题
 * <p>
 * 适用场景
 * <p>
 * 在编译时不知道该对象或类可能属于哪些类，通过反射可以使程序代码访问装载到JVM中的类的内部信息
 * 反射提高了灵活性和扩展性，低耦合。允许程序创建和控制任何类的对象，无需提前硬编码目标类
 * 反射是解释操作，用于字段和方法接入时效率低
 * 会模糊程序内部逻辑：程序人员希望在源代码中看到程序的逻辑，反射等绕过了源代码的技术，带来维护问题
 * Class
 * Class类是反射的入口，用于获取与类相关的各种信息和方法
 * <p>
 * 每个类也可看做是对象，有共同的Class来存放类的结构信息，能够通过相应方法取出相应信息：类名、属性、方法、构造方法、父类和接口
 * <p>
 * ==获取class对象4种方式== (反射的实现方式)
 * 1。类名.class
 * 2。Class.forName("包名")
 * 3。对象.getClass()
 * 4。类加载器.loadClass("包名") 类加载器获取class对象不会进行初始化,静态块和静态对象不会得到执行
 */
public class JavaReflect {

    private String stringField;

    public void test() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method[] methods = JavaReflect.class.getMethods();
        JavaReflect reflect = JavaReflect.class.newInstance();
        Annotation[] annotations = JavaReflect.class.getAnnotations();
        Field[] fields = JavaReflect.class.getFields(); // 获得所有public字段
        Field[] allFields = JavaReflect.class.getDeclaredFields(); // 获得所有字段
        allFields[0].setAccessible(true); // 忽略访问权限修饰符

        //泛型擦除
        List<JavaReflect> list = new ArrayList<>();
//        list.add(5); 编译无法通过
        Class<? extends List> listClass = list.getClass();
        Method add = listClass.getDeclaredMethod("add", Object.class);
        add.setAccessible(true);
        //通过invoke()避免了编译时的泛型检验, 成功插入到list
        add.invoke(list, 5);

        System.out.println(list.size());
        System.out.println(list);
    }

    public static void main(String[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        new JavaReflect().test();
    }

}
