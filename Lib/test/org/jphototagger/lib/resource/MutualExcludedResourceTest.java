package org.jphototagger.lib.resource;

import org.jphototagger.lib.util.MutualExcludedResource;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Elmar Baumann
 */
public class MutualExcludedResourceTest {

    public MutualExcludedResourceTest() {
    }

    private final class TestClass extends MutualExcludedResource<Object> {

        TestClass() {
            setResource(new Object());
        }
    }

    /**
     * Test of isAvailable method, of class MutualExcludedResource.
     */
    @Test
    public void testIsAvailable() {
        MutualExcludedResource<?> res = new TestClass();

        assertTrue(res.isAvailable());
        res.getResource(this);
        assertFalse(res.isAvailable());
    }

    /**
     * Test of getResource method, of class MutualExcludedResource.
     */
    @Test
    public void testGetResource() {
        MutualExcludedResource<?> res = new TestClass();

        assertNotNull(res.getResource(this));

        Object o = new Object();

        assertNull(res.getResource(o));
        assertNull(res.getResource(this));
    }

    /**
     * Test of releaseResource method, of class MutualExcludedResource.
     */
    @Test
    public void testReleaseResource() {
        MutualExcludedResource<?> res = new TestClass();
        Object o = new Object();

        res.getResource(this);
        assertFalse(res.releaseResource(o));
        assertTrue(res.releaseResource(this));
        assertNotNull(res.getResource(o));
    }

    /**
     * Test of setResource method, of class MutualExcludedResource.
     */
    @Test
    public void testSetResource() {
        final class TestClass2 extends MutualExcludedResource<Object> {

            @SuppressWarnings("unchecked")
            TestClass2() {
                assertFalse(isAvailable());
                setResource(new Object());
                assertTrue(isAvailable());
            }
        }
        new TestClass2();
    }
}
