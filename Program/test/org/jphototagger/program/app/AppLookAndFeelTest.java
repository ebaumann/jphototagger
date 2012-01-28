package org.jphototagger.program.app;

import java.util.Locale;

import org.jphototagger.program.app.ui.AppLookAndFeel;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the Class {@code org.jphototagger.program.app.AppLookAndFeel}.
 *
 * @author Elmar Baumann
 */
public class AppLookAndFeelTest {

    public AppLookAndFeelTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of toLocalizedPath method, of class AppLookAndFeel.
     */
    @Test
    public void testToLocalizedPath() {
        final String lang = Locale.getDefault().getLanguage();
        String path = "image.png";
        String expResult = lang + "/" + path;
        String result = AppLookAndFeel.toLocalizedPath(path);

        assertEquals(expResult, result);
        path = "/org/jphototagger/program/resoure/images/image.png";
        expResult = "/org/jphototagger/program/resoure/images/" + lang + "/image.png";
        result = AppLookAndFeel.toLocalizedPath(path);
        assertEquals(expResult, result);
    }
}
