package de.elmar_baumann.jpt.data;

import de.elmar_baumann.jpt.data.Xmp;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.mapping.IptcXmpMapping;
import de.elmar_baumann.jpt.database.metadata.mapping.XmpRepeatableValues;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.lib.generics.Pair;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann. }.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-02-20
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

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDcCreator method, of class Xmp.
     */
    @Test
    public void testGetDcCreator() {
        System.out.println("getDcCreator");
        
        Xmp instance = new Xmp();

        assertNull(instance.getDcCreator());

        String creator = "Elmar";
        String expResult = creator;
        instance.setDcCreator(creator);
        String result = instance.getDcCreator();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDcCreator method, of class Xmp.
     */
    @Test
    public void testSetDcCreator() {
        System.out.println("setDcCreator");
        
        String creator = "Elmar";
        Xmp instance = new Xmp();
        instance.setDcCreator(creator);
        assertEquals(instance.getDcCreator(), creator);
    }

    /**
     * Test of getDcDescription method, of class Xmp.
     */
    @Test
    public void testGetDcDescription() {
        System.out.println("getDcDescription");

        Xmp instance = new Xmp();
        
        assertNull(instance.getDcDescription());

        String description = "bla";
        String expResult = description;
        instance.setDcDescription(description);
        String result = instance.getDcDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDcDescription method, of class Xmp.
     */
    @Test
    public void testSetDcDescription() {
        System.out.println("setDcDescription");
        
        String dcDescription = "bla";
        Xmp instance = new Xmp();
        instance.setDcDescription(dcDescription);
        assertEquals(instance.getDcDescription(), dcDescription);
    }

    /**
     * Test of getDcRights method, of class Xmp.
     */
    @Test
    public void testGetDcRights() {
        System.out.println("getDcRights");

        Xmp instance = new Xmp();

        assertNull(instance.getDcRights());

        String rights = "rights";
        String expResult = rights;
        instance.setDcRights(rights);
        String result = instance.getDcRights();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDcRights method, of class Xmp.
     */
    @Test
    public void testSetDcRights() {
        System.out.println("setDcRights");

        String dcRights = "rights";
        Xmp instance = new Xmp();
        instance.setDcRights(dcRights);
        assertEquals(instance.getDcRights(), dcRights);
    }

    /**
     * Test of getDcSubjects method, of class Xmp.
     */
    @Test
    public void testGetDcSubjects() {
        System.out.println("getDcSubjects");

        Xmp instance = new Xmp();

        assertNull(instance.getDcSubjects());

        List<String> expResult = Arrays.asList("baum", "wolke", "himmel");
        for (String subject : expResult) {
            instance.addDcSubject(subject);
        }
        List<String> result = instance.getDcSubjects();
        assertEquals(expResult, result);
    }

    /**
     * Test of addDcSubject method, of class Xmp.
     */
    @Test
    public void testAddDcSubject() {
        System.out.println("addDcSubject");

        String subject = "bla";
        Xmp instance = new Xmp();
        instance.addDcSubject(subject);
        assertEquals(subject, instance.getDcSubjects().get(0));
    }

    /**
     * Test of getDcTitle method, of class Xmp.
     */
    @Test
    public void testGetDcTitle() {
        System.out.println("getDcTitle");
        
        Xmp instance = new Xmp();

        assertNull(instance.getDcTitle());

        String title = "title";
        String expResult = title;
        instance.setDcTitle(title);
        String result = instance.getDcTitle();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDcTitle method, of class Xmp.
     */
    @Test
    public void testSetDcTitle() {
        System.out.println("setDcTitle");

        String dcTitle = "title";
        Xmp instance = new Xmp();
        instance.setDcTitle(dcTitle);
        assertEquals(instance.getDcTitle(), dcTitle);
    }

    /**
     * Test of getIptc4xmpcoreCountrycode method, of class Xmp.
     */
    @Test
    public void testGetIptc4xmpcoreCountrycode() {
        System.out.println("getIptc4xmpcoreCountrycode");

        Xmp instance = new Xmp();
        
        assertNull(instance.getIptc4xmpcoreCountrycode());
        
        String code = "ccode";
        String expResult = code;
        instance.setIptc4xmpcoreCountrycode(code);
        String result = instance.getIptc4xmpcoreCountrycode();
        assertEquals(expResult, result);
    }

    /**
     * Test of setIptc4xmpcoreCountrycode method, of class Xmp.
     */
    @Test
    public void testSetIptc4xmpcoreCountrycode() {
        System.out.println("setIptc4xmpcoreCountrycode");

        String iptc4xmpcoreCountrycode = "ccode";
        Xmp instance = new Xmp();
        instance.setIptc4xmpcoreCountrycode(iptc4xmpcoreCountrycode);
        assertEquals(instance.getIptc4xmpcoreCountrycode(), iptc4xmpcoreCountrycode);
    }

    /**
     * Test of getIptc4xmpcoreLocation method, of class Xmp.
     */
    @Test
    public void testGetIptc4xmpcoreLocation() {
        System.out.println("getIptc4xmpcoreLocation");

        Xmp instance = new Xmp();
        
        assertNull(instance.getIptc4xmpcoreLocation());
        
        String loc = "cloc";
        String expResult = loc;
        instance.setIptc4xmpcoreLocation(loc);
        String result = instance.getIptc4xmpcoreLocation();
        assertEquals(expResult, result);
    }

    /**
     * Test of setIptc4xmpcoreLocation method, of class Xmp.
     */
    @Test
    public void testSetIptc4xmpcoreLocation() {
        System.out.println("setIptc4xmpcoreLocation");

        String iptc4xmpcoreLocation = "loc";
        Xmp instance = new Xmp();
        instance.setIptc4xmpcoreLocation(iptc4xmpcoreLocation);
        assertEquals(instance.getIptc4xmpcoreLocation(), iptc4xmpcoreLocation);
    }

    /**
     * Test of getPhotoshopAuthorsposition method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopAuthorsposition() {
        System.out.println("getPhotoshopAuthorsposition");

        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopAuthorsposition());

        String apos = "apos";
        String expResult = apos;
        instance.setPhotoshopAuthorsposition(apos);
        String result = instance.getPhotoshopAuthorsposition();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPhotoshopAuthorsposition method, of class Xmp.
     */
    @Test
    public void testSetPhotoshopAuthorsposition() {
        System.out.println("setPhotoshopAuthorsposition");

        String photoshopAuthorsposition = "apos";
        Xmp instance = new Xmp();
        instance.setPhotoshopAuthorsposition(photoshopAuthorsposition);
        assertEquals(instance.getPhotoshopAuthorsposition(), photoshopAuthorsposition);
    }

    /**
     * Test of getPhotoshopCaptionwriter method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopCaptionwriter() {
        System.out.println("getPhotoshopCaptionwriter");

        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopCaptionwriter());

        String cw = "captwrt";
        String expResult = cw;
        instance.setPhotoshopCaptionwriter(cw);
        String result = instance.getPhotoshopCaptionwriter();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPhotoshopCaptionwriter method, of class Xmp.
     */
    @Test
    public void testSetPhotoshopCaptionwriter() {
        System.out.println("setPhotoshopCaptionwriter");

        String photoshopCaptionwriter = "cwr";
        Xmp instance = new Xmp();
        instance.setPhotoshopCaptionwriter(photoshopCaptionwriter);
        assertEquals(instance.getPhotoshopCaptionwriter(), photoshopCaptionwriter);
    }

    /**
     * Test of getPhotoshopCategory method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopCategory() {
        System.out.println("getPhotoshopCategory");

        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopCategory());

        String cat = "cat";
        String expResult = cat;
        instance.setPhotoshopCategory(cat);
        String result = instance.getPhotoshopCategory();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPhotoshopCategory method, of class Xmp.
     */
    @Test
    public void testSetPhotoshopCategory() {
        System.out.println("setPhotoshopCategory");

        String photoshopCategory = "cat";
        Xmp instance = new Xmp();
        instance.setPhotoshopCategory(photoshopCategory);
        assertEquals(instance.getPhotoshopCategory(), photoshopCategory);
    }

    /**
     * Test of getPhotoshopCity method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopCity() {
        System.out.println("getPhotoshopCity");

        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopCity());

        String city = "city";
        String expResult = city;
        instance.setPhotoshopCity(city);
        String result = instance.getPhotoshopCity();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPhotoshopCity method, of class Xmp.
     */
    @Test
    public void testSetPhotoshopCity() {
        System.out.println("setPhotoshopCity");

        String photoshopCity = "city";
        Xmp instance = new Xmp();
        instance.setPhotoshopCity(photoshopCity);
        assertEquals(instance.getPhotoshopCity(), photoshopCity);
    }

    /**
     * Test of getPhotoshopCountry method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopCountry() {
        System.out.println("getPhotoshopCountry");

        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopCountry());

        String country = "country";
        String expResult = country;
        instance.setPhotoshopCountry(country);
        String result = instance.getPhotoshopCountry();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPhotoshopCountry method, of class Xmp.
     */
    @Test
    public void testSetPhotoshopCountry() {
        System.out.println("setPhotoshopCountry");

        String photoshopCountry = "country";
        Xmp instance = new Xmp();
        instance.setPhotoshopCountry(photoshopCountry);
        assertEquals(instance.getPhotoshopCountry(), photoshopCountry);
    }

    /**
     * Test of getPhotoshopCredit method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopCredit() {
        System.out.println("getPhotoshopCredit");

        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopCredit());

        String credit = "credit";
        String expResult = credit;
        instance.setPhotoshopCredit(credit);
        String result = instance.getPhotoshopCredit();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPhotoshopCredit method, of class Xmp.
     */
    @Test
    public void testSetPhotoshopCredit() {
        System.out.println("setPhotoshopCredit");

        String photoshopCredit = "credit";
        Xmp instance = new Xmp();
        instance.setPhotoshopCredit(photoshopCredit);
        assertEquals(instance.getPhotoshopCredit(), photoshopCredit);
    }

    /**
     * Test of getPhotoshopHeadline method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopHeadline() {
        System.out.println("getPhotoshopHeadline");

        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopHeadline());

        String headline = "headline";
        String expResult = headline;
        instance.setPhotoshopHeadline(headline);
        String result = instance.getPhotoshopHeadline();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPhotoshopHeadline method, of class Xmp.
     */
    @Test
    public void testSetPhotoshopHeadline() {
        System.out.println("setPhotoshopHeadline");

        String photoshopHeadline = "headline";
        Xmp instance = new Xmp();
        instance.setPhotoshopHeadline(photoshopHeadline);
        assertEquals(instance.getPhotoshopHeadline(), photoshopHeadline);
    }

    /**
     * Test of getPhotoshopInstructions method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopInstructions() {
        System.out.println("getPhotoshopInstructions");

        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopInstructions());

        String instr = "instr";
        String expResult = instr;
        instance.setPhotoshopInstructions(instr);
        String result = instance.getPhotoshopInstructions();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPhotoshopInstructions method, of class Xmp.
     */
    @Test
    public void testSetPhotoshopInstructions() {
        System.out.println("setPhotoshopInstructions");

        String photoshopInstructions = "instr";
        Xmp instance = new Xmp();
        instance.setPhotoshopInstructions(photoshopInstructions);
        assertEquals(instance.getPhotoshopInstructions(), photoshopInstructions);
    }

    /**
     * Test of getPhotoshopSource method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopSource() {
        System.out.println("getPhotoshopSource");
        
        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopSource());

        String src = "src";
        String expResult = src;
        instance.setPhotoshopSource(src);
        String result = instance.getPhotoshopSource();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPhotoshopSource method, of class Xmp.
     */
    @Test
    public void testSetPhotoshopSource() {
        System.out.println("setPhotoshopSource");

        String photoshopSource = "src";
        Xmp instance = new Xmp();
        instance.setPhotoshopSource(photoshopSource);
        assertEquals(instance.getPhotoshopSource(), photoshopSource);
    }

    /**
     * Test of getPhotoshopState method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopState() {
        System.out.println("getPhotoshopState");

        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopState());

        String state = "state";
        String expResult = state;
        instance.setPhotoshopState(state);
        String result = instance.getPhotoshopState();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPhotoshopState method, of class Xmp.
     */
    @Test
    public void testSetPhotoshopState() {
        System.out.println("setPhotoshopState");

        String photoshopState = "state";
        Xmp instance = new Xmp();
        instance.setPhotoshopState(photoshopState);
        assertEquals(instance.getPhotoshopState(), photoshopState);
    }

    /**
     * Test of getPhotoshopSupplementalCategories method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopSupplementalCategories() {
        System.out.println("getPhotoshopSupplementalCategories");

        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopSupplementalCategories());

        List<String> expResult = Arrays.asList("hallo", "welt");
        for (String cat : expResult) {
            instance.addPhotoshopSupplementalCategory(cat);
        }
        List<String> result = instance.getPhotoshopSupplementalCategories();
        assertEquals(expResult, result);
    }

    /**
     * Test of addPhotoshopSupplementalCategory method, of class Xmp.
     */
    @Test
    public void testAddPhotoshopSupplementalCategory() {
        System.out.println("addPhotoshopSupplementalCategory");
        
        String category = "abc";
        Xmp instance = new Xmp();
        instance.addPhotoshopSupplementalCategory(category);
        assertEquals(instance.getPhotoshopSupplementalCategories().get(0), category);
    }

    /**
     * Test of getPhotoshopTransmissionReference method, of class Xmp.
     */
    @Test
    public void testGetPhotoshopTransmissionReference() {
        System.out.println("getPhotoshopTransmissionReference");

        Xmp instance = new Xmp();

        assertNull(instance.getPhotoshopTransmissionReference());

        String ref = "trref";
        String expResult = ref;
        instance.setPhotoshopTransmissionReference(ref);
        String result = instance.getPhotoshopTransmissionReference();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPhotoshopTransmissionReference method, of class Xmp.
     */
    @Test
    public void testSetPhotoshopTransmissionReference() {
        System.out.println("setPhotoshopTransmissionReference");

        String photoshopTransmissionReference = "ref";
        Xmp instance = new Xmp();
        instance.setPhotoshopTransmissionReference(photoshopTransmissionReference);
        assertEquals(instance.getPhotoshopTransmissionReference(), photoshopTransmissionReference);
    }

    /**
     * Test of setLastModified method, of class Xmp.
     */
    @Test
    public void testSetLastModified() {
        System.out.println("setLastModified");

        long lastModified = 1250;
        Xmp instance = new Xmp();
        instance.setLastModified(lastModified);
        assertTrue(lastModified == instance.getLastModified());
    }

    /**
     * Test of getLastModified method, of class Xmp.
     */
    @Test
    public void testGetLastModified() {
        System.out.println("getLastModified");

        Xmp instance = new Xmp();

        assertNull(instance.getLastModified());

        Long expResult = new Long(1250);
        instance.setLastModified(expResult);
        Long result = instance.getLastModified();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class Xmp.
     */
    @Test
    public void testGetValue() {
        System.out.println("getValue");

        List<Pair<IPTCEntryMeta, Column>> pairs = IptcXmpMapping.getAllPairs();
        Xmp instance = new Xmp();

        for (Pair<IPTCEntryMeta, Column> pair : pairs) {
            assertNull(instance.getValue(pair.getSecond()));
        }

        List<String> repeat = Arrays.asList("abc", "def", "ghi");
        String s = "bla";
        int size = pairs.size();
        for (int i = 0; i < size; i++) {
            Column xmpColumn = pairs.get(i).getSecond();
            if (XmpRepeatableValues.isRepeatable(xmpColumn)) {
                for (String rs : repeat) {
                    instance.setValue(xmpColumn, rs);
                }
                assertEquals(instance.getValue(xmpColumn), repeat);
            } else {
                String si = s + i;
                instance.setValue(xmpColumn, si);
                assertEquals(instance.getValue(xmpColumn), si);
            }
        }
    }

    /**
     * Test of setValue method, of class Xmp.
     */
    @Test
    public void testSetValue() {
        System.out.println("setValue");
        // testGetValue() covers this
    }

    /**
     * Test of removeValue method, of class Xmp.
     */
    @Test
    public void testRemoveValue() {
        System.out.println("removeValue");

        Xmp instance = new Xmp();
        Column xmpColumn = ColumnXmpDcCreator.INSTANCE;
        Column xmpRepeatableColumn = ColumnXmpDcSubjectsSubject.INSTANCE;
        String creator = "creator";
        String ab = "ab";
        String cd = "cd";

        instance.setDcCreator(creator);
        instance.removeValue(xmpColumn, "");
        assertNull(instance.getDcCreator());

        instance.addDcSubject(ab);
        instance.addDcSubject(cd);
        instance.removeValue(xmpRepeatableColumn, ab);
        assertNotNull(instance.getDcSubjects());
        assertEquals(instance.getDcSubjects().get(0), cd);
        instance.removeValue(xmpRepeatableColumn, cd);
        assertNull(instance.getDcSubjects());
    }

    /**
     * Test of isEmpty method, of class Xmp.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");

        List<Pair<IPTCEntryMeta, Column>> pairs = IptcXmpMapping.getAllPairs();
        String value = "abc";
        Xmp instance = new Xmp();

        for (Pair<IPTCEntryMeta, Column> pair : pairs) {
            Column xmpColumn = pair.getSecond();
            assertTrue(instance.isEmpty());
            instance.setValue(xmpColumn, value);
            assertFalse(instance.isEmpty());
            instance.removeValue(xmpColumn, value);
        }

    }
}