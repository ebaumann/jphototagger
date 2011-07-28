package org.jphototagger.lib.resource;

import org.junit.Test;
import static org.junit.Assert.*;

/**
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
}
