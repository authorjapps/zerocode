package org.jsmart.zerocode.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringEscapeUtils.escapeJavaScript;
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
    public void testXmlToJsonWithSingleQuote_willNotFail() throws Exception {

        String xml = "<?xml version='1.0' encoding=\"UTF-8\"?><address>Street 123</address>";
        String escapedOut = escapeJavaScript(xml);
        assertThat(escapedOut, containsString("<?xml version=\\'1.0\\' encoding=\\\"UTF-8\\\"?><address>Street 123<\\/address>"));

        escapedOut = escapeJava(xml);
        assertThat(escapedOut, containsString("<?xml version='1.0' encoding=\\\"UTF-8\\\"?><address>Street 123</address>"));

        System.out.println("escapedOut: " + escapedOut);

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

        System.out.println("---prettyJson:\n" + prettyJson);

        assertThat(prettyJson, containsString("\"postCode\":500"));
        assertThat(prettyJson, containsString("\"address\":["));
        assertThat(prettyJson, containsString("{\"addresses\":{"));
    }

    @Test
    public void testJsonToJson() throws Exception {

        String jsonString = "{\n" +
                "  \"soap:Envelope\" : {\n" +
                "    \"xmlns:xsd\" : \"http://www.w3.org/2001/XMLSchema\",\n" +
                "    \"xmlns:soap\" : \"http://schemas.xmlsoap.org/soap/envelope/\",\n" +
                "    \"xmlns:xsi\" : \"http://www.w3.org/2001/XMLSchema-instance\",\n" +
                "    \"soap:Body\" : {\n" +
                "      \"ConversionRateResponse\" : {\n" +
                "        \"xmlns\" : \"http://www.webserviceX.NET/\",\n" +
                "        \"ConversionRateResult\" : -1\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JsonNode jsonNode = (JsonNode)xmlToJsonConverter.jsonToJson(jsonString);

        System.out.println("--- jsonNode:\n" + jsonNode.toString());

        assertThat(jsonNode.toString(), containsString("{\"soap:Envelope\":{\"xmlns:xsd\":"));

    }

    @Test
    public void testJsonNodeToJson() throws Exception {
        String jsonNodeString = "{\n" +
                "  \"soap:Envelope\" : {\n" +
                "    \"xmlns:xsd\" : \"http://www.w3.org/2001/XMLSchema\",\n" +
                "    \"xmlns:soap\" : \"http://schemas.xmlsoap.org/soap/envelope/\",\n" +
                "    \"xmlns:xsi\" : \"http://www.w3.org/2001/XMLSchema-instance\",\n" +
                "    \"soap:Body\" : {\n" +
                "      \"ConversionRateResponse\" : {\n" +
                "        \"xmlns\" : \"http://www.webserviceX.NET/\",\n" +
                "        \"ConversionRateResult\" : -1\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JsonNode jsonNodeInput = mapper.readTree(jsonNodeString);

        Object jsonNodeOutput = xmlToJsonConverter.jsonNodeToJson(jsonNodeInput);

        System.out.println("---- jsonNodeOutput:\n" + jsonNodeOutput);

        assertThat(jsonNodeOutput.toString(), containsString("{\"soap:Envelope\":{\"xmlns:xsd\":"));


    }

    @Test
    public void testJsonArrayToJson() throws Exception {

        String jsonArrayInput = "[\n" +
                "        {\n" +
                "            \"postCode\": 4005,\n" +
                "            \"message\": \"The field 'quantity' is invalid.\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"postCode\": 500,\n" +
                "            \"message\": \"new message\"\n" +
                "        }\n" +
                "    ]";
        JsonNode jsonNodeInput = mapper.readTree(jsonArrayInput);

        Object jsonNodeOutput = (JsonNode)xmlToJsonConverter.jsonNodeToJson(jsonNodeInput);

        System.out.println("--- jsonNodeOutput:\n" + jsonNodeOutput);

        assertThat(jsonNodeOutput.toString(), containsString("[{\"postCode\":"));
    }

    @Test
    public void testJsonBlockToJson() throws Exception {
        String jsonBlockString = "{\n" +
                "            \"addresses\": {\n" +
                "                \"address\": [\n" +
                "                    {\n" +
                "                        \"postCode\": 4005,\n" +
                "                        \"message\": \"The field 'quantity' is invalid.\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"postCode\": 500,\n" +
                "                        \"message\": \"new message\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        }";
        JsonNode jsonNodeInput = mapper.readTree(jsonBlockString);

        Object jsonNodeOutput = xmlToJsonConverter.jsonBlockToJson(jsonNodeInput);

        System.out.println("--- jsonBlockOutput:\n" + jsonNodeOutput);

        assertThat(jsonNodeOutput.toString(), containsString("{\"addresses\":{\"address\":[{"));

    }

    @Test
    public void testJsonArrayBlockToJson() throws Exception {
        String jsonBlockString = "{\n" +
                "            \"addresses\": {\n" +
                "                \"address\": [\n" +
                "                    {\n" +
                "                        \"postCode\": 4005,\n" +
                "                        \"message\": \"The field 'quantity' is invalid.\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"postCode\": 500,\n" +
                "                        \"message\": \"new message\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        }";

        String jsonArrayString = JsonPath.read(jsonBlockString, "$.addresses.address").toString();
        System.out.println("--- jsonArray: \n" + jsonArrayString);

        JsonNode jsonNodeInput = mapper.readTree(jsonArrayString);

        Object jsonNodeOutput = xmlToJsonConverter.jsonBlockToJson(jsonNodeInput);

        System.out.println("--- jsonArrayBlockOutput:\n" + jsonNodeOutput);

        assertThat(jsonNodeOutput.toString(), containsString("[{\"postCode\":4005"));

    }
}