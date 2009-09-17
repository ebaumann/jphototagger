package de.elmar_baumann.lib.util;

import de.elmar_baumann.lib.comparator.ComparatorFilesLastModified;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann.lib.util.ComparatorFilesLastModified}.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-01-23
 */
public class ComparatorFilesLastModifiedTest {

    public ComparatorFilesLastModifiedTest() {
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
     * Test of compare method, of class ComparatorFilesLastModified.
     */
    @Test
    public void testCompare() {
        System.out.println("compare"); // NOI18N
        try {
            File older = File.createTempFile("ComparatorFilesLastModifiedTestOlder", "t"); // NOI18N

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ComparatorFilesLastModifiedTest.class.getName()).log(Level.SEVERE, null, ex);
            }

            File newer = File.createTempFile("ComparatorFilesLastModifiedTestNewer", "t"); // NOI18N

            compare(ComparatorFilesLastModified.ASCENDING, newer, older, true);
            compare(ComparatorFilesLastModified.DESCENDING, newer, older, false);

            older.delete();
            newer.delete();
        } catch (IOException ex) {
            Logger.getLogger(ComparatorFilesLastModifiedTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Couldn't create test files!"); // NOI18N
        }
    }

    private void compare(ComparatorFilesLastModified compare, File newer, File older, boolean ascending) {
        int result = compare.compare(newer, newer);
        assertTrue(result == 0);

        result = compare.compare(older, newer);
        assertTrue(ascending ? result < 0 : result > 0);

        result = compare.compare(newer, older);
        assertTrue(ascending ? result > 0 : result < 0);
    }
}