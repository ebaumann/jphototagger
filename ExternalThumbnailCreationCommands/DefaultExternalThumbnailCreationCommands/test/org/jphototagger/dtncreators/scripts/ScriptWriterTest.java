package org.jphototagger.dtncreators.scripts;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elmar Baumann
 */
public class ScriptWriterTest {
    
    @Test
    public void testSetReplace() throws Exception {
        ScriptWriter scriptWriter = new ScriptWriter();
        String replace = "${program}";
        String replacement = "C:\\Program Files\\GIMP\\gimp.exe";
        String scriptSourcePath = "/org/jphototagger/dtncreators/scripts/windows/ScriptWriterTest.bat";
        String readScript = scriptWriter.readScript(scriptSourcePath);
        
        assertFalse(readScript.contains(replacement));
        scriptWriter.addReplace(replace, replacement);
        String scriptWithReplaced = scriptWriter.replaceIn(readScript);
        assertTrue(scriptWithReplaced.contains(replacement));
    }

    @Test
    public void testReadScript() throws Exception {
        ScriptWriter scriptWriter = new ScriptWriter();
        String scriptSourcePath = "/org/jphototagger/dtncreators/scripts/windows/ScriptWriterTest.bat";
        String readScript = scriptWriter.readScript(scriptSourcePath);
        
        assertTrue(readScript.contains("@echo off"));
        assertTrue(readScript.contains("${program} ${parameters}"));
    }

    @Test
    public void testReplace() {
        // Implicit tested in testSetReplace
    }
}
