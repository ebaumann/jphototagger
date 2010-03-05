/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.resource;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann. }.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-01-31
 */
public class MutualExcludedResourceTest {

    public MutualExcludedResourceTest() {
    }

    private final class TestClass extends MutualExcludedResource<Object> {

        public TestClass() {
            setResource(new Object());
        }
    }

    /**
     * Test of isAvailable method, of class MutualExcludedResource.
     */
    @Test
    public void testIsAvailable() {
        System.out.println("isAvailable");

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
        System.out.println("getResource");

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
        System.out.println("releaseResource");

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
        System.out.println("setResource");

        final class TestClass2 extends MutualExcludedResource<Object> {

            @SuppressWarnings("unchecked")
            public TestClass2() {
                assertFalse(isAvailable());
                setResource(new Object());
                assertTrue(isAvailable());
            }
        }

        new TestClass2();
    }
}
