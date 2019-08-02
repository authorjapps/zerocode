package org.jsmart.zerocode.converter;

import java.util.HashMap;
import java.util.Map;
import org.jsmart.zerocode.core.utils.SmartUtils;

public class SoapMocker {

    public Object soapResponseXml(String nothing){

        try {
            final String rawBody = SmartUtils.readJsonAsString("soap_response/mock_soap_response.xml");
            Map<String, String> singleKeyValueMap = new HashMap<>();
            singleKeyValueMap.put("rawBody", rawBody);

            return singleKeyValueMap;

        } catch (RuntimeException  e) {
            throw new RuntimeException("something wrong happened here" + e);
        }
    }
}
