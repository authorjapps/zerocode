package org.jsmart.zerocode.core.httpclient.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;
import static org.jsmart.zerocode.core.httpclient.BasicHttpClient.BOUNDARY_FIELD;
import static org.jsmart.zerocode.core.httpclient.BasicHttpClient.FILES_FIELD;

public class FileUploadUtils {

    public static RequestBuilder createUploadRequestBuilder(String httpUrl, String methodName, MultipartEntityBuilder multipartEntityBuilder) {

        RequestBuilder uploadRequestBuilder = RequestBuilder
                .create(methodName)
                .setUri(httpUrl);

        HttpEntity reqEntity = multipartEntityBuilder.build();

        uploadRequestBuilder.setEntity(reqEntity);

        return uploadRequestBuilder;
    }


    public static void buildMultiPartBoundary(Map<String, Object> fileFieldNameValueMap, MultipartEntityBuilder multipartEntityBuilder) {
        String boundary = (String) fileFieldNameValueMap.get(BOUNDARY_FIELD);
        multipartEntityBuilder.setBoundary(boundary != null ? boundary : currentTimeMillis() + now().toString());
    }

    public static void buildAllFilesToUpload(List<String> fileFiledsList, MultipartEntityBuilder multipartEntityBuilder) {
        fileFiledsList.forEach(fileField -> {
            String[] fieldNameValue = fileField.split(":");
            String fieldName = fieldNameValue[0];
            String fileNameWithPath = fieldNameValue[1].trim();

            FileBody fileBody = new FileBody(new File(getAbsPath(fileNameWithPath)));
            multipartEntityBuilder.addPart(fieldName, fileBody);
        });
    }

    public static void buildOtherRequestParams(Map<String, Object> fileFieldNameValueMap, MultipartEntityBuilder multipartEntityBuilder) {
        for (Map.Entry<String, Object> entry : fileFieldNameValueMap.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            if (entry.getKey().equals(FILES_FIELD) || entry.getKey().equals(BOUNDARY_FIELD)) {
                continue;
            }
            multipartEntityBuilder.addPart(entry.getKey(), new StringBody((String) entry.getValue(), TEXT_PLAIN));
        }
    }

    public static Map<String, Object> getFileFieldNameValue(String reqBodyAsString) throws IOException {
        return new ObjectMapperProvider().get().readValue(reqBodyAsString, HashMap.class);
    }

    public static String getAbsPath(String filePath) {

        if (new File(filePath).exists()) {
            return filePath;
        }

        ClassLoader classLoader = FileUploadUtils.class.getClassLoader();
        URL resource = classLoader.getResource(filePath);
        if (resource == null) {
            throw new RuntimeException("Could not get details of file or folder - `" + filePath + "`, does this exist?");
        }
        return resource.getPath();
    }

}
