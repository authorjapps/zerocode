package org.jsmart.zerocode.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.json.XML;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.slf4j.LoggerFactory.getLogger;

public class MimeTypeConverter implements Converter {

    private static final org.slf4j.Logger LOGGER = getLogger(MimeTypeConverter.class);

    private final ObjectMapper mapper;

    @Inject
    public MimeTypeConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Object xmlToJson(String xmlContent) {
        final String xmlFormated = prettyFormat(xmlContent);

        LOGGER.info("\n------------------------- XML -----------------------------\n"
                + xmlFormated +
                "\n------------------------- * -----------------------------\n");

        String jsonNotPretty = XML.toJSONObject(xmlContent).toString();

        try {
            return mapper.readTree(jsonNotPretty);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("XmlToJson conversion problem-  " + e.getMessage());
        }

    }

    /**
     * Converts input JSON string (usually escaped e.g. "{\"a\": \"b\", \"active\": true}" ) to JSON block
     * See also- method jsonBlockToJson for unescaped json to json block.
     *
     * @param jsonString
     * @return
     * @throws IOException
     */
    @Override
    public Object jsonToJson(String jsonString) throws IOException {
        return mapper.readValue(jsonString, JsonNode.class);
    }

    /**
     * Converts JSON Block({"a": "b", "active": true}) to JSON block
     * See also- jsonNodeToJson which is identical to jsonBlockToJson.
     *
     * @param jsonNode
     * @return
     * @throws IOException
     */
    public Object jsonBlockToJson(JsonNode jsonNode) throws IOException {
        return jsonNode;
    }

    public static String prettyFormat(String input) {
        return prettyFormat(input, 2);
    }

    public static String prettyFormat(String originalXml, int indentType) {
        try {
            Source xmlInput = new StreamSource(new StringReader(originalXml));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            // This statement works with JDK 6
            transformerFactory.setAttribute("indent-number", indentType);

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Throwable e) {
            e.printStackTrace();
            // You'll come here if you are using JDK 1.5
            // you are getting an the following exeption
            // java.lang.IllegalArgumentException: Not supported: indent-number
            // Use this code (Set the output property in transformer.
            try {
                Source xmlInput = new StreamSource(new StringReader(originalXml));
                StringWriter stringWriter = new StringWriter();
                StreamResult xmlOutput = new StreamResult(stringWriter);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indentType));
                transformer.transform(xmlInput, xmlOutput);
                return xmlOutput.getWriter().toString();
            } catch (Throwable t) {
                e.printStackTrace();
                return originalXml;
            }
        }
    }


}
