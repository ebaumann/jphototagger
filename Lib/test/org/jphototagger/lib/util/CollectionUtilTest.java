/*
 * @(#)CollectionUtilTest.java    Created on 
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

import org.jphototagger.lib.util.CollectionUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Elmar Baumann
 */
public class CollectionUtilTest {
    public CollectionUtilTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    /**
     * Test of binaryInsert method, of class CollectionUtil.
     */
    @Test
    public void testBinaryInsert() {
        System.out.println("binaryInsert");

        LinkedList<String> list = new LinkedList<String>();
        String             el2  = "Birne";

        CollectionUtil.binaryInsert(list, el2);
        assertTrue(list.size() == 1);
        assertEquals(el2, list.get(0));

        String el1 = "Apfel";

        CollectionUtil.binaryInsert(list, el1);
        assertTrue(Arrays.equals(list.toArray(), new Object[] { el1, el2 }));

        String el3 = "Zitrone";

        CollectionUtil.binaryInsert(list, el3);
        assertTrue(Arrays.equals(list.toArray(),
                                 new Object[] { el1, el2, el3 }));
        list.clear();
        CollectionUtil.binaryInsert(list, el1);
        CollectionUtil.binaryInsert(list, el3);
        assertTrue(Arrays.equals(list.toArray(), new Object[] { el1, el3 }));
        list.clear();
        CollectionUtil.binaryInsert(list, el1);
        CollectionUtil.binaryInsert(list, el2);
        CollectionUtil.binaryInsert(list, el1);
        assertTrue(Arrays.equals(list.toArray(),
                                 new Object[] { el1, el1, el2 }));
        list.clear();
        CollectionUtil.binaryInsert(list, el2);
        CollectionUtil.binaryInsert(list, el1);
        CollectionUtil.binaryInsert(list, el1);
        CollectionUtil.binaryInsert(list, el2);
        assertTrue(Arrays.equals(list.toArray(), new Object[] { el1, el1, el2,
                el2 }));
    }

    /**
     * Test of integerTokenToArray method, of class ArrayUtil.
     */
    @Test
    public void testIntegerTokenToList() {
        System.out.println("integerTokenToList");

        String        string    = "1,125,7";
        String        delimiter = ",";
        List<Integer> expResult = new ArrayList<Integer>();

        expResult.add(1);
        expResult.add(125);
        expResult.add(7);

        List<Integer> result = CollectionUtil.integerTokenToList(string,
                                   delimiter);

        assertEquals(expResult, result);
        string    = "-1,42,29:33,72";
        delimiter = ":,";
        expResult = new ArrayList<Integer>();
        expResult.add(-1);
        expResult.add(42);
        expResult.add(29);
        expResult.add(33);
        expResult.add(72);
        result = CollectionUtil.integerTokenToList(string, delimiter);
        assertEquals(expResult, result);
        string    = "-1";
        delimiter = ",";
        expResult = new ArrayList<Integer>();
        expResult.add(-1);
        result = CollectionUtil.integerTokenToList(string, delimiter);
        assertEquals(expResult, result);
        string    = "";
        delimiter = ",";
        expResult = new ArrayList<Integer>();
        result    = CollectionUtil.integerTokenToList(string, delimiter);
        assertEquals(expResult, result);

        try {
            string    = "12,Peter";
            delimiter = ",";
            expResult = new ArrayList<Integer>();
            result    = CollectionUtil.integerTokenToList(string, delimiter);
            fail("no NumberFormatException");
        } catch (NumberFormatException ex) {

            // ok
        }

        try {
            CollectionUtil.integerTokenToList(null, "");
            fail("NullpointerException was not thrown");
        } catch (NullPointerException ex) {

            // ok
        }

        try {
            CollectionUtil.integerTokenToList("", null);
            fail("NullpointerException was not thrown");
        } catch (NullPointerException ex) {

            // ok
        }
    }

    /**
     * Test of stringTokenToList method, of class ArrayUtil.
     */
    @Test
    public void testStringTokenToList() {
        System.out.println("stringTokenToList");

        String       string    = "anton,berta,cäsar,wilhelm";
        String       delimiter = ",:";
        List<String> expResult = new ArrayList<String>();

        expResult.add("anton");
        expResult.add("berta");
        expResult.add("cäsar");
        expResult.add("wilhelm");

        List<String> result = CollectionUtil.stringTokenToList(string,
                                  delimiter);

        assertEquals(expResult, result);
        string    = "anton,berta,cäsar:wilhelm";
        delimiter = ",:";
        expResult = new ArrayList<String>();
        expResult.add("anton");
        expResult.add("berta");
        expResult.add("cäsar");
        expResult.add("wilhelm");
        result = CollectionUtil.stringTokenToList(string, delimiter);
        assertEquals(expResult, result);
        string    = "anton:berta::cäsar:wilhelm";
        delimiter = ",:";
        expResult = new ArrayList<String>();
        expResult.add("anton");
        expResult.add("berta");
        expResult.add("cäsar");
        expResult.add("wilhelm");
        result = CollectionUtil.stringTokenToList(string, delimiter);
        assertEquals(expResult, result);
        string    = "anton";
        delimiter = ",";
        expResult = new ArrayList<String>();
        expResult.add(string);
        result = CollectionUtil.stringTokenToList(string, delimiter);
        assertEquals(expResult, result);
        string    = "anton,berta,cäsar,:wilhelm";
        delimiter = "";
        expResult = new ArrayList<String>();
        expResult.add(string);
        result = CollectionUtil.stringTokenToList(string, delimiter);
        assertEquals(expResult, result);
        string    = "";
        delimiter = ",";
        expResult = new ArrayList<String>();
        result    = CollectionUtil.stringTokenToList(string, delimiter);
        assertEquals(expResult, result);

        try {
            CollectionUtil.stringTokenToList(null, "");
            fail("NullpointerException was not thrown");
        } catch (NullPointerException ex) {

            // ok
        }

        try {
            CollectionUtil.stringTokenToList("", null);
            fail("NullpointerException was not thrown");
        } catch (NullPointerException ex) {

            // ok
        }
    }
}
