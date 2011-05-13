package org.jphototagger.lib.generics;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann
 */
public class PairTest {
    public PairTest() {}

    /**
     * Test of getFirst method, of class Pair.
     */
    @Test
    public void testGetFirst() {
        System.out.println("getFirst");

        Integer                int1     = new Integer(5);
        Integer                int2     = new Integer(25);
        Pair<Integer, Integer> instance = new Pair<Integer, Integer>(int1,
                                              int2);
        Integer expResult = int1;
        Integer result    = instance.getFirst();

        assertEquals(expResult, result);
    }

    /**
     * Test of getSecond method, of class Pair.
     */
    @Test
    public void testGetSecond() {
        System.out.println("getSecond");

        Integer                int1     = new Integer(5);
        Integer                int2     = new Integer(25);
        Pair<Integer, Integer> instance = new Pair<Integer, Integer>(int1,
                                              int2);
        Integer expResult = int2;
        Integer result    = instance.getSecond();

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Pair.
     */
    @Test
    public void testToString() {
        System.out.println("toString");

        String                first    = "First string";
        Integer               second   = new Integer(100);
        Pair<String, Integer> instance = new Pair<String, Integer>(first,
                                             second);
        String expResult = "(First string, 100)";
        String result    = instance.toString();

        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Pair.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");

        String                sFirst    = "A string";
        Integer               iFirst    = new Integer(88);
        String                sSecond   = "A string";
        Integer               iSecond   = new Integer(88);
        Object                different = "A different string";
        Pair<String, Integer> pair1     = new Pair<String, Integer>(sFirst,
                                              iFirst);
        Pair<String, Integer> pair2     = new Pair<String, Integer>(sSecond,
                                              iSecond);

        assertTrue(pair1.equals(pair2));
        assertFalse(pair1.equals(different));
        pair1 = new Pair<String, Integer>(null, null);
        pair2 = new Pair<String, Integer>(null, null);
        assertTrue(pair1.equals(pair2));
        pair1 = new Pair<String, Integer>(null, iFirst);
        pair2 = new Pair<String, Integer>(null, iSecond);
        assertTrue(pair1.equals(pair2));
        pair1 = new Pair<String, Integer>(sFirst, null);
        pair2 = new Pair<String, Integer>(sSecond, null);
        assertTrue(pair1.equals(pair2));
        pair1 = new Pair<String, Integer>(null, null);
        pair2 = new Pair<String, Integer>(sSecond, null);
        assertFalse(pair1.equals(pair2));
        assertFalse(pair1.equals(null));
    }
}
