package com.key.jorigin.lambda;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * @author whzhang 2016年1月22日
 *
 */
public class LambdaLog {

    static Logger logger = LoggerFactory.getLogger(LambdaLog.class);

    public static String getPre() {
        return "LOG PRE : ";
    }

    public static void info(Logger log, String msg, Object... params) {
        log.info(getPre() + msg, params);
    }

    public static void debug(Supplier<String> supplier) {
        if (logger.isDebugEnabled()) {
            logger.debug(supplier.get());
        }
    }

    public static void main(String[] args) {

        DOMConfigurator.configure(LambdaLog.class.getResource("/log4j-wenwen.xml"));

        LambdaLog.debug(() -> "log msg");
        LambdaLog.info(logger, "msg : {}, {}", "xxx", "xxxx2");
    }
}
