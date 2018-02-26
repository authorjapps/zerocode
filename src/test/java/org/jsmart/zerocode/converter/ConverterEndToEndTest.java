package org.jsmart.zerocode.converter;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

//@HostProperties(host="http://localhost", port=9998, context = "") //Not needed for java execution
@RunWith(ZeroCodeUnitRunner.class)
public class ConverterEndToEndTest {
    
    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json
     */

    @Test
    @JsonTestCase("17_xml_to_json_converter/30_xml_to_json_single_quote_happy.json")
    public void testConvertXmlWith_singleQuoteuote() throws Exception {

    }

    @Test
    @JsonTestCase("17_xml_to_json_converter/10_xml_to_json_format_happy.json")
    public void testConverterAt_runTime() throws Exception {
    
    }

    @Test
    @JsonTestCase("17_xml_to_json_converter/20_json_to_json.json")
    public void testJsonToJson_runTime() throws Exception {

    }

}



