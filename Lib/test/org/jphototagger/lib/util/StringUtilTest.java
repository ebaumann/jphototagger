/*
 * @(#)StringUtilTest.java    Created on 
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

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
