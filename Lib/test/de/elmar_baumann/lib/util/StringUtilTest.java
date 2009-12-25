/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.elmar_baumann.lib.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public class StringUtilTest {

    public StringUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of wrapWords method, of class StringUtil.
     */
    @Test
    public void testWrapWords() {
        System.out.println("wrapWords");
        String text = "";
        int maxCharsPerLine = 1;
        List<String> expResult = Collections.emptyList();
        List<String> result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "a";
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "aa";
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a", "a");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "a a";
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a", "a");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "aa a";
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a", "a", "a");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "Dies ist ein längerer Text mit 43 Zeichen.";
        maxCharsPerLine = 25;
        expResult = Arrays.asList("Dies ist ein längerer", "Text mit 43 Zeichen.");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "DiesisteinlängererTextmit36Zeichen.";
        maxCharsPerLine = 25;
        expResult = Arrays.asList("DiesisteinlängererTextmit", "36Zeichen.");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
    }
}