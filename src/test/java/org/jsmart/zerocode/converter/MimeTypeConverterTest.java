package org.jsmart.zerocode.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class MimeTypeConverterTest {

    private ObjectMapper mapper = new ObjectMapperProvider().get();

    private Converter xmlToJsonConverter;

    @Before
    public void setUpStuffs() throws Exception {
        xmlToJsonConverter = new MimeTypeConverter(mapper);
    }

    @Test
    public void testXmlToJson_happy() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<addresses>\n" +
                "  <address>\n" +
                "    <postCode>4005</postCode>\n" +
                "    <message>The field 'quantity' is invalid.</message>\n" +
                "  </address>\n" +
                "  <address>\n" +
                "    <postCode>500</postCode>\n" +
                "    <message>new message</message>\n" +
                "  </address>\n" +
                "</addresses>";

        JsonNode jsonNode = (JsonNode)xmlToJsonConverter.xmlToJson(xml);

        String prettyJson = mapper.writeValueAsString(jsonNode);

        assertThat(prettyJson, containsString("\"postCode\":500"));
        assertThat(prettyJson, containsString("\"address\":["));
        assertThat(prettyJson, containsString("{\"addresses\":{"));

    }
}