package com.key.jorigin.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Version {

    private Version() {
    }

    private static final Logger logger = LoggerFactory.getLogger(Version.class);

    private static final Pattern VERSION_PATTERN = Pattern.compile("([0-9][0-9\\.\\-]*)\\.jar");

    private static final String VERSION = getVersion(Version.class, "2.0.0");

    public static String getVersion() {
        return VERSION;
    }

    public static String getVersion(Class cls, String defaultVersion) {
        try {
            // 首先查找MANIFEST.MF规范中的版本号  
            String version = cls.getPackage().getImplementationVersion();
            if (version == null || version.length() == 0) {
                version = cls.getPackage().getSpecificationVersion();
            }
            if (version == null || version.length() == 0) {
                // 如果MANIFEST.MF规范中没有版本号，基于jar包名获取版本号  
                String file = cls.getProtectionDomain().getCodeSource().getLocation().getFile();
                if (file != null && file.length() > 0 && file.endsWith(".jar")) {
                    Matcher matcher = VERSION_PATTERN.matcher(file);
                    while (matcher.find() && matcher.groupCount() > 0) {
                        version = matcher.group(1);
                    }
                }
            }
            // 返回版本号，如果为空返回缺省版本号  
            return version == null || version.length() == 0 ? defaultVersion : version;
        } catch (Throwable e) { // 防御性容错  
            // 忽略异常，返回缺省版本号  
            logger.error(e.getMessage(), e);
            return defaultVersion;
        }
    }

    /**
     * 次应用一出错，应用的开发或测试就会把出错信息发过来，询问原因，这时候我都会问一大堆套话，
     * 用的哪个版本呀？是生产环境还是开发测试环境？哪个注册中心呀？哪个项目中的？哪台机器呀？哪个服务?
     * 累啊，最主要的是，有些开发或测试人员根本分不清，没办法，只好提供上门服务，浪费的时间可不是浮云，
     * 所以，日志中最好把需要的环境信息一并打进去，最好给日志输出做个包装，统一处理掉，免得忘了。包装Logger接口如
     *
     * @param msg
     * @param e
     */

    public static void error(String msg, Throwable e) {
        try {
            logger.error(msg + " on server " + InetAddress.getLocalHost() + " using version " + Version.getVersion(), e);
            System.out.println(msg + " on server " + InetAddress.getLocalHost() + " using version " + Version.getVersion(Logger.class, "1.0.0"));
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] a) {
        error("error_msg", new NullPointerException("haha_null_la"));
    }
}