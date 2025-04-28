package org.jsmart.zerocode.core.di;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;

import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.di.provider.CsvParserProvider;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;


@RunWith(JukitoRunner.class)
public class CsvParserTest {

    public static class JukitoModule extends TestModule {
        @Override
        protected void configureTest() {
            ApplicationMainModule applicationMainModule = new ApplicationMainModule("config_hosts_test.properties");
            install(applicationMainModule);
        }
    }

    @Inject
    CsvParserProvider parser;

    @Test
    public void testCsvparseSpaces() {
        assertThat(parser.parseLine(" abc ,\t de f  , ghi "), is(new String[] { "abc", "de f", "ghi" }));
    }

    @Test
    public void testCsvParseEmptyCell() {
        assertThat(parser.parseLine(",,  , \t ,"), is(new String[] { null, null, null, null, null }));
    }

    @Test
    public void testCsvparseEmptyLine() {
        assertNull(parser.parseLine(""));
        assertThat(parser.parseLine("  "), is(new String[] { null }));
        assertThat(parser.parseLine(" \t "), is(new String[] { null }));
    }

    @Test
    public void testCsvParseQuotesAndOtherChars() {
        assertThat(parser.parseLine("a'c, d\"f, x\\y"), is(new String[] { "a'c", "d\"f", "x\\y" }));
        assertThat(parser.parseLine("euro\u20AC, año, naïf"), is(new String[] { "euro\u20AC", "año", "naïf" }));
    }

    @Test
    public void testCsvParseWindowsEndings() { // assume that lines where already splitted by \n
        assertThat(parser.parseLine("abc, def\r"), is(new String[] { "abc", "def" }));
        assertThat(parser.parseLine("abc, def \r"), is(new String[] { "abc", "def" }));
        // empty lines
        assertThat(parser.parseLine("\r"), is(new String[] { null })); // should be null?, see testCsvparseEmptyLine
        assertThat(parser.parseLine("  \r"), is(new String[] { null }));
        assertThat(parser.parseLine(" \t \r"), is(new String[] { null }));
    }

}