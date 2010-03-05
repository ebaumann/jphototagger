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
package de.elmar_baumann.lib.util;

import de.elmar_baumann.lib.util.CommandLineParser.Option;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann. }.
 *
 * @author Elmar Baumann
 * @version 2010/01/22
 */
public class CommandLineParserTest {

    public CommandLineParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of hasOption method, of class CommandLineParser.
     */
    @Test
    public void testHasOption() {
        System.out.println("hasOption");
        
        CommandLineParser parser = new CommandLineParser(new String[] { "-delta=0", "-quaffel", "-beta = 7" }, "-", "=");

        String name = "delta";
        boolean expResult = true;
        boolean result = parser.hasOption(name);
        assertEquals(expResult, result);

        name = "gamma";
        expResult = false;
        result = parser.hasOption(name);
        assertEquals(expResult, result);

        name = "beta";
        expResult = true;
        result = parser.hasOption(name);
        assertEquals(expResult, result);

        name = "quaffel";
        expResult = true;
        result = parser.hasOption(name);
        assertEquals(expResult, result);

        parser = new CommandLineParser(new String[] {}, "-", "=");
        expResult = false;
        result = parser.hasOption(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getOption method, of class CommandLineParser.
     */
    @Test
    public void testGetOption() {
        System.out.println("getOption");

        CommandLineParser parser = new CommandLineParser(new String[] { "-delta=0", "-quaffel", "-beta = 7 " }, "-", "=");

        String name = "delta";
        Option result = parser.getOption(name);
        assertEquals(name, result.getName());
        assertEquals("0", result.getValues().get(0));

        name = "quaffel";
        result = parser.getOption(name);
        assertEquals(name, result.getName());

        name = "beta";
        result = parser.getOption(name);
        assertEquals(name, result.getName());
        assertEquals("7", result.getValues().get(0));
    }

}