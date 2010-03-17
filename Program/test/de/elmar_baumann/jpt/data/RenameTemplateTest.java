/*
 * @(#)RenameTemplateTest.java    2010/03/01
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

package de.elmar_baumann.jpt.data;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann. }.
 *
 * @author Elmar Baumann
 */
public class RenameTemplateTest {
    public RenameTemplateTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    public static RenameTemplate createTemplate() {
        RenameTemplate template = new RenameTemplate();

        template.setDateDelimiter("DateDelimiter");
        template.setDelimiter1("Delimiter1");
        template.setDelimiter2("Delimiter2");
        template.setFormatClassAtBegin(Short.class);
        template.setFormatClassAtEnd(Integer.class);
        template.setFormatClassInTheMiddle(Double.class);
        template.setId(Long.valueOf(0));
        template.setName("Name");
        template.setNumberCount(Integer.valueOf(1));
        template.setStartNumber(Integer.valueOf(2));
        template.setStepWidth(Integer.valueOf(3));
        template.setTextAtBegin("TextAtBegin");
        template.setTextAtEnd("TextAtEnd");
        template.setTextInTheMiddle("TextInTheMiddle");

        return template;
    }

    @Test
    public void testSetOther() {
        System.out.println("set");

        RenameTemplate other    = createTemplate();
        RenameTemplate template = new RenameTemplate();

        template.set(other);
        assertEqualsCreated(template);
        other = new RenameTemplate(createTemplate());
        assertEqualsCreated(template);
    }

    @Test
    public void testSetter() {
        System.out.println("testSetter");

        RenameTemplate template = createTemplate();

        assertEqualsCreated(template);
    }

    public static void assertEqualsCreated(RenameTemplate template) {
        assertEquals("DateDelimiter", template.getDateDelimiter());
        assertEquals("Delimiter1", template.getDelimiter1());
        assertEquals("Delimiter2", template.getDelimiter2());
        assertEquals(Short.class, template.getFormatClassAtBegin());
        assertEquals(Integer.class, template.getFormatClassAtEnd());
        assertEquals(Double.class, template.getFormatClassInTheMiddle());
        assertEquals(Long.valueOf(0), template.getId());
        assertEquals("Name", template.getName());
        assertEquals(Integer.valueOf(1), template.getNumberCount());
        assertEquals(Integer.valueOf(2), template.getStartNumber());
        assertEquals(Integer.valueOf(3), template.getStepWidth());
        assertEquals("TextAtBegin", template.getTextAtBegin());
        assertEquals("TextAtEnd", template.getTextAtEnd());
        assertEquals("TextInTheMiddle", template.getTextInTheMiddle());
    }

    @Test
    public void testNull() {
        System.out.println("testNull");

        RenameTemplate template = new RenameTemplate();

        assertNull(template.getDateDelimiter());
        assertNull(template.getDelimiter1());
        assertNull(template.getDelimiter2());
        assertNull(template.getFormatClassAtBegin());
        assertNull(template.getFormatClassAtEnd());
        assertNull(template.getFormatClassInTheMiddle());
        assertNull(template.getId());
        assertNull(template.getName());
        assertNull(template.getNumberCount());
        assertNull(template.getStepWidth());
        assertNull(template.getStartNumber());
        assertNull(template.getTextAtBegin());
        assertNull(template.getTextAtEnd());
        assertNull(template.getTextInTheMiddle());
    }
}
