package org.jphototagger.tcc.def.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.IoUtil;
import org.jphototagger.lib.util.SystemProperties;

/**
 *
 * @author Elmar Baumann
 */
public final class ScriptWriter {

    private final Map<String, String> replacementOf = new HashMap<String, String>();
    private static final String NEWLINE = SystemProperties.getLineSeparator();

    /**
     * Replaces in a script each occurence of {@code replaceIn} with {@code replacement}.
     * <em>Does not replaceIn within replaced strings.</em>
     *
     * @param replace
     * @param replacement
     */
    public void addReplace(String replace, String replacement) {
        if (replace == null) {
            throw new NullPointerException("replace == null");
        }

        if (replacement == null) {
            throw new NullPointerException("replacement == null");
        }

        replacementOf.put(replace, replacement);
    }

    /**
     *
     * @param scriptSourcePath scriptSourcePath e.g. "/org/jphotoTagger/resource/myscript.sh"
     * @param targetFile
     * @throws IOException
     */
    public void writeScript(String scriptSourcePath, File targetFile) throws IOException {
        if (scriptSourcePath == null) {
            throw new NullPointerException("scriptSourcePath == null");
        }

        if (targetFile == null) {
            throw new NullPointerException("targetFile == null");
        }

        FileUtil.getContentAsBytes(targetFile);

        String script = readScript(scriptSourcePath);
        String scriptWithReplacements = replaceIn(script);

        FileUtil.writeStringAsFile(scriptWithReplacements, targetFile);
    }

    /**
     *
     * @param scriptSourcePath e.g. "/org/jphotoTagger/resource/myscript.sh"
     * @return
     * @throws IOException
     */
    public String readScript(String scriptSourcePath) throws IOException {
        BufferedReader br = null;

        try {
            InputStream is = ScriptWriter.class.getResourceAsStream(scriptSourcePath);
            InputStreamReader isr = new InputStreamReader(is);
            StringBuilder sb = new StringBuilder();
            String line = null;
            boolean isFirstLine = true;

            br = new BufferedReader(isr);

            while ((line = br.readLine()) != null) {
                sb.append(isFirstLine ? "" : NEWLINE);
                sb.append(line);
                isFirstLine = false;
            }

            if (sb.length() > 0) {
                sb.append(NEWLINE);
            }

            return sb.toString();
        } finally {
            IoUtil.close(br);
        }
    }

    /**
     * Uses replacements in {@link #addReplace(java.lang.String, java.lang.String)}.
     *
     * @param s
     * @return s with replaced substrings
     */
    public String replaceIn(String s) {
        if (s == null) {
            throw new NullPointerException("s == null");
        }

        if (replacementOf.isEmpty()) {
            return s;
        }

        String replaced = s;
        Set<String> replacementOfKeys = replacementOf.keySet();

        for (String replace : replacementOfKeys) {
            String replacement = replacementOf.get(replace);

            replaced = replaced.replace(replace, replacement);
        }

        return replaced;
    }
}
