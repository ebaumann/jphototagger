package de.elmar_baumann.lib.resource;

import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann. }.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-01-31
 */
public class SettingsTest {

    public SettingsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        List<String> expResult = new ArrayList<String>();
        List<String> result = Resources.INSTANCE.getFramesIconImagesPaths();

        assertArrayEquals(expResult.toArray(), result.toArray());
        assertEquals(result.size(), 0);
    }

    /**
     * Test of getFramesIconImagesPaths method, of class Settings.
     */
    @Test
    public void testGetIconImagesPaths() {
        System.out.println("getIconImagesPaths");

        List<String> expResult = new ArrayList<String>();
        List<String> result = Resources.INSTANCE.getFramesIconImagesPaths();

        expResult.add("/de/x/y/z.png");
        Resources.INSTANCE.setFramesIconImagesPath(expResult);
        result = Resources.INSTANCE.getFramesIconImagesPaths();
        assertArrayEquals(expResult.toArray(), result.toArray());
        assertEquals(result.size(), 1);
    }

    /**
     * Test of setFramesIconImagesPath method, of class Settings.
     */
    @Test
    public void testSetIconImagesPath() {
        System.out.println("setIconImagesPath");

        List<String> expResult = new ArrayList<String>();
        List<String> result = Resources.INSTANCE.getFramesIconImagesPaths();

        Resources.INSTANCE.setFramesIconImagesPath(expResult);
        result = Resources.INSTANCE.getFramesIconImagesPaths();
        assertArrayEquals(expResult.toArray(), result.toArray());
        assertEquals(result.size(), 0);

        expResult.add("/de/x/y/z.png");
        Resources.INSTANCE.setFramesIconImagesPath(expResult);
        result = Resources.INSTANCE.getFramesIconImagesPaths();
        assertArrayEquals(expResult.toArray(), result.toArray());
        assertEquals(result.size(), 1);
    }

    /**
     * Test of hasFrameIconImages method, of class Settings.
     */
    @Test
    public void testHasIconImages() {
        System.out.println("hasIconImages");

        List<String> paths = new ArrayList<String>();

        Resources.INSTANCE.setFramesIconImagesPath(paths);
        assertFalse(Resources.INSTANCE.hasFrameIconImages());

        paths.add("/de/x/y/z.png");
        Resources.INSTANCE.setFramesIconImagesPath(paths);
        assertTrue(Resources.INSTANCE.hasFrameIconImages());
    }
}