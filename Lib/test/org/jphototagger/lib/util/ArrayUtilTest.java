/*
 * @(#)ArrayUtilTest.java    Created on 2009-01-23
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

import org.jphototagger.lib.util.ArrayUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tests the Class {@link org.jphototagger.lib.util.ArrayUtil}.
 *
 * @author Elmar Baumann
 */
public class ArrayUtilTest {
    public ArrayUtilTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    /**
     * Test of toStringArray method, of class ArrayUtil.
     */
    @Test
    public void testToStringArray() {
        System.out.println("toStringArray");

        Object[] array = new Object[] { new Integer(12), "Eiscreme",
                                        new Double(25.5) };
        String[] expResult = new String[] { "12", "Eiscreme", "25.5" };
        String[] result    = ArrayUtil.toStringArray(array);

        assertArrayEquals(expResult, result);

        URI uri = null;

        try {
            uri       = new URI("http://www.elmar-baumann.de");
            array     = new Object[] { uri };
            expResult = new String[] { uri.toString() };
            result    = ArrayUtil.toStringArray(array);
            assertArrayEquals(expResult, result);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ArrayUtilTest.class.getName()).log(Level.SEVERE,
                             null, ex);
        }

        array     = new Object[] {};
        expResult = new String[] {};
        result    = ArrayUtil.toStringArray(array);
        assertArrayEquals(expResult, result);
        array = new Object[] { "a", null, "b" };

        try {
            ArrayUtil.toStringArray(array);
            fail("IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException ex) {

            // ok
        }

        try {
            ArrayUtil.toStringArray(null);
            fail("NullpointerException was not thrown");
        } catch (NullPointerException ex) {

            // ok
        }
    }
}
