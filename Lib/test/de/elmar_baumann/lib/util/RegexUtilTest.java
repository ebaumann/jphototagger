/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the class {@link de.elmar_baumann.lib.util.RegexUtil}.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public class RegexUtilTest {

    public RegexUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getMatches method, of class RegexUtil.
     */
    @Test
    public void testGetMatches() {
        System.out.println("getMatches");

        Collection<String> strings = new ArrayList<String>();

        final String s1 = "Bill";
        final String s2 = "Heute ist leider Montag";
        final String s3 = "99Luftballons";
        final String s4 = "Eiskreme";
        final String s5 = ".*pattern$";
        final String s6 = "Montag ist der Tag nach Sonntag";

        strings.add(s1);
        strings.add(s2);
        strings.add(s3);
        strings.add(s4);
        strings.add(s5);
        strings.add(s6);

        String pattern = ".*Montag$";
        List<String> expResult = Arrays.asList(s2);
        List<String> result = RegexUtil.getMatches(strings, pattern);
        assertEquals(expResult, result);

        pattern = ".*Montag.*";
        expResult = Arrays.asList(s2, s6);
        result = RegexUtil.getMatches(strings, pattern);
        assertEquals(expResult, result);

        pattern = java.util.regex.Pattern.quote(s5);
        expResult = Arrays.asList(s5);
        result = RegexUtil.getMatches(strings, pattern);
        assertEquals(expResult, result);

        pattern = "[0-9]+.*";
        expResult = Arrays.asList(s3);
        result = RegexUtil.getMatches(strings, pattern);
        assertEquals(expResult, result);

        pattern = ".*nüscht.*";
        expResult = new ArrayList<String>();
        result = RegexUtil.getMatches(strings, pattern);
        assertEquals(expResult, result);

        pattern = "^B.*ll$";
        expResult = Arrays.asList(s1);
        result = RegexUtil.getMatches(strings, pattern);
        assertEquals(expResult, result);
    }

    /**
     * Test of containsMatch method, of class RegexUtil.
     */
    @Test
    public void testContainsMatch() {
        System.out.println("containsMatch");

        Collection<String> patterns = new ArrayList<String>();

        patterns.add(".*Montag$");
        patterns.add(".*Montag.*");
        patterns.add(java.util.regex.Pattern.quote(".*pattern$"));
        patterns.add("[0-9]+.*");
        patterns.add(".*nüscht.*");
        patterns.add("^B.*ll$");

        final String s1 = "Bill";
        final String s2 = "Heute ist leider Montag";
        final String s3 = "99Luftballons";
        final String s4 = "Eiskreme";
        final String s5 = ".*pattern$";
        final String s6 = "Montag ist der Tag nach Sonntag";

        assertTrue(RegexUtil.containsMatch(patterns, s1));
        assertTrue(RegexUtil.containsMatch(patterns, s2));
        assertTrue(RegexUtil.containsMatch(patterns, s3));
        assertFalse(RegexUtil.containsMatch(patterns, s4));
        assertTrue(RegexUtil.containsMatch(patterns, s5));
        assertTrue(RegexUtil.containsMatch(patterns, s6));
    }


    /**
     * Test of matches method, of class ArrayUtil.
     */
    @Test
    public void testContainsMatch_movedFromArrayUtils() {
        System.out.println("matches");
        List<String> patterns = new ArrayList<String>();
        patterns.add(".*ille$");
        String string = "Brille";
        boolean expResult = true;
        boolean result = RegexUtil.containsMatch(patterns, string);
        assertEquals(expResult, result);

        patterns = new ArrayList<String>();
        patterns.add("xyz");
        patterns.add("^[A-Z]+");
        string = "ABC";
        expResult = true;
        result = RegexUtil.containsMatch(patterns, string);
        assertEquals(expResult, result);

        patterns = new ArrayList<String>();
        patterns.add("xyz");
        patterns.add("^[A-Z]+");
        string = "AbC";
        expResult = false;
        result = RegexUtil.containsMatch(patterns, string);
        assertEquals(expResult, result);

        try {
            RegexUtil.containsMatch(null, "");
            fail("NullpointerException was not thrown");
        } catch (NullPointerException ex) {
            // ok
        }

        try {
            RegexUtil.containsMatch(new ArrayList<String>(), null);
            fail("NullpointerException was not thrown");
        } catch (NullPointerException ex) {
            // ok
        }
    }
}