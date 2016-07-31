package org.jsmart.zerocode.core.httpclient;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.spi.LinkHeader;
import org.jboss.resteasy.util.GenericType;

import javax.ws.rs.core.MultivaluedMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

public class CustomRuntimeTestHttpClient implements BasicHttpClient {

    @Override
    public ClientResponse execute(String httpUrl, String methodName, Map<String, Object> headers, Map<String, Object> queryParams, Object body) throws Exception {

        ClientResponse clientResponse = new ClientResponse() {

            @Override
            public Status getResponseStatus() {
                return Status.fromStatusCode(200);
            }

            @Override
            public Object getEntity(Class aClass) {
                return "{\n" +
                        "  \"result\" : \"via custom http client\" \n" +
                        "}";
            }

            @Override
            public MultivaluedMap<String, String> getHeaders() {
                return null;
            }

            @Override
            public Object getEntity() {
                return "";
            }

            @Override
            public Object getEntity(Class aClass, Type type) {
                return "";
            }

            @Override
            public Object getEntity(Class aClass, Type type, Annotation[] annotations) {
                return null;
            }

            @Override
            public Object getEntity(GenericType genericType) {
                return "";
            }

            @Override
            public Object getEntity(GenericType genericType, Annotation[] annotations) {
                return null;
            }

            @Override
            public LinkHeader getLinkHeader() {
                return null;
            }

            @Override
            public Link getLocation() {
                return null;
            }

            @Override
            public Link getHeaderAsLink(String s) {
                return null;
            }

            @Override
            public void resetStream() {

            }

            @Override
            public void releaseConnection() {

            }

            @Override
            public Map<String, Object> getAttributes() {
                return null;
            }

            @Override
            public int getStatus() {
                return 0;
            }

            @Override
            public MultivaluedMap<String, Object> getMetadata() {
                return null;
            }
        };

        return clientResponse;
    }


}