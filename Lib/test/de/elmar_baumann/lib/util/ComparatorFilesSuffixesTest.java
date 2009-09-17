package de.elmar_baumann.lib.util;

import de.elmar_baumann.lib.comparator.ComparatorFilesSuffixes;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann.lib.util.ComparatorFilesSuffixes}.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-01-23
 */
public class ComparatorFilesSuffixesTest {

    public ComparatorFilesSuffixesTest() {
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
     * Test of compare method, of class ComparatorFilesSuffixes.
     */
    @Test
    public void testCompare() {
        System.out.println("compare");

        compare(ComparatorFilesSuffixes.ASCENDING_CASE_SENSITIVE, true, false);
        compare(ComparatorFilesSuffixes.ASCENDING_IGNORE_CASE, true, true);
        compare(ComparatorFilesSuffixes.DESCENDING_CASE_SENSITIVE, false, false);
        compare(ComparatorFilesSuffixes.DESCENDING_IGNORE_CASE, false, true);
    }

    private void compare(ComparatorFilesSuffixes compare, boolean ascending, boolean ignoreCase) {
        File a = new File("a.a"); // NOI18N
        File b = new File("a.b"); // NOI18N
        File A = new File("a.A"); // NOI18N
        File X = new File("X"); // NOI18N
        File x = new File("x"); // NOI18N

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

        result = compare.compare(x, x);
        assertTrue(result == 0);

        result = compare.compare(X, x);
        assertTrue(ignoreCase ? result == 0 : ascending ? result < 0 : result > 0);

        result = compare.compare(a, x);
        assertTrue(ascending ? result > 0 : result < 0);

        result = compare.compare(x, a);
        assertTrue(ascending ? result < 0 : result > 0);
    }
}