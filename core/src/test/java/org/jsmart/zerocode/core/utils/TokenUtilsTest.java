package org.jsmart.zerocode.core.utils;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.jsmart.zerocode.core.utils.TokenUtils.resolveKnownTokens;
import static org.junit.Assert.assertThat;

public class TokenUtilsTest {

    @Test
    public void testResolve_knownTokens() {
        String clientId = "zerocode-clientid_${RANDOM.NUMBER}";
        String uniqueClientId = resolveKnownTokens(clientId);
        String uniqueId = uniqueClientId.substring("zerocode-clientid_".length());

        assertThat(Long.parseLong(uniqueId) > 1, is(true));

    }

    @Ignore("This might fail for who is e.g. Open Jdk or Zulu JDK")
    @Test
    public void testResolveSystemProperty_PROPERTY_FOUND_javaVendor() {
    	
    	String exampleInputString = "zerocode-tokentest: ${SYSTEM.PROPERTY:java.vendor}"; // java.vendor = Oracle Corporation
    	String resolvedString = resolveKnownTokens(exampleInputString);
    	String resolvedToken = resolvedString.substring("zerocode-tokentest: ".length());
    	
    	assertThat(resolvedToken.equals("Oracle Corporation"), is(true));

    }

    @Test
    public void testResolveSystemProperty_PROPERTY_FOUND_fileEncoing() {

        String exampleInputString = "zerocode-tokentest: ${SYSTEM.PROPERTY:file.encoding}"; // java.vendor = Oracle Corporation
        String resolvedString = resolveKnownTokens(exampleInputString);
        String resolvedToken = resolvedString.substring("zerocode-tokentest: ".length());

        assertThat(resolvedToken, is("UTF-8"));
    }

    @Test
    public void testResolveSystemProperty_PROPERTY_NOT_FOUND() {
    	
    	String exampleInputString = "zerocode-tokentest: ${SYSTEM.PROPERTY:dummy_property_one}"; // Property does not exist
    	String resolvedString = resolveKnownTokens(exampleInputString);
    	String unResolvedToken = resolvedString.substring("zerocode-tokentest: ".length());
    	// If the property is NOT FOUND then the token is not resolved and remains as a normal string
    	assertThat(unResolvedToken.equals("${SYSTEM.PROPERTY:dummy_property_one}"), is(true));
    	
    }
}