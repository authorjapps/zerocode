package org.jsmart.zerocode.core.utils;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.jsmart.zerocode.core.httpclient.utils.FileUploadUtils;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class FileUploadUtilsTest {

    @Test
    public void buildOtherRequestParamsTest1() {
        Map<String, Object> fileFieldNameValueMapStub = new LinkedHashMap<>();
        fileFieldNameValueMapStub.put("name", "name");
        fileFieldNameValueMapStub.put("fileName", "test.wav");
        fileFieldNameValueMapStub.put("location", "location");
        MultipartEntityBuilder multipartEntityBuilderStub = MultipartEntityBuilder.create();
        try {
            FileUploadUtils.buildOtherRequestParams(fileFieldNameValueMapStub, multipartEntityBuilderStub);
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }
}
