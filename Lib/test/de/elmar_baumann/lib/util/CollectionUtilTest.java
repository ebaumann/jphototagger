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

import java.util.Arrays;
import java.util.LinkedList;
import org.junit.AfterClass;
import org.junit.BeforeClass;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public class CollectionUtilTest {

    public CollectionUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of binaryInsert method, of class CollectionUtil.
     */
    @Test
    public void testBinaryInsert() {
        System.out.println("binaryInsert");

        LinkedList<String> list = new LinkedList<String>();
        String el2  = "Birne";
        CollectionUtil.binaryInsert(list, el2);
        assertTrue(list.size() == 1);
        assertEquals(el2, list.get(0));
        String el1 = "Apfel";
        CollectionUtil.binaryInsert(list, el1);
        assertTrue(Arrays.equals(list.toArray(), new Object[] { el1, el2} ));
        String el3 = "Zitrone";
        CollectionUtil.binaryInsert(list, el3);
        assertTrue(Arrays.equals(list.toArray(), new Object[] { el1, el2, el3 } ));

        list.clear();
        CollectionUtil.binaryInsert(list, el1);
        CollectionUtil.binaryInsert(list, el3);
        assertTrue(Arrays.equals(list.toArray(), new Object[] { el1, el3 } ));

        list.clear();
        CollectionUtil.binaryInsert(list, el1);
        CollectionUtil.binaryInsert(list, el2);
        CollectionUtil.binaryInsert(list, el1);
        assertTrue(Arrays.equals(list.toArray(), new Object[] { el1, el1, el2 } ));

        list.clear();
        CollectionUtil.binaryInsert(list, el2);
        CollectionUtil.binaryInsert(list, el1);
        CollectionUtil.binaryInsert(list, el1);
        CollectionUtil.binaryInsert(list, el2);
        assertTrue(Arrays.equals(list.toArray(), new Object[] { el1, el1, el2, el2 } ));

    }

}