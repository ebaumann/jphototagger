package org.jphototagger.lib.util;

import org.jphototagger.lib.util.CommandLineParser.Option;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann
 */
public class CommandLineParserTest {
    public CommandLineParserTest() {}

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    /**
     * Test of hasOption method, of class CommandLineParser.
     */
    @Test
    public void testHasOption() {
        System.out.println("hasOption");

        CommandLineParser parser = new CommandLineParser(new String[] {
                                       "-delta=0",
                                       "-quaffel", "-beta = 7" }, "-", "=");
        String  name      = "delta";
        boolean expResult = true;
        boolean result    = parser.hasOption(name);

        assertEquals(expResult, result);
        name      = "gamma";
        expResult = false;
        result    = parser.hasOption(name);
        assertEquals(expResult, result);
        name      = "beta";
        expResult = true;
        result    = parser.hasOption(name);
        assertEquals(expResult, result);
        name      = "quaffel";
        expResult = true;
        result    = parser.hasOption(name);
        assertEquals(expResult, result);
        parser    = new CommandLineParser(new String[] {}, "-", "=");
        expResult = false;
        result    = parser.hasOption(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getOption method, of class CommandLineParser.
     */
    @Test
    public void testGetOption() {
        System.out.println("getOption");

        CommandLineParser parser = new CommandLineParser(new String[] {
                                       "-delta=0",
                                       "-quaffel", "-beta = 7 " }, "-", "=");
        String name   = "delta";
        Option result = parser.getOption(name);

        assertEquals(name, result.getName());
        assertEquals("0", result.getValues().get(0));
        name   = "quaffel";
        result = parser.getOption(name);
        assertEquals(name, result.getName());
        name   = "beta";
        result = parser.getOption(name);
        assertEquals(name, result.getName());
        assertEquals("7", result.getValues().get(0));
    }
}
