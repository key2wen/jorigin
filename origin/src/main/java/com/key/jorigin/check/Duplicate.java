package com.key.jorigin.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public final class Duplicate {

    static Logger logger = LoggerFactory.getLogger(Duplicate.class);

    private Duplicate() {
    }

    public static void checkDuplicate(Class cls) {
        checkDuplicate(cls.getName().replace('.', '/') + ".class");
    }

    public static void checkDuplicate(String path) {
        try {
            // 在ClassPath搜文件  
            Enumeration urls = Thread.currentThread().getContextClassLoader().getResources(path);
            Set files = new HashSet();
            while (urls.hasMoreElements()) {
                URL url = (URL) urls.nextElement();
                if (url != null) {
                    String file = url.getFile();
                    if (file != null && file.length() > 0) {
                        files.add(file);
                    }
                }
            }
            // 如果有多个，就表示重复  
            if (files.size() > 1) {
                logger.error("Duplicate class " + path + " in " + files.size() + " jar " + files);
            }

            for (Object file : files) {
                System.out.println("--->" + file);
            }

        } catch (Throwable e) { // 防御性容错  
            logger.error(e.getMessage(), e);
        }
    }

    public static void main(String[] a) {
        /**
         * 痴呆的问题，就是有多个版本的相同jar包，会出现新版本的 A 类，调用了旧版本的 B 类，而且和JVM加载顺序有关，问题带有偶然性，误导性，遇到这种莫名其妙的问题，最头疼，所以，第一条，先把它防住，在每个 jar 包中挑一个一定会加载的类，加上重复类检查
         */
        Duplicate.checkDuplicate(Logger.class);

        /**
         * 配置文件加载错，也是经常碰到的问题。用户通常会和你说：“我配置的很正确啊，不信我发给你看下，但就是报错”。然后查一圈下来，原来他发过来的配置根本没加载，平台很多产品都会在 classpath 下放一个约定的配置，如果项目中有多个，通常会取JVM加载的第一个，为了不被这么低级的问题折腾，和上面的重复jar包一样，在配置加载的地方，加上
         */
        Duplicate.checkDuplicate("xxx.properties");


    }

}