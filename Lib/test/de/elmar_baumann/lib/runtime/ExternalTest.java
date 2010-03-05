/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.runtime;

import de.elmar_baumann.lib.generics.Pair;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Class {@link de.elmar_baumann. }.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-01-30
 */
public class ExternalTest {

    public ExternalTest() {
    }

    /**
     * Test of executeGetOutput method, of class External.
     */
    @Test
    public void testExecuteGetOutput() {
        System.out.println("executeGetOutput");

        String output = "abcd";
        String command = "echo " + output;

        Pair<byte[], byte[]> result = External.executeGetOutput(command, 10000);

        String resultToString = new String(result.getFirst()).trim(); // trim(): "echo" appends "\n"
        assertEquals(output, resultToString);
        assertEquals(null, result.getSecond());

        result = External.executeGetOutput("thiscommanddoesnotexist-really", 10000);
        assertNull(result);
    }
}