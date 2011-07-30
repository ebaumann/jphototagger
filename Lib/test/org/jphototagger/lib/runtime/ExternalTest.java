package org.jphototagger.lib.runtime;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Elmar Baumann
 */
public class ExternalTest {

    public ExternalTest() {
    }

    /**
     * Test of executeGetOutput method, of class External.
     */
    @Test
    public void testExecuteGetOutput() {
        String output = "abcd";
        String command = "echo " + output;
        ExternalOutput result = External.executeGetOutput(command, 10000);
        String resultToString = new String(result.getOutputStream()).trim();    // trim(): "echo" appends "\n"

        assertEquals(output, resultToString);
        assertEquals(null, result.getErrorStream());
        result = External.executeGetOutput("thiscommanddoesnotexist-really", 10000);
        assertNull(result);
    }
}
