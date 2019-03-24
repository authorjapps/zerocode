package org.jsmart.zerocode.core.utils;

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
}