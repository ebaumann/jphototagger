package org.jphototagger.lib.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Relies on an appropriate Bundle.properties in this package.
 *
 * @author Elmar Baumann
 */
public class BundleTest {

    @Test
    public void testGetString() {
        String s = Bundle.getString(BundleTest.class, "BundleTestKey2Args", "blubb", 25);

        assertEquals("Bla blubb bla 25", s);
    }
}
