package org.jsmart.zerocode.core.engine.executor.javaapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class JavaCustomExecutorTest {

    @Test
    public void testSplit() {
        String qualifiedMethod = "org.jsmart.zerocode.core.engine.executor.javaapi.CustomUtils#strLen:4";
        String[] parts = qualifiedMethod.split("#");
        String className = parts[0];
        assertThat(className, is("org.jsmart.zerocode.core.engine.executor.javaapi.CustomUtils"));

        String methodNameWithVal = parts[1];
        String[] methodParts = methodNameWithVal.split(":");
        String methodName = methodParts[0];
        assertThat(methodName, is("strLen"));

        String argsWithComma = methodParts[1];
        String[] argsParts = argsWithComma.split(",");
        assertThat(argsParts.length, is(1));
    }

    @Test
    public void testSplitArgs() {
        String qualifiedMethod = "org.jsmart.zerocode.core.engine.executor.javaapi.CustomUtils#strLen:4,5";
        String[] parts = qualifiedMethod.split("#");
        String className = parts[0];
        assertThat(className, is("org.jsmart.zerocode.core.engine.executor.javaapi.CustomUtils"));

        String methodNameWithVal = parts[1];
        String[] methodParts = methodNameWithVal.split(":");
        String methodName = methodParts[0];
        assertThat(methodName, is("strLen"));

        String argsWithComma = methodParts[1];
        String[] argsParts = argsWithComma.split(",");
        assertThat(argsParts.length, is(2));
    }

    @Test
    public void testSplitArgsString() {
        String qualifiedMethod = "org.jsmart.zerocode.core.engine.executor.javaapi.CustomUtils#strLen:Foo,Bar";
        String[] parts = qualifiedMethod.split("#");
        String className = parts[0];
        assertThat(className, is("org.jsmart.zerocode.core.engine.executor.javaapi.CustomUtils"));

        String methodNameWithVal = parts[1];
        String[] methodParts = methodNameWithVal.split(":");
        String methodName = methodParts[0];
        assertThat(methodName, is("strLen"));

        String argsWithComma = methodParts[1];
        String[] argsParts = argsWithComma.split(",");
        assertThat(argsParts.length, is(2));
    }

    @Test
    public void testSplitArgsAsJson() throws JsonProcessingException {
        String qualifiedMethod = "org.jsmart.zerocode.core.engine.executor.javaapi.CustomUtils#strLen:{\"expectLen\":5, \"expFlag\":true}";
        String[] parts = qualifiedMethod.split("#");
        String className = parts[0];
        assertThat(className, is("org.jsmart.zerocode.core.engine.executor.javaapi.CustomUtils"));

        String methodNameWithVal = parts[1];
        String[] methodParts = methodNameWithVal.split(":", 2);
        String methodName = methodParts[0];
        String expectedRawJson = methodParts[1];
        assertThat(methodName, is("strLen"));
        assertThat(expectedRawJson, is("{\"expectLen\":5, \"expFlag\":true}"));

        ObjectMapper mapper = new ObjectMapperProvider().get();
        HashMap<String, Object> valueMap = mapper.readValue(expectedRawJson, HashMap.class);
        assertThat(valueMap.get("expectLen"), is(5));
        assertThat(valueMap.get("expFlag"), is(true));
    }

    @Test
    public void testExecuteByReflection() throws JsonProcessingException {
        String qualifiedMethod = "org.jsmart.zerocode.integrationtests.customassert.MyCustomComparator#assertLength:{\"expectLen\":3}";
        Boolean passed = JavaCustomExecutor.executeMethod(qualifiedMethod, "Foo");
        assertThat(passed, is(true));
    }

}