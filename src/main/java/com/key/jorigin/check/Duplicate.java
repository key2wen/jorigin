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
        Duplicate.checkDuplicate(Logger.class);
    }

}