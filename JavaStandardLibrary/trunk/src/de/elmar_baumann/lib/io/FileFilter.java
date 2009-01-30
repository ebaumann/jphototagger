package de.elmar_baumann.lib.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Filter für Dateien, Verzeichnisse werden abgelehnt. Akzeptiert
 * reguläre Ausdrücke als Match-Pattern.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class FileFilter implements java.io.FileFilter {

    private List<String> acceptedPatterns = new ArrayList<String>();

    /**
     * Konstruktor zum späteren Hinzufügen von akzeptierten Match-Pattern.
     * 
     * @see #addAcceptPattern(String)
     */
    public FileFilter() {
    }

    /**
     * Kopierkonstruktor.
     * 
     * @param other Anderer Filter
     */
    public FileFilter(FileFilter other) {
        setFileFilter(other);
    }

    /**
     * Erzeugt einen Dateifilter.
     * 
     * @param acceptedPatterns String mit regulären Ausdrücken für akzeptierte
     *                         Dateien
     * @param delim            Begrenzer zwischen den einzelnen Mustern
     */
    public FileFilter(String acceptedPatterns, String delim) {
        setAcceptedValues(acceptedPatterns, delim);
    }

    /**
     * Setzt einen anderen Filter.
     * 
     * @param other Anderer Filter
     */
    public void setFileFilter(FileFilter other) {
        if (other == this) {
            return;
        }
        acceptedPatterns.clear();
        acceptedPatterns.addAll(acceptedPatterns);
    }

    private void setAcceptedValues(String acceptedValueString, String delim) {
        StringTokenizer token = new StringTokenizer(acceptedValueString, delim);
        while (token.hasMoreElements()) {
            acceptedPatterns.add(token.nextToken());
        }
    }

    /**
     * Fügt ein Match-Pattern hinzu.
     * 
     * @param pattern String mit regulärem Ausdruck für akzeptierte Dateien
     */
    public void addAcceptPattern(String pattern) {
        acceptedPatterns.add(pattern);
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
}
