package com.key.jorigin.groovy;

import groovy.lang.*;
import groovy.util.GroovyScriptEngine;
import groovy.util.ScriptException;
import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroovyTest<T> {

    // 方式一，使用GroovyClassLoader调用
    @Test
    public <T> T invoke01(String scriptText, String func, Object... objs) throws IllegalAccessException, InstantiationException {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
        Class groovyClazz = groovyClassLoader.parseClass(scriptText);

        GroovyObject groovyObject = (GroovyObject) groovyClazz.newInstance();
        Object result = groovyObject.invokeMethod(func, objs);
        return (T) result;
    }

    // 方式二，使用ScriptEngine调用
    @Test
    public <T> T invoke02(String scriptText, String func, Object... objs) throws ScriptException,
            NoSuchMethodException, javax.script.ScriptException {
        ScriptEngine scriptEngine = new GroovyScriptEngineFactory().getScriptEngine();
        scriptEngine.eval(scriptText);
        Object result = ((Invocable) scriptEngine).invokeFunction(func, objs);
        return (T) result;
    }

    private static Map<String, Script> scriptCache = new ConcurrentHashMap<>();
    GroovyShell groovyShell = new GroovyShell();

    // 方式三，使用GroovyShell调用(推荐)
    public <T> T invoke03(String scriptText, String func, Object... objs) {

        String cacheKey = getMd5(scriptText);
        Script script = scriptCache.computeIfAbsent(cacheKey, k -> {
            Script newScript = groovyShell.parse(scriptText);
            return newScript;
        });

        Object result = InvokerHelper.invokeMethod(script, func, objs);
        return (T) result;

    }

    public static void invoke4(String args[]) throws Throwable {
        GroovyShell shell = new GroovyShell();
        Script scrpt = shell.parse(new File("C:\\dev\\groovy-embed\\src\\groovy\\ com\\vanward\\groovy\\Songs.groovy"));
        Binding binding = new Binding();
        Object[] path = {"C:\\music\\temp\\mp3s"};
        binding.setVariable("args", path);
        scrpt.setBinding(binding);

        scrpt.run();
    }

    public static void invoke5(String args[]) throws Throwable {

        String[] paths = {"C:\\dev\\groovy-embed\\src\\groovy\\ com\\vanward\\groovy"};
        GroovyScriptEngine gse = new GroovyScriptEngine(paths);
        Binding binding = new Binding();
        Object[] path = {"C:\\music\\temp\\mp3s"};
        binding.setVariable("args", path);

        gse.run("Songs.groovy", binding);
        gse.run("BusinessObjects.groovy", binding);
    }

    private String getMd5(String scriptText) {
        return null;
    }

}
