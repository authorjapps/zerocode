package org.jsmart.zerocode.core.utils;

import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.jsmart.zerocode.core.utils.PropertiesProviderUtils.loadCustomZerocodeProperties;

public class PropertiesProviderUtilsTest {

    @Test
    public void loadCustomZerocodeProperties_returnsProperties_whenFileOnClasspath() {
        Properties props = loadCustomZerocodeProperties();
        assertNotNull(props);
        assertThat(props.getProperty("zerocode.report.html.name"), is("target/my-custom-report.html"));
        assertThat(props.getProperty("zerocode.report.csv.name"), is("my-custom-granular.csv"));
    }

    @Test
    public void loadCustomZerocodeProperties_returnsEmptyProperties_forAbsentKey() {
        Properties props = loadCustomZerocodeProperties();
        assertNotNull(props);
        assertNull(props.getProperty("zerocode.report.nonexistent.key"));
    }

}
