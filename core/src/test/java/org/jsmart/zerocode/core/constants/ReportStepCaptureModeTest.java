package org.jsmart.zerocode.core.constants;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReportStepCaptureModeTest {

    @Test
    public void testFromPropertyWithNullWillGetFailedOnly() {
        assertThat(ReportStepCaptureMode.fromProperty(null), is(ReportStepCaptureMode.FAILED_ONLY));
    }

    @Test
    public void testFromPropertyWithBlankWillGetFailedOnly() {
        assertThat(ReportStepCaptureMode.fromProperty("  "), is(ReportStepCaptureMode.FAILED_ONLY));
    }

    @Test
    public void testFromPropertyWithAllWillGetAll() {
        assertThat(ReportStepCaptureMode.fromProperty("ALL"), is(ReportStepCaptureMode.ALL));
    }

    @Test
    public void testFromPropertyWithLowerCaseAndSpacesWillGetAll() {
        assertThat(ReportStepCaptureMode.fromProperty(" all "), is(ReportStepCaptureMode.ALL));
    }

    @Test
    public void testFromPropertyWithFailedOnlyWillGetFailedOnly() {
        assertThat(ReportStepCaptureMode.fromProperty("FAILED_ONLY"), is(ReportStepCaptureMode.FAILED_ONLY));
    }

    @Test
    public void testFromPropertyWithUnknownValueWillGetFailedOnly() {
        assertThat(ReportStepCaptureMode.fromProperty("EVERYTHING"), is(ReportStepCaptureMode.FAILED_ONLY));
    }

    @Test
    public void testShouldCaptureForPassedStepInFailedOnlyModeWillGetFalse() {
        assertFalse(ReportStepCaptureMode.FAILED_ONLY.shouldCapture(true));
    }

    @Test
    public void testShouldCaptureForFailedStepInFailedOnlyModeWillGetTrue() {
        assertTrue(ReportStepCaptureMode.FAILED_ONLY.shouldCapture(false));
    }

    @Test
    public void testShouldCaptureForPassedStepInAllModeWillGetTrue() {
        assertTrue(ReportStepCaptureMode.ALL.shouldCapture(true));
    }

    @Test
    public void testShouldCaptureForFailedStepInAllModeWillGetTrue() {
        assertTrue(ReportStepCaptureMode.ALL.shouldCapture(false));
    }
}
