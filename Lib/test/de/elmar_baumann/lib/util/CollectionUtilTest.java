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