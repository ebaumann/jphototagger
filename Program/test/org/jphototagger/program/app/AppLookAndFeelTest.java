/*
 * @(#)AppLookAndFeelTest.java    Created on 2009/12/30
 *
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
