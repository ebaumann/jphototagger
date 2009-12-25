package de.elmar_baumann.lib.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann.lib.util.ArrayUtil}.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-01-23
 */
public class ArrayUtilTest {

    public ArrayUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of stringTokenToList method, of class ArrayUtil.
     */
    @Test
    public void testStringTokenToList() {
        System.out.println("stringTokenToList");
        String string = "anton,berta,cäsar,wilhelm";
        String delimiter = ",:";
        List<String> expResult = new ArrayList<String>();
        expResult.add("anton");
        expResult.add("berta");
        expResult.add("cäsar");
        expResult.add("wilhelm");
        List<String> result = ArrayUtil.stringTokenToList(string, delimiter);
        assertEquals(expResult, result);

        string = "anton,berta,cäsar:wilhelm";
        delimiter = ",:";
        expResult = new ArrayList<String>();
        expResult.add("anton");
        expResult.add("berta");
        expResult.add("cäsar");
        expResult.add("wilhelm");
        result = ArrayUtil.stringTokenToList(string, delimiter);
        assertEquals(expResult, result);

        string = "anton:berta::cäsar:wilhelm";
        delimiter = ",:";
        expResult = new ArrayList<String>();
        expResult.add("anton");
        expResult.add("berta");
        expResult.add("cäsar");
        expResult.add("wilhelm");
        result = ArrayUtil.stringTokenToList(string, delimiter);
        assertEquals(expResult, result);

        string = "anton";
        delimiter = ",";
        expResult = new ArrayList<String>();
        expResult.add(string);
        result = ArrayUtil.stringTokenToList(string, delimiter);
        assertEquals(expResult, result);

        string = "anton,berta,cäsar,:wilhelm";
        delimiter = "";
        expResult = new ArrayList<String>();
        expResult.add(string);
        result = ArrayUtil.stringTokenToList(string, delimiter);
        assertEquals(expResult, result);

        string = "";
        delimiter = ",";
        expResult = new ArrayList<String>();
        result = ArrayUtil.stringTokenToList(string, delimiter);
        assertEquals(expResult, result);

        try {
            ArrayUtil.stringTokenToList(null, "");
            fail("NullpointerException was not thrown");
        } catch (NullPointerException ex) {
            // ok
        }

        try {
            ArrayUtil.stringTokenToList("", null);
            fail("NullpointerException was not thrown");
        } catch (NullPointerException ex) {
            // ok
        }
    }

    /**
     * Test of integerTokenToArray method, of class ArrayUtil.
     */
    @Test
    public void testIntegerTokenToList() {
        System.out.println("integerTokenToList");

        String string = "1,125,7";
        String delimiter = ",";
        List<Integer> expResult = new ArrayList<Integer>();
        expResult.add(1);
        expResult.add(125);
        expResult.add(7);
        List<Integer> result = ArrayUtil.integerTokenToList(string, delimiter);
        assertEquals(expResult, result);

        string = "-1,42,29:33,72";
        delimiter = ":,";
        expResult = new ArrayList<Integer>();
        expResult.add(-1);
        expResult.add(42);
        expResult.add(29);
        expResult.add(33);
        expResult.add(72);
        result = ArrayUtil.integerTokenToList(string, delimiter);
        assertEquals(expResult, result);

        string = "-1";
        delimiter = ",";
        expResult = new ArrayList<Integer>();
        expResult.add(-1);
        result = ArrayUtil.integerTokenToList(string, delimiter);
        assertEquals(expResult, result);

        string = "";
        delimiter = ",";
        expResult = new ArrayList<Integer>();
        result = ArrayUtil.integerTokenToList(string, delimiter);
        assertEquals(expResult, result);

        try {
            string = "12,Peter";
            delimiter = ",";
            expResult = new ArrayList<Integer>();
            result = ArrayUtil.integerTokenToList(string, delimiter);
            fail("no NumberFormatException");
        } catch (NumberFormatException ex) {
            // ok
        }

        try {
            ArrayUtil.integerTokenToList(null, "");
            fail("NullpointerException was not thrown");
        } catch (NullPointerException ex) {
            // ok
        }

        try {
            ArrayUtil.integerTokenToList("", null);
            fail("NullpointerException was not thrown");
        } catch (NullPointerException ex) {
            // ok
        }
    }

    /**
     * Test of toStringArray method, of class ArrayUtil.
     */
    @Test
    public void testToStringArray() {
        System.out.println("toStringArray");
        Object[] array = new Object[]{new Integer(12), "Eiscreme", new Double(25.5)};
        String[] expResult = new String[]{"12", "Eiscreme", "25.5"};
        String[] result = ArrayUtil.toStringArray(array);
        assertArrayEquals(expResult, result);

        URI uri = null;
        try {
            uri = new URI("http://www.elmar-baumann.de");
            array = new Object[]{uri};
            expResult = new String[]{uri.toString()};
            result = ArrayUtil.toStringArray(array);
            assertArrayEquals(expResult, result);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ArrayUtilTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        array = new Object[]{};
        expResult = new String[]{};
        result = ArrayUtil.toStringArray(array);
        assertArrayEquals(expResult, result);

        array = new Object[]{"a", null, "b"};
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