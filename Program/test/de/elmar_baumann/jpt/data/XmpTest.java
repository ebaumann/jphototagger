/*
 * JPhotoTagger tags and finds images fast.
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

import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.jpt.data.Xmp.SetIptc;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.mapping.IptcXmpMapping;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpLastModified;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import java.util.Arrays;
import java.util.Collection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann. }.
 *
 * @author Elmar Baumann
 * @version 2010/02/08
 */
public class XmpTest {

    public XmpTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of contains method, of class Xmp.
     */
    @Test
    public void testContains() {
        System.out.println("contains");

        Xmp xmp = new Xmp();
        assertFalse(xmp.contains(ColumnXmpDcSubjectsSubject.INSTANCE));
        assertFalse(xmp.contains(ColumnXmpDcTitle.INSTANCE));

        xmp.setValue(ColumnXmpDcSubjectsSubject.INSTANCE, "test");
        xmp.setValue(ColumnXmpDcTitle.INSTANCE, "abc");
        assertTrue(xmp.contains(ColumnXmpDcSubjectsSubject.INSTANCE));
        assertTrue(xmp.contains(ColumnXmpDcTitle.INSTANCE));
    }

    /**
     * Test of remove method, of class Xmp.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");

        Xmp                        xmp         = new Xmp();
        ColumnXmpDcSubjectsSubject colSubjects = ColumnXmpDcSubjectsSubject.INSTANCE;
        ColumnXmpDcTitle           colTitle    = ColumnXmpDcTitle.INSTANCE;
        String                     subject     = "subject";
        String                     title       = "title";

        assertNull(xmp.remove(colSubjects));
        xmp.setValue(colSubjects, subject);
        xmp.setValue(colTitle, title);
        assertEquals(Arrays.asList(subject), xmp.remove(colSubjects));
        assertNull(xmp.remove(colSubjects));
        assertEquals(title, xmp.remove(colTitle));
        assertNull(xmp.remove(colTitle));
    }

    /**
     * Test of textRemoved method, of class Xmp.
     */
    @Test
    public void testTextRemoved() {
        System.out.println("textRemoved");

        Xmp xmp = new Xmp();
        String text = "bla";
        xmp.setValue(ColumnXmpDcDescription.INSTANCE, text);
        xmp.textRemoved(ColumnXmpDcDescription.INSTANCE, text);
        assertNull(xmp.remove(ColumnXmpDcDescription.INSTANCE));
    }

    /**
     * Test of textAdded method, of class Xmp.
     */
    @Test
    public void testTextAdded() {
        System.out.println("textAdded");

        String text = "fiffi";
        Xmp xmp = new Xmp();
        xmp.textAdded(ColumnXmpDcTitle.INSTANCE, text);
        assertEquals(text, xmp.getValue(ColumnXmpDcTitle.INSTANCE));
    }

    /**
     * Test of textChanged method, of class Xmp.
     */
    @Test
    @SuppressWarnings("element-type-mismatch")
    public void testTextChanged() {
        System.out.println("textChanged");

        Column xmpColumn = ColumnXmpPhotoshopCaptionwriter.INSTANCE;
        String oldText = "quaffel";
        String newText = "quirrel";
        Xmp xmp = new Xmp();
        xmp.setValue(xmpColumn, oldText);
        xmp.textChanged(xmpColumn, oldText, newText);
        assertEquals(newText, xmp.getValue(xmpColumn));

        xmpColumn = ColumnXmpDcSubjectsSubject.INSTANCE;
        xmp.setValue(xmpColumn, oldText);
        xmp.textChanged(xmpColumn, oldText, newText);
        assertFalse(((Collection<?>)xmp.getValue(xmpColumn)).contains(oldText));
        assertTrue(((Collection<?>)xmp.getValue(xmpColumn)).contains(newText));
    }

    /**
     * Test of setMetaDataTemplate method, of class Xmp.
     */
    @Test
    @SuppressWarnings("element-type-mismatch")
    public void testSetMetaDataTemplate() {
        System.out.println("setMetaDataTemplate");
        
        MetadataTemplate template = new MetadataTemplate();
        Xmp              xmp      = new Xmp();
        Column           col1     = ColumnXmpDcDescription.INSTANCE;
        Column           col2     = ColumnXmpDcSubjectsSubject.INSTANCE;
        Column           col3     = ColumnXmpPhotoshopCaptionwriter.INSTANCE;
        String           val1     = "description";
        String           val2     = "subject";

        template.setValueOfColumn(col1, val1);
        template.setValueOfColumn(col2, Arrays.asList(val2));
        xmp.setValue(col3, val2);

        xmp.setMetaDataTemplate(template);
        assertEquals(val1, xmp.getValue(col1));
        assertTrue(((Collection<?>) xmp.getValue(col2)).contains(val2));
        assertNull(xmp.getValue(col3));
    }

    /**
     * Test of setIptc method, of class Xmp.
     */
    @Test
    @SuppressWarnings("element-type-mismatch")
    public void testSetIptc() {
        System.out.println("setIptc");

        SetIptc                    options    = SetIptc.REPLACE_EXISTING_VALUES;
        Iptc                       iptc       = new Iptc();
        Xmp                        xmp        = new Xmp();
        String                     title      = "title";
        String                     creator    = "creator";
        String                     subject1   = "subject1";
        String                     subject2   = "subject2";
        ColumnXmpDcCreator         colCreator = ColumnXmpDcCreator.INSTANCE;
        ColumnXmpDcSubjectsSubject colSubject = ColumnXmpDcSubjectsSubject.INSTANCE;
        IPTCEntryMeta              headline   = IPTCEntryMeta.HEADLINE;

        iptc.setValue(headline, title);
        iptc.setValue(IptcXmpMapping.getIptcEntryMetaOfXmpColumn(colCreator), creator);
        iptc.setValue(IptcXmpMapping.getIptcEntryMetaOfXmpColumn(colSubject), subject1);
        xmp.setValue(colSubject, subject2);
        xmp.setValue(colCreator, "xxx");
        xmp.setIptc(iptc, options);

        assertEquals(title, xmp.getValue(IptcXmpMapping.getXmpColumnOfIptcEntryMeta(headline)));
        assertEquals(creator, xmp.getValue(colCreator));
        assertTrue(((Collection<?>)xmp.getValue(colSubject)).contains(subject1));
        assertFalse(((Collection<?>)xmp.getValue(colSubject)).contains(subject2));

        options = SetIptc.DONT_CHANGE_EXISTING_VALUES;
        iptc.setValue(headline, title);
        iptc.setValue(IptcXmpMapping.getIptcEntryMetaOfXmpColumn(colCreator), creator);
        iptc.setValue(IptcXmpMapping.getIptcEntryMetaOfXmpColumn(colSubject), subject1);
        xmp.setValue(colSubject, subject2);
        xmp.setValue(colCreator, "xxx");
        xmp.setIptc(iptc, options);

        assertEquals(title, xmp.getValue(IptcXmpMapping.getXmpColumnOfIptcEntryMeta(headline)));
        assertEquals("xxx", xmp.getValue(colCreator));
        assertTrue(((Collection<?>)xmp.getValue(colSubject)).contains(subject1));
        assertTrue(((Collection<?>)xmp.getValue(colSubject)).contains(subject2));
    }

    /**
     * Test of getValue method, of class Xmp.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");

        Column repeatableColumn = ColumnXmpDcSubjectsSubject.INSTANCE;
        Column stringColumn     = ColumnXmpPhotoshopCaptionwriter.INSTANCE;
        Column longColumn       = ColumnXmpLastModified.INSTANCE;
        String subject          = "subject";
        String captWriter       = "writer";
        Long   longValue        = Long.valueOf(5);
        Xmp    xmp              = new Xmp();

        xmp.setValue(repeatableColumn, subject);
        xmp.setValue(stringColumn, captWriter);
        xmp.setValue(longColumn, longValue);
        Collection<?> coll = (Collection<?>) xmp.getValue(repeatableColumn);
        assertTrue(coll.contains(subject));
    }

    /**
     * Test of setValue method, of class Xmp.
     */
    @Test
    @SuppressWarnings("element-type-mismatch")
    public void testSetValue() {
        System.out.println("setValue");

        Column xmpColumn = ColumnXmpDcCreator.INSTANCE;
        String creator   = "creator";
        Xmp xmp = new Xmp();

        assertNull(xmp.getValue(xmpColumn));
        xmp.setValue(xmpColumn, creator);
        assertEquals(creator, xmp.getValue(xmpColumn));

        xmpColumn = ColumnXmpDcSubjectsSubject.INSTANCE;
        String subject1 = "subject1";
        String subject2 = "subject2";
        assertNull(xmp.getValue(xmpColumn));
        xmp.setValue(xmpColumn, subject1);
        assertTrue(((Collection<?>) xmp.getValue(xmpColumn)).contains(subject1));
        assertFalse(((Collection<?>) xmp.getValue(xmpColumn)).contains(subject2));
        xmp.setValue(xmpColumn, subject2);
        assertTrue(((Collection<?>) xmp.getValue(xmpColumn)).contains(subject1));
        assertTrue(((Collection<?>) xmp.getValue(xmpColumn)).contains(subject2));
    }

    /**
     * Test of removeValue method, of class Xmp.
     */
    @Test
    @SuppressWarnings("element-type-mismatch")
    public void testRemoveValue() {
        System.out.println("removeValue");


        Column xmpColumn = ColumnXmpDcCreator.INSTANCE;
        String creator   = "creator";
        Xmp xmp = new Xmp();

        xmp.setValue(xmpColumn, creator);
        assertEquals(creator, xmp.getValue(xmpColumn));
        xmp.removeValue(xmpColumn, creator);
        assertNull(xmp.getValue(xmpColumn));

        xmpColumn = ColumnXmpDcSubjectsSubject.INSTANCE;
        String subject1 = "subject1";
        String subject2 = "subject2";
        xmp.setValue(xmpColumn, subject1);
        xmp.setValue(xmpColumn, subject2);
        assertTrue(((Collection<?>) xmp.getValue(xmpColumn)).contains(subject1));
        assertTrue(((Collection<?>) xmp.getValue(xmpColumn)).contains(subject2));
        xmp.removeValue(xmpColumn, subject1);
        assertFalse(((Collection<?>) xmp.getValue(xmpColumn)).contains(subject1));
        assertTrue(((Collection<?>) xmp.getValue(xmpColumn)).contains(subject2));
        xmp.removeValue(xmpColumn, subject2);
        assertNull(xmp.getValue(xmpColumn));
    }

    /**
     * Test of clear method, of class Xmp.
     */
    @Test
    public void testClear() {
        System.out.println("clear");

        Xmp xmp = new Xmp();

        xmp.setValue(ColumnXmpDcSubjectsSubject.INSTANCE, "abc");
        xmp.setValue(ColumnXmpPhotoshopAuthorsposition.INSTANCE, "abc");

        assertFalse(xmp.isEmpty());
        xmp.clear();
        assertTrue(xmp.isEmpty());
    }

    /**
     * Test of isEmpty method, of class Xmp.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");

        Xmp xmp = new Xmp();

        assertTrue(xmp.isEmpty());
        xmp.setValue(ColumnXmpDcCreator.INSTANCE, "abc");
        assertFalse(xmp.isEmpty());

        xmp = new Xmp();
        xmp.setValue(ColumnXmpDcSubjectsSubject.INSTANCE, "abc");
        assertFalse(xmp.isEmpty());
        xmp.removeValue(ColumnXmpDcSubjectsSubject.INSTANCE, "abc");
        assertTrue(xmp.isEmpty());
}

    /**
     * Test of set method, of class Xmp.
     */
    @Test
    public void testSet() {
        System.out.println("set");

        Xmp    xmp1    = new Xmp();
        Xmp    xmp2    = new Xmp();
        Column col1    = ColumnXmpDcCreator.INSTANCE;
        Column col2    = ColumnXmpDcSubjectsSubject.INSTANCE;
        Column col3    = ColumnXmpLastModified.INSTANCE;
        String creator = "creator";
        String subject = "subject";
        Long   longVal = Long.valueOf(25);

        xmp1.setValue(col1, creator);
        xmp1.setValue(col2, subject);
        xmp1.setValue(col3, longVal);

        xmp2.set(xmp1);

        assertEquals(creator, xmp2.getValue(col1));
        assertEquals(Arrays.asList(subject), xmp2.getValue(col2));
        assertEquals(longVal, xmp2.getValue(col3));
    }

    /**
     * Test of containsValue method, of class Xmp.
     */
    @Test
    public void testContainsValue() {
        System.out.println("containsValue");

        Column columnDcSubj = ColumnXmpDcSubjectsSubject.INSTANCE;
        Column columnLastM  = ColumnXmpLastModified.INSTANCE;
        Column columnTitle  = ColumnXmpDcTitle.INSTANCE;
        String keyw1        = "keyw1";
        String keyw2        = "keyw2";
        String title        = "title";
        Long   lastM        = Long.valueOf(200);
        Xmp    xmp          = new Xmp();

        assertFalse(xmp.containsValue(columnDcSubj, keyw1));
        assertFalse(xmp.containsValue(columnLastM, lastM));
        assertFalse(xmp.containsValue(columnTitle, title));

        xmp.setValue(columnDcSubj, keyw1);
        xmp.setValue(columnLastM, lastM);
        xmp.setValue(columnTitle, title);
        assertTrue(xmp.containsValue(columnDcSubj, keyw1));
        assertTrue(xmp.containsValue(columnLastM, lastM));
        assertTrue(xmp.containsValue(columnTitle, title));

        xmp.setValue(columnDcSubj, keyw2);
        assertTrue(xmp.containsValue(columnDcSubj, keyw1));
        assertTrue(xmp.containsValue(columnDcSubj, keyw2));
    }

    /**
     * Test of toString method, of class Xmp.
     */
    @Test
    public void testToString() {
    }
}