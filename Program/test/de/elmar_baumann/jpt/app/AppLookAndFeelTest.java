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
package de.elmar_baumann.jpt.app;

import java.util.Locale;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann.jpt.app.AppLookAndFeel}.
 *
 * @author Elmar Baumann
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