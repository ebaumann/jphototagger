package de.elmar_baumann.jpt.app;

import java.util.Locale;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann.jpt.app.AppLookAndFeel}.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/12/30
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

        System.out.println("toLocalizedPath");

        final String lang = Locale.getDefault().getLanguage();

        String path      = "image.png";
        String expResult = lang + "/" + path;
        String result    = AppLookAndFeel.toLocalizedPath(path);

        assertEquals(expResult, result);

        path      = "/de/elmar_baumann/jpt/resoure/images/image.png";
        expResult = "/de/elmar_baumann/jpt/resoure/images/" + lang + "/image.png";
        result    = AppLookAndFeel.toLocalizedPath(path);

        assertEquals(expResult, result);
    }
}