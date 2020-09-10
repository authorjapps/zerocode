package org.jsmart.zerocode.core.kafka.helper;


import org.jsmart.zerocode.core.kafka.KafkaConstants;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.jsmart.zerocode.core.kafka.helper.KafkaProducerHelper.protoClassType;

public class KafkaProducerHelperTest  {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_protoClassType() {
        String requestJson = "{\n" +
                "\"recordType\": \"PROTO\"," +
                "\"protoClassType\": \"org.jsmart.zerocode.proto.Foo$Bar\"" +
                "}";
        String classType = protoClassType(requestJson, KafkaConstants.PROTO_BUF_MESSAGE_CLASS_TYPE);
        assertThat(classType, is("org.jsmart.zerocode.proto.Foo$Bar"));
    }

    @Test
    public void test_protoClassTypeMissing() {

        String requestJson = "{\n" +
                "\"recordType\": \"PROTO\"" +
                //"\"protoClassType\": \"org.jsmart.zerocode.proto.Foo$Bar\"" +
                "}";
        expectedException.expectMessage("Missing 'protoClassType' for 'recordType:PROTO'. Please provide 'protoClassType'");
        protoClassType(requestJson, KafkaConstants.PROTO_BUF_MESSAGE_CLASS_TYPE);
    }
}