package de.elmar_baumann.lib.io.filefilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Filter für Dateien, Verzeichnisse werden abgelehnt. Akzeptiert
 * reguläre Ausdrücke als Match-Pattern.
 * 
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class RegexFileFilter implements java.io.FileFilter {

    private List<String> acceptedPatterns = new ArrayList<String>();

    /**
     * Erzeugt einen Dateifilter.
     * 
     * @param acceptedPatterns String mit regulären Ausdrücken für akzeptierte
     *                         Dateien
     * @param delim            Begrenzer zwischen den einzelnen Mustern
     */
    public RegexFileFilter(String acceptedPatterns, String delim) {
        if (acceptedPatterns == null)
            throw new NullPointerException("acceptedPatterns == null"); // NOI18N
        if (delim == null)
            throw new NullPointerException("delim == null"); // NOI18N

        setAcceptedValues(acceptedPatterns, delim);
    }

    private void setAcceptedValues(String acceptedValueString, String delim) {
        assert acceptedValueString != null : acceptedValueString;
        assert delim != null : delim;

        StringTokenizer token = new StringTokenizer(acceptedValueString, delim);
        while (token.hasMoreElements()) {
            acceptedPatterns.add(token.nextToken());
        }
    }

    @Override
    public boolean accept(File pathname) {
        String filename = pathname.getName();
        for (String pattern : acceptedPatterns) {
            if (filename.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a file filter for f file chooser.
     *
     * @param  description  description
     * @return file filter
     */
    public javax.swing.filechooser.FileFilter forFileChooser(String description) {
        return new FileChooserFilter(this, description);
    }
}
