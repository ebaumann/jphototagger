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
        System.out.println("wrapWords"); // NOI18N
        String text = "";
        int maxCharsPerLine = 1;
        List<String> expResult = Collections.emptyList();
        List<String> result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "a";
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a"); // NOI18N
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "aa";
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a", "a"); // NOI18N
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "a a"; // NOI18N
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a", "a"); // NOI18N
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "aa a"; // NOI18N
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a", "a", "a"); // NOI18N
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "Dies ist ein längerer Text mit 43 Zeichen."; // NOI18N
        maxCharsPerLine = 25;
        expResult = Arrays.asList("Dies ist ein längerer", "Text mit 43 Zeichen."); // NOI18N
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);

        text = "DiesisteinlängererTextmit36Zeichen."; // NOI18N
        maxCharsPerLine = 25;
        expResult = Arrays.asList("DiesisteinlängererTextmit", "36Zeichen."); // NOI18N
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
    }
}