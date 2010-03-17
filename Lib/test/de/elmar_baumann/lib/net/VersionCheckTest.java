/*
 * @(#)VersionCheckTest.java    2010/01/05
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

package de.elmar_baumann.lib.net;

import de.elmar_baumann.lib.util.Version;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann. }.
 *
 * @author Elmar Baumann
 */
public class VersionCheckTest {
    public VersionCheckTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    /**
     * Test of existsNewer method, of class NetVersion.
     * @throws Exception
     */
    @Test
    public void testExistsNewer() throws Exception {
        System.out.println("existsNewer");

        final String urlHtml =
            "http://localhost/fotografie/tipps/computer/lightroom/imagemetadataviewer.html";
        final String versionDelimiter = ".";
        Version      compareToVersion = new Version(0, 7, 2);
        boolean      expResult        = true;
        boolean      result           =
            compareToVersion.compareTo(NetVersion.getOverHttp(urlHtml,
                versionDelimiter)) < 0;

        assertEquals(expResult, result);
    }
}
