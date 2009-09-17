package de.elmar_baumann.lib.util;

import de.elmar_baumann.lib.comparator.ComparatorFilesNames;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann.lib.util.ComparatorFilesNames}.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-01-23
 */
public class ComparatorFilesNamesTest {

    public ComparatorFilesNamesTest() {
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
     * Test of compare method, of class ComparatorFilesNames.
     */
    @Test
    public void testCompare() {
        System.out.println("compare"); // NOI18N

        compare(ComparatorFilesNames.ASCENDING_CASE_SENSITIVE, true, false);
        compare(ComparatorFilesNames.ASCENDING_IGNORE_CASE, true, true);
        compare(ComparatorFilesNames.DESCENDING_CASE_SENSITIVE, false, false);
        compare(ComparatorFilesNames.DESCENDING_IGNORE_CASE, false, true);
    }

    private void compare(ComparatorFilesNames compare, boolean ascending, boolean ignoreCase) {
        File a = new File("a"); // NOI18N
        File b = new File("b"); // NOI18N
        File A = new File("A"); // NOI18N

        int result = compare.compare(a, b);
        assertTrue(ascending ? result < 0 : result > 0);

        result = compare.compare(b, a);
        assertTrue(ascending ? result > 0 : result < 0);

        result = compare.compare(b, b);
        assertTrue(result == 0);

        result = compare.compare(a, A);
        assertTrue(ignoreCase ? result == 0 : ascending ? result > 0 : result < 0);

        result = compare.compare(A, a);
        assertTrue(ignoreCase ? result == 0 : ascending ? result < 0 : result > 0);
    }
}