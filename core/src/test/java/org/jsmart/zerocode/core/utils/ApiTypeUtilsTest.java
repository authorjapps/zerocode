package org.jsmart.zerocode.core.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.jsmart.zerocode.core.utils.ApiType.KAFKA_CALL;
import static org.jsmart.zerocode.core.utils.ApiType.REST_CALL;
import static org.jsmart.zerocode.core.utils.ApiType.S3_CALL;
import static org.jsmart.zerocode.core.utils.ApiTypeUtils.apiType;

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

    @Test
    public void testApiType_s3BucketUrl_returnsS3Call() {
        assertThat(apiType("s3-bucket:my-test-bucket", "upload"), is(S3_CALL));
    }

    @Test
    public void testApiType_s3BucketUrl_download_returnsS3Call() {
        assertThat(apiType("s3-bucket:my-test-bucket", "download"), is(S3_CALL));
    }

    @Test
    public void testApiType_s3BucketUrl_list_returnsS3Call() {
        assertThat(apiType("s3-bucket:my-test-bucket", "list"), is(S3_CALL));
    }

    @Test
    public void testApiType_httpUrl_notS3Call() {
        assertThat(apiType("http://localhost:8080/api/users", "GET"), is(REST_CALL));
    }

    @Test
    public void testApiType_kafkaUrl_notS3Call() {
        assertThat(apiType("kafka-topic:my-topic", "produce"), is(KAFKA_CALL));
    }

}