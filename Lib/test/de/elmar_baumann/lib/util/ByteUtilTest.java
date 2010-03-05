/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.util;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public class ByteUtilTest {

    public ByteUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of toInt method, of class ByteUtil.
     */
    @Test
    public void testToInt() {
        System.out.println("toInt");
        
        assertEquals(  0, ByteUtil.toInt((byte)0x0));
        assertEquals(  8, ByteUtil.toInt((byte)0x8));
        assertEquals(  9, ByteUtil.toInt((byte)0x9));
        assertEquals( 10, ByteUtil.toInt((byte)0xA));
        assertEquals( 15, ByteUtil.toInt((byte)0xF));
        assertEquals(100, ByteUtil.toInt((byte)0x64));
        assertEquals(255, ByteUtil.toInt((byte)0xFF));
    }

}