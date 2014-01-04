package org.jphototagger.lib.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class ExternalTest {

    public ExternalTest() {
    }

    /**
     * Test of executeWaitForTermination method, of class External.
     */
    @Test
    public void testExecuteGetOutput() {
        String output = "abcd";
        String command = "echo " + output;
        ProcessResult result = External.executeWaitForTermination(command, 10000);
        String resultToString = new String(result.getStdOutBytes()).trim();    // trim(): "echo" appends "\n"

        assertEquals(output, resultToString);
        assertEquals(null, result.getStdErrBytes());
        result = External.executeWaitForTermination("thiscommanddoesnotexist-really", 10000);
        assertNull(result);
    }
}
