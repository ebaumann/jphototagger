package org.jphototagger.lib.runtime;

import org.jphototagger.lib.runtime.External;
import org.jphototagger.lib.generics.Pair;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann
 */
public class ExternalTest {
    public ExternalTest() {}

    /**
     * Test of executeGetOutput method, of class External.
     */
    @Test
    public void testExecuteGetOutput() {
        System.out.println("executeGetOutput");

        String               output         = "abcd";
        String               command        = "echo " + output;
        Pair<byte[], byte[]> result         =
            External.executeGetOutput(command, 10000);
        String               resultToString =
            new String(result.getFirst()).trim();    // trim(): "echo" appends "\n"

        assertEquals(output, resultToString);
        assertEquals(null, result.getSecond());
        result = External.executeGetOutput("thiscommanddoesnotexist-really",
                                           10000);
        assertNull(result);
    }
}
