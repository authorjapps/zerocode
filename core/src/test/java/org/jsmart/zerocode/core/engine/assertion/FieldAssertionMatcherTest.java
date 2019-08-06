package org.jsmart.zerocode.core.engine.assertion;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FieldAssertionMatcherTest {

    FieldAssertionMatcher fieldAssertionMatcher;

    @Before
    public void setUp() throws Exception {

        fieldAssertionMatcher = new FieldAssertionMatcher(null);
    }

    @Test
    public void willCreateMatchingMessage() throws Exception {

        assertThat(fieldAssertionMatcher.matches(), is(true));
        assertThat(fieldAssertionMatcher.toString(), is("Actual field value matched the expected field value"));
    }

    @Test
    public void willCreateUnMatchingMessage() throws Exception {

        fieldAssertionMatcher = new FieldAssertionMatcher("$.a.path", "VJ", "VJ2");

        assertThat(fieldAssertionMatcher.matches(), is(false));
        assertThat(fieldAssertionMatcher.toString(), is("Assertion jsonPath '$.a.path' with actual value 'VJ2' did not match the expected value 'VJ'"));
    }

}