package org.jphototagger.lib.resource;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Relies on an appropriate Bundle.properties in this package.
 *
 * @author Elmar Baumann
 */
public class BundleTest {

    @Test
    public void testGetBundle() {
        Bundle bundle = Bundle.getBundle(BundleTest.class);
        String value = bundle.getString("BundleTestKey");

        assertEquals("BundleTestValue", value);
    }

    @Test
    public void testGetString_3args() {
        String s = Bundle.getString(BundleTest.class, "BundleTestKey2Args", "blubb", 25);

        assertEquals("Bla blubb bla 25", s);
    }

    @Test
    public void testGetString_String_ObjectArr() {
        Bundle bundle = new Bundle("org/jphototagger/lib/resource/Bundle");
        String s = bundle.getString("BundleTestKey2Args", "blubb", 25);

        assertEquals("Bla blubb bla 25", s);
    }

    @Test
    public void testContainsKey() {
        Bundle bundle = Bundle.getBundle(BundleTest.class);

        assertTrue(bundle.containsKey("BundleTestKey"));
        assertFalse(bundle.containsKey("NotExistingBundleTestKey"));
    }
}
