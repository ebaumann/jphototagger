package org.jphototagger.program.app;

import org.jphototagger.program.app.AppLookAndFeel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Locale;

/**
 * Tests the Class {@link org.jphototagger.program.app.AppLookAndFeel}.
 *
 * @author Elmar Baumann
 */
public class AppLookAndFeelTest {
    public AppLookAndFeelTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    /**
     * Test of toLocalizedPath method, of class AppLookAndFeel.
     */
    @Test
    public void testToLocalizedPath() {
        System.out.println("toLocalizedPath");

        final String lang      = Locale.getDefault().getLanguage();
        String       path      = "image.png";
        String       expResult = lang + "/" + path;
        String       result    = AppLookAndFeel.toLocalizedPath(path);

        assertEquals(expResult, result);
        path      = "/org/jphototagger/program/resoure/images/image.png";
        expResult = "/org/jphototagger/program/resoure/images/" + lang
                    + "/image.png";
        result = AppLookAndFeel.toLocalizedPath(path);
        assertEquals(expResult, result);
    }
}
