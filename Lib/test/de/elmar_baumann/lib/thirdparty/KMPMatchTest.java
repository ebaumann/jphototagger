/*
 * @(#)KMPMatchTest.java    Created on 2010/02/19
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

package de.elmar_baumann.lib.thirdparty;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann. }.
 *
 * @author Elmar Baumann
 */
public class KMPMatchTest {
    public KMPMatchTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    /**
     * Test of indexOf method, of class KMPMatch.
     */
    @Test
    public void testIndexOf() {
        System.out.println("indexOf");

        byte[] data    = { 0x00 };
        byte[] pattern = {
            0x3C, 0x3F, 0x78, 0x70, 0x61, 0x63, 0x6B, 0x65, 0x74, 0x20, 0x62,
            0x65, 0x67, 0x69, 0x6E, 0x3D
        };
        int expResult = -1;
        int result    = KMPMatch.indexOf(data, pattern);

        assertEquals(expResult, result);

        byte[] data2 = {
            0x3C, 0x3F, 0x78, 0x70, 0x61, 0x63, 0x6B, 0x65, 0x74, 0x20, 0x62,
            0x65, 0x67, 0x69, 0x6E, 0x3D
        };

        expResult = 0;
        result    = KMPMatch.indexOf(data2, pattern);
        assertEquals(expResult, result);

        byte[] data3 = {
            0x01, 0x02, 0x04, 0x3C, 0x3F, 0x78, 0x70, 0x61, 0x63, 0x6B, 0x65,
            0x74, 0x20, 0x62, 0x65, 0x67, 0x69, 0x6E, 0x3D
        };

        expResult = 3;
        result    = KMPMatch.indexOf(data3, pattern);
        assertEquals(expResult, result);

        byte[] data4 = {
            0x01, 0x02, 0x04, 0x3C, 0x3F, 0x78, 0x70, 0x61, 0x63, 0x6B, 0x65,
            0x74, 0x20, 0x62, 0x65, 0x67, 0x69, 0x6E, 0x3D, 0x05, 0x06
        };

        expResult = 3;
        result    = KMPMatch.indexOf(data4, pattern);
        assertEquals(expResult, result);

        byte[] data5 = {
            0x3C, 0x3F, 0x78, 0x70, 0x61, 0x63, 0x6B, 0x65, 0x74, 0x20, 0x62,
            0x65, 0x67, 0x69, 0x6E, 0x3D, 0x05, 0x06
        };

        expResult = 0;
        result    = KMPMatch.indexOf(data5, pattern);
        assertEquals(expResult, result);

        byte[] data6    = new byte[0];
        byte[] pattern2 = {
            0x3C, 0x3F, 0x78, 0x70, 0x61, 0x63, 0x6B, 0x65, 0x74, 0x20, 0x62,
            0x65, 0x67, 0x69, 0x6E, 0x3D
        };

        expResult = -1;
        result    = KMPMatch.indexOf(data6, pattern2);
        assertEquals(expResult, result);

        byte[] data7    = new byte[0];
        byte[] pattern3 = new byte[0];

        expResult = -1;
        result    = KMPMatch.indexOf(data7, pattern3);
        assertEquals(expResult, result);
    }
}
