package org.jsmart.zerocode.core.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ApiTypeUtilsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    ApiTypeUtils apiTypeUtils;

    @Test
    public void testJavaApiProtoMappings_service1() {
        String mappings = "foo://v1/s1|bar.service1";
        apiTypeUtils = new ApiTypeUtils(mappings);
        String qualifiedClass = apiTypeUtils.getQualifiedJavaApi("foo://v1/s1");
        assertThat(qualifiedClass, is("bar.service1"));
    }

    @Test
    public void testJavaApiProtoMappings_notFound() {
        String mappings = "xyz|mno";
        apiTypeUtils = new ApiTypeUtils(mappings);
        expectedException.expectMessage("url 'foo://v1/s1' Not found");
        String qualifiedClass = apiTypeUtils.getQualifiedJavaApi("foo://v1/s1");
    }

    @Test
    public void testJavaApiProtoMappings_emptyMappings() {
        String mappings = "";
        apiTypeUtils = new ApiTypeUtils(mappings);
        expectedException.expectMessage("Protocol mapping was null or empty.");
        String qualifiedClass = apiTypeUtils.getQualifiedJavaApi("foo://v1/s1");
    }

    @Test
    public void testJavaApiProtoMappings_nullMappings() {
        String mappings = "";
        apiTypeUtils = new ApiTypeUtils(mappings);
        expectedException.expectMessage("Protocol mapping was null or empty.");
        String qualifiedClass = apiTypeUtils.getQualifiedJavaApi("foo://v1/s1");
    }

}