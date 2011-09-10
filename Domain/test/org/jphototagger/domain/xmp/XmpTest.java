package org.jphototagger.domain.xmp;

import java.util.Arrays;
import java.util.Collection;

import org.jphototagger.domain.iptc.Iptc;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.mapping.IptcXmpMapping;
import org.jphototagger.domain.metadata.xmp.XmpDcCreatorMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcDescriptionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcTitleMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpLastModifiedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopAuthorspositionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCaptionwriterMetaDataValue;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.domain.xmp.Xmp.SetIptc;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import com.imagero.reader.iptc.IPTCEntryMeta;

/**
 *
 * @author Elmar Baumann
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
        Xmp xmp = new Xmp();

        assertFalse(xmp.contains(XmpDcSubjectsSubjectMetaDataValue.INSTANCE));
        assertFalse(xmp.contains(XmpDcTitleMetaDataValue.INSTANCE));
        xmp.setValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, "test");
        xmp.setValue(XmpDcTitleMetaDataValue.INSTANCE, "abc");
        assertTrue(xmp.contains(XmpDcSubjectsSubjectMetaDataValue.INSTANCE));
        assertTrue(xmp.contains(XmpDcTitleMetaDataValue.INSTANCE));
    }

    /**
     * Test of remove method, of class Xmp.
     */
    @Test
    public void testRemove() {
        Xmp xmp = new Xmp();
        XmpDcSubjectsSubjectMetaDataValue colSubjects = XmpDcSubjectsSubjectMetaDataValue.INSTANCE;
        XmpDcTitleMetaDataValue colTitle = XmpDcTitleMetaDataValue.INSTANCE;
        String subject = "subject";
        String title = "title";

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
        Xmp xmp = new Xmp();
        String text = "bla";

        xmp.setValue(XmpDcDescriptionMetaDataValue.INSTANCE, text);
        xmp.textRemoved(XmpDcDescriptionMetaDataValue.INSTANCE, text);
        assertNull(xmp.remove(XmpDcDescriptionMetaDataValue.INSTANCE));
    }

    /**
     * Test of textAdded method, of class Xmp.
     */
    @Test
    public void testTextAdded() {
        String text = "fiffi";
        Xmp xmp = new Xmp();

        xmp.textAdded(XmpDcTitleMetaDataValue.INSTANCE, text);
        assertEquals(text, xmp.getValue(XmpDcTitleMetaDataValue.INSTANCE));
    }

    /**
     * Test of textChanged method, of class Xmp.
     */
    @Test
    @SuppressWarnings("element-type-mismatch")
    public void testTextChanged() {
        MetaDataValue xmpValue = XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE;
        String oldText = "quaffel";
        String newText = "quirrel";
        Xmp xmp = new Xmp();

        xmp.setValue(xmpValue, oldText);
        xmp.textChanged(xmpValue, oldText, newText);
        assertEquals(newText, xmp.getValue(xmpValue));
        xmpValue = XmpDcSubjectsSubjectMetaDataValue.INSTANCE;
        xmp.setValue(xmpValue, oldText);
        xmp.textChanged(xmpValue, oldText, newText);
        assertFalse(((Collection<?>) xmp.getValue(xmpValue)).contains(oldText));
        assertTrue(((Collection<?>) xmp.getValue(xmpValue)).contains(newText));
    }

    /**
     * Test of setMetaDataTemplate method, of class Xmp.
     */
    @Test
    @SuppressWarnings("element-type-mismatch")
    public void testSetMetaDataTemplate() {
        MetadataTemplate template = new MetadataTemplate();
        Xmp xmp = new Xmp();
        MetaDataValue col1 = XmpDcDescriptionMetaDataValue.INSTANCE;
        MetaDataValue col2 = XmpDcSubjectsSubjectMetaDataValue.INSTANCE;
        MetaDataValue col3 = XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE;
        String val1 = "description";
        String val2 = "subject";

        template.setMetaDataValue(col1, val1);
        template.setMetaDataValue(col2, Arrays.asList(val2));
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
        SetIptc options = SetIptc.REPLACE_EXISTING_VALUES;
        Iptc iptc = new Iptc();
        Xmp xmp = new Xmp();
        String title = "title";
        String creator = "creator";
        String subject1 = "subject1";
        String subject2 = "subject2";
        XmpDcCreatorMetaDataValue colCreator = XmpDcCreatorMetaDataValue.INSTANCE;
        XmpDcSubjectsSubjectMetaDataValue colSubject = XmpDcSubjectsSubjectMetaDataValue.INSTANCE;
        IPTCEntryMeta headline = IPTCEntryMeta.HEADLINE;

        iptc.setValue(headline, title);
        iptc.setValue(IptcXmpMapping.getIptcEntryMetaOfXmpMetaDataValue(colCreator), creator);
        iptc.setValue(IptcXmpMapping.getIptcEntryMetaOfXmpMetaDataValue(colSubject), subject1);
        xmp.setValue(colSubject, subject2);
        xmp.setValue(colCreator, "xxx");
        xmp.setIptc(iptc, options);
        assertEquals(title, xmp.getValue(IptcXmpMapping.getXmpMetaDataValueOfIptcEntryMeta(headline)));
        assertEquals(creator, xmp.getValue(colCreator));
        assertTrue(((Collection<?>) xmp.getValue(colSubject)).contains(subject1));
        assertFalse(((Collection<?>) xmp.getValue(colSubject)).contains(subject2));
        options = SetIptc.DONT_CHANGE_EXISTING_VALUES;
        iptc.setValue(headline, title);
        iptc.setValue(IptcXmpMapping.getIptcEntryMetaOfXmpMetaDataValue(colCreator), creator);
        iptc.setValue(IptcXmpMapping.getIptcEntryMetaOfXmpMetaDataValue(colSubject), subject1);
        xmp.setValue(colSubject, subject2);
        xmp.setValue(colCreator, "xxx");
        xmp.setIptc(iptc, options);
        assertEquals(title, xmp.getValue(IptcXmpMapping.getXmpMetaDataValueOfIptcEntryMeta(headline)));
        assertEquals("xxx", xmp.getValue(colCreator));
        assertTrue(((Collection<?>) xmp.getValue(colSubject)).contains(subject1));
        assertTrue(((Collection<?>) xmp.getValue(colSubject)).contains(subject2));
    }

    /**
     * Test of getValue method, of class Xmp.
     */
    @Test
    public void testGetValue() {
        MetaDataValue repeatableValue = XmpDcSubjectsSubjectMetaDataValue.INSTANCE;
        MetaDataValue stringValue = XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE;
        MetaDataValue longVal = XmpLastModifiedMetaDataValue.INSTANCE;
        String subject = "subject";
        String captWriter = "writer";
        Long longValue = Long.valueOf(5);
        Xmp xmp = new Xmp();

        xmp.setValue(repeatableValue, subject);
        xmp.setValue(stringValue, captWriter);
        xmp.setValue(longVal, longValue);

        Collection<?> coll = (Collection<?>) xmp.getValue(repeatableValue);

        assertTrue(coll.contains(subject));
    }

    /**
     * Test of setValue method, of class Xmp.
     */
    @Test
    @SuppressWarnings("element-type-mismatch")
    public void testSetValue() {
        MetaDataValue xmpValue = XmpDcCreatorMetaDataValue.INSTANCE;
        String creator = "creator";
        Xmp xmp = new Xmp();

        assertNull(xmp.getValue(xmpValue));
        xmp.setValue(xmpValue, creator);
        assertEquals(creator, xmp.getValue(xmpValue));
        xmpValue = XmpDcSubjectsSubjectMetaDataValue.INSTANCE;

        String subject1 = "subject1";
        String subject2 = "subject2";

        assertNull(xmp.getValue(xmpValue));
        xmp.setValue(xmpValue, subject1);
        assertTrue(((Collection<?>) xmp.getValue(xmpValue)).contains(subject1));
        assertFalse(((Collection<?>) xmp.getValue(xmpValue)).contains(subject2));
        xmp.setValue(xmpValue, subject2);
        assertTrue(((Collection<?>) xmp.getValue(xmpValue)).contains(subject1));
        assertTrue(((Collection<?>) xmp.getValue(xmpValue)).contains(subject2));
    }

    /**
     * Test of removeValue method, of class Xmp.
     */
    @Test
    @SuppressWarnings("element-type-mismatch")
    public void testRemoveValue() {
        MetaDataValue xmpValue = XmpDcCreatorMetaDataValue.INSTANCE;
        String creator = "creator";
        Xmp xmp = new Xmp();

        xmp.setValue(xmpValue, creator);
        assertEquals(creator, xmp.getValue(xmpValue));
        xmp.removeValue(xmpValue, creator);
        assertNull(xmp.getValue(xmpValue));
        xmpValue = XmpDcSubjectsSubjectMetaDataValue.INSTANCE;

        String subject1 = "subject1";
        String subject2 = "subject2";

        xmp.setValue(xmpValue, subject1);
        xmp.setValue(xmpValue, subject2);
        assertTrue(
                ((Collection<?>) xmp.getValue(xmpValue)).contains(subject1));
        assertTrue(
                ((Collection<?>) xmp.getValue(xmpValue)).contains(subject2));
        xmp.removeValue(xmpValue, subject1);
        assertFalse(
                ((Collection<?>) xmp.getValue(xmpValue)).contains(subject1));
        assertTrue(
                ((Collection<?>) xmp.getValue(xmpValue)).contains(subject2));
        xmp.removeValue(xmpValue, subject2);
        assertNull(xmp.getValue(xmpValue));
    }

    /**
     * Test of clear method, of class Xmp.
     */
    @Test
    public void testClear() {
        Xmp xmp = new Xmp();

        xmp.setValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, "abc");
        xmp.setValue(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE, "abc");
        assertFalse(xmp.isEmpty());
        xmp.clear();
        assertTrue(xmp.isEmpty());
    }

    /**
     * Test of isEmpty method, of class Xmp.
     */
    @Test
    public void testIsEmpty() {
        Xmp xmp = new Xmp();

        assertTrue(xmp.isEmpty());
        xmp.setValue(XmpDcCreatorMetaDataValue.INSTANCE, "abc");
        assertFalse(xmp.isEmpty());
        xmp = new Xmp();
        xmp.setValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, "abc");
        assertFalse(xmp.isEmpty());
        xmp.removeValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, "abc");
        assertTrue(xmp.isEmpty());
    }

    /**
     * Test of set method, of class Xmp.
     */
    @Test
    public void testSet() {
        Xmp xmp1 = new Xmp();
        Xmp xmp2 = new Xmp();
        MetaDataValue col1 = XmpDcCreatorMetaDataValue.INSTANCE;
        MetaDataValue col2 = XmpDcSubjectsSubjectMetaDataValue.INSTANCE;
        MetaDataValue col3 = XmpLastModifiedMetaDataValue.INSTANCE;
        String creator = "creator";
        String subject = "subject";
        Long longVal = Long.valueOf(25);

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
        MetaDataValue xmpDcSubj = XmpDcSubjectsSubjectMetaDataValue.INSTANCE;
        MetaDataValue xmpLastM = XmpLastModifiedMetaDataValue.INSTANCE;
        MetaDataValue xmpTitle = XmpDcTitleMetaDataValue.INSTANCE;
        String keyw1 = "keyw1";
        String keyw2 = "keyw2";
        String title = "title";
        Long lastM = Long.valueOf(200);
        Xmp xmp = new Xmp();

        assertFalse(xmp.containsValue(xmpDcSubj, keyw1));
        assertFalse(xmp.containsValue(xmpLastM, lastM));
        assertFalse(xmp.containsValue(xmpTitle, title));
        xmp.setValue(xmpDcSubj, keyw1);
        xmp.setValue(xmpLastM, lastM);
        xmp.setValue(xmpTitle, title);
        assertTrue(xmp.containsValue(xmpDcSubj, keyw1));
        assertTrue(xmp.containsValue(xmpLastM, lastM));
        assertTrue(xmp.containsValue(xmpTitle, title));
        xmp.setValue(xmpDcSubj, keyw2);
        assertTrue(xmp.containsValue(xmpDcSubj, keyw1));
        assertTrue(xmp.containsValue(xmpDcSubj, keyw2));
    }

    /**
     * Test of toString method, of class Xmp.
     */
    @Test
    public void testToString() {
    }
}
