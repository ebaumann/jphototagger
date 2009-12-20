package de.elmar_baumann.lib.template;

import de.elmar_baumann.lib.generics.Pair;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann. }.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-01-29
 */
public class PairTest {

    public PairTest() {
    }

    /**
     * Test of getFirst method, of class Pair.
     */
    @Test
    public void testGetFirst() {
        System.out.println("getFirst"); // NOI18N
        Integer int1 = new Integer(5);
        Integer int2 = new Integer(25);
        Pair<Integer, Integer> instance = new Pair<Integer, Integer>(int1, int2);
        Integer expResult = int1;
        Integer result = instance.getFirst();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSecond method, of class Pair.
     */
    @Test
    public void testGetSecond() {
        System.out.println("getSecond"); // NOI18N
        Integer int1 = new Integer(5);
        Integer int2 = new Integer(25);
        Pair<Integer, Integer> instance = new Pair<Integer, Integer>(int1, int2);
        Integer expResult = int2;
        Integer result = instance.getSecond();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Pair.
     */
    @Test
    public void testToString() {
        System.out.println("toString"); // NOI18N
        String first = "First string"; // NOI18N
        Integer second = new Integer(100);
        Pair<String, Integer> instance = new Pair<String, Integer>(first, second);
        String expResult = "(First string, 100)"; // NOI18N
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Pair.
     */
    @Test
    public void testEquals() {
        System.out.println("equals"); // NOI18N
        String sFirst = "A string"; // NOI18N
        Integer iFirst = new Integer(88);
        String sSecond = "A string"; // NOI18N
        Integer iSecond = new Integer(88);
        Object different = "A different string"; // NOI18N
        Pair<String, Integer> pair1 = new Pair<String, Integer>(sFirst, iFirst);
        Pair<String, Integer> pair2 = new Pair<String, Integer>(sSecond, iSecond);

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