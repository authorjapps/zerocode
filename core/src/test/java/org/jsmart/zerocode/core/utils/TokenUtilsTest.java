package org.jsmart.zerocode.core.utils;

import java.util.Map;
import java.util.stream.IntStream;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.jsmart.zerocode.core.utils.TokenUtils.absolutePathOf;
import static org.jsmart.zerocode.core.utils.TokenUtils.getMasksReplaced;
import static org.jsmart.zerocode.core.utils.TokenUtils.getMasksRemoved;
import static org.jsmart.zerocode.core.utils.TokenUtils.resolveKnownTokens;
import static org.junit.Assert.*;

public class TokenUtilsTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    static String globalRandomNumber = "";

    @Test
    public void testGlobalRandomNumberSameness_1(){
        String result = resolveKnownTokens("${GLOBAL.RANDOM.NUMBER},${GLOBAL.RANDOM.NUMBER}");
        String[] split = result.split(",");
        assertTrue(split[0].equals(split[1]));
        globalRandomNumber = split[0];
    }

    @Test
    public void testGlobalRandomNumberSameness_2(){
        String result = resolveKnownTokens("${GLOBAL.RANDOM.NUMBER},${GLOBAL.RANDOM.NUMBER}");
        String[] split = result.split(",");
        assertTrue(split[0].equals(split[1]));
        assertTrue(split[0].equals(globalRandomNumber));
    }

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
    public void testFixedLengthRandomNumberUniqueness() {
        String result = resolveKnownTokens("${RANDOM.NUMBER:12},${RANDOM.NUMBER:12}");
        String[] split = result.split(",");
        assertFalse(split[0].equals(split[1]));
    }

    @Test
    public void testRandomNumberUniqueness(){
        String result = resolveKnownTokens("${RANDOM.NUMBER},${RANDOM.NUMBER}");
        String[] split = result.split(",");
        assertFalse(split[0].equals(split[1]));
    }

    @Test
    public void testFixedRandomNumberSameness(){
        String result = resolveKnownTokens("${RANDOM.NUMBER.FIXED},${RANDOM.NUMBER.FIXED}");
        String[] split = result.split(",");
        assertTrue(split[0].equals(split[1]));
    }

    @Test
    public void testUUIDUniqueness(){
        String result = resolveKnownTokens("${RANDOM.UUID},${RANDOM.UUID}");
        String[] split = result.split(",");
        assertFalse(split[0].equals(split[1]));
    }

    @Test
    public void testUUIDFixedSameness(){
        String result = resolveKnownTokens("${RANDOM.UUID.FIXED},${RANDOM.UUID.FIXED}");
        String[] split = result.split(",");
        assertTrue(split[0].equals(split[1]));
    }



    @Test
    public  void testEnvPropertyReplace(){
        Map<String, String> env = System.getenv();
        env.forEach((k,v) ->{
            String text = "${SYSTEM.ENV:"+ k +"}";
            String resolvedValue = resolveKnownTokens(text);
            assertTrue(resolvedValue.equals(v));
        });
    }

    @Test
    public void testAbsolutePathOf_wrongPath() {
        exceptionRule.expectMessage("Wrong file name or path found");
        exceptionRule.expectMessage("Please fix it and rerun.");
        absolutePathOf("WRONG_PATH/jks_files/dummy_key_store.jks");
    }

    @Test
    public void testAbsolutePathOf() {
        assertThat(absolutePathOf("unit_test_files/jks_files/dummy_key_store.jks"),
                containsString("zerocode/core/target/test-classes/unit_test_files/jks_files/dummy_key_store.jks"));
    }


    @Test
    public void testGetMaskedTokensReplaced_multipleOccurrences(){
        assertEquals("This is a ***masked*** message with ***masked*** tokens.", getMasksReplaced("This is a ${MASKED:secret} message with ${MASKED:masked} tokens."));
    }

    @Test
    public void testGetMaskedTokensReplaced_noOccurrences(){
        assertEquals("This string has no masked tokens.", getMasksReplaced("This string has no masked tokens."));
    }

    @Test
    public void testGetMaskedTokensReplaced_emptyString(){
        assertEquals("", getMasksReplaced(""));
    }

    @Test
    public void testGetMaskedTokensReplaced_specialCharacters(){
        assertEquals("***masked*** and ***masked***", getMasksReplaced("${MASKED:abc@123} and ${MASKED:!@#$%^}"));
    }

    @Test
    public void testGetMaskedTokensRemoved_multipleOccurrences(){
        assertEquals("This is a secret message with masked tokens.", getMasksRemoved("This is a ${MASKED:secret} message with ${MASKED:masked} tokens."));
    }

    @Test
    public void testGetMaskedTokensRemoved_noOccurrences(){
        assertEquals("This string has no masked tokens.", getMasksRemoved("This string has no masked tokens."));
    }

    @Test
    public void testGetMaskedTokensRemoved_emptyString(){
        assertEquals("", getMasksRemoved(""));
    }

    @Test
    public void testGetMaskedTokensRemoved_specialCharacters(){
        assertEquals("abc@123 and !@#$%^", getMasksRemoved("${MASKED:abc@123} and ${MASKED:!@#$%^}"));
    }



}