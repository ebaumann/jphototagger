package org.jphototagger.lib.util;

import org.jphototagger.lib.util.StringUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Elmar Baumann
 */
public class StringUtilTest {
    public StringUtilTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    /**
     * Test of wrapWords method, of class StringUtil.
     */
    @Test
    public void testWrapWords() {
        System.out.println("wrapWords");

        String       text            = "";
        int          maxCharsPerLine = 1;
        List<String> expResult       = Collections.emptyList();
        List<String> result          = StringUtil.wrapWords(text,
                                           maxCharsPerLine);

        assertEquals(expResult, result);
        text            = "a";
        maxCharsPerLine = 1;
        expResult       = Arrays.asList("a");
        result          = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
        text            = "aa";
        maxCharsPerLine = 1;
        expResult       = Arrays.asList("a", "a");
        result          = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
        text            = "a a";
        maxCharsPerLine = 1;
        expResult       = Arrays.asList("a", "a");
        result          = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
        text            = "aa a";
        maxCharsPerLine = 1;
        expResult       = Arrays.asList("a", "a", "a");
        result          = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
        text            = "Dies ist ein längerer Text mit 43 Zeichen.";
        maxCharsPerLine = 25;
        expResult       = Arrays.asList("Dies ist ein längerer",
                                        "Text mit 43 Zeichen.");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
        text            = "DiesisteinlängererTextmit36Zeichen.";
        maxCharsPerLine = 25;
        expResult       = Arrays.asList("DiesisteinlängererTextmit",
                                        "36Zeichen.");
        result          = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
    }
}
