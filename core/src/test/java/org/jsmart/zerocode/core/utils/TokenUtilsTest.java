package org.jsmart.zerocode.core.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.jsmart.zerocode.core.utils.TokenUtils.resolveKnownTokens;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TokenUtilsTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

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

        String exampleInputString = "zerocode-tokentest: ${SYSTEM.PROPERTY:java.vendor}"; // java.vendor = Oracle
        // Corporation
        String resolvedString = resolveKnownTokens(exampleInputString);
        String resolvedToken = resolvedString.substring("zerocode-tokentest: ".length());

        assertThat(resolvedToken.equals("Oracle Corporation"), is(true));

    }

    @Test
    public void testResolveSystemProperty_PROPERTY_FOUND_fileEncoing() {

        String exampleInputString = "zerocode-tokentest: ${SYSTEM.PROPERTY:file.encoding}"; // java.vendor = Oracle
        // Corporation
        String resolvedString = resolveKnownTokens(exampleInputString);
        String resolvedToken = resolvedString.substring("zerocode-tokentest: ".length());

        assertThat(resolvedToken, is("UTF-8"));
    }

    @Test
    public void testResolveSystemProperty_PROPERTY_NOT_FOUND() {

        String exampleInputString = "zerocode-tokentest: ${SYSTEM.PROPERTY:dummy_property_one}"; // Property does not
        // exist
        String resolvedString = resolveKnownTokens(exampleInputString);
        String unResolvedToken = resolvedString.substring("zerocode-tokentest: ".length());
        // If the property is NOT FOUND then the token is not resolved and remains as a
        // normal string
        assertThat(unResolvedToken.equals("${SYSTEM.PROPERTY:dummy_property_one}"), is(true));

    }

    @Test
    public void testFixedRandomGenerator_success() {
        IntStream.rangeClosed(1, 19).forEach(i -> {
            assertTrue(FixedLengthRandomGenerator.getGenerator(i).generateRandomNumber().length() == i);
        });
    }

    @Test
    public void testFixedRandomGenerator_failure_min() {
        exceptionRule.expect(RuntimeException.class);
        FixedLengthRandomGenerator.getGenerator(0).generateRandomNumber();
    }

    @Test
    public void testFixedRandomGenerator_failure_max() {
        exceptionRule.expect(RuntimeException.class);
        FixedLengthRandomGenerator.getGenerator(20).generateRandomNumber();
    }

    @Test
    public void testFixedRandomTokenReplace() {
        String clientId = "zerocode-clientid_${RANDOM.NUMBER:10}";
        String uniqueClientId = resolveKnownTokens(clientId);
        String uniqueId = uniqueClientId.substring("zerocode-clientid_".length());
        assertTrue(uniqueId.length() == 10);
    }

    @Test
    public void testFixedRandomUniqueness() {
        String result = resolveKnownTokens("${RANDOM.NUMBER:12},${RANDOM.NUMBER:12}");
        String[] split = result.split(",");
        assertTrue(split[0] != split[1]);
    }

}