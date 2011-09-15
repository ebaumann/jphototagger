package org.jphototagger.lib.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.jphototagger.api.component.DisplayNameProvider;

/**
 *
 * @author Elmar Baumann
 */
public final class RegexFileFilter implements java.io.FileFilter, Serializable, DisplayNameProvider {

    private static final long serialVersionUID = 5995205186843465364L;
    private final Set<String> acceptedPatterns = new HashSet<String>();
    private String displayname;

    public RegexFileFilter(RegexFileFilter other) {
        set(other);
    }

    public void set(RegexFileFilter other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }

        acceptedPatterns.clear();
        displayname = other.displayname;
        acceptedPatterns.addAll(other.acceptedPatterns);
    }

    /**
     * Erzeugt einen Dateifilter.
     *
     * @param acceptedPatterns String mit regulären Ausdrücken für akzeptierte
     *                         Dateien
     * @param delim            Begrenzer zwischen den einzelnen Mustern
     */
    public RegexFileFilter(String acceptedPatterns, String delim) {
        if (acceptedPatterns == null) {
            throw new NullPointerException("acceptedPatterns == null");
        }

        if (delim == null) {
            throw new NullPointerException("delim == null");
        }

        setAcceptedValues(acceptedPatterns, delim);
    }

    private void setAcceptedValues(String acceptedValueString, String delim) {
        StringTokenizer token = new StringTokenizer(acceptedValueString, delim);

        while (token.hasMoreElements()) {
            acceptedPatterns.add(token.nextToken());
        }
    }

    public void addAcceptPatternsOf(RegexFileFilter other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }

        if (other == this) {
            return;
        }

        acceptedPatterns.addAll(other.acceptedPatterns);
    }

    public void removeAcceptPatternsOf(RegexFileFilter other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }

        if (other == this) {
            return;
        }

        acceptedPatterns.removeAll(other.acceptedPatterns);
    }

    public void addAcceptPattern(String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }

        acceptedPatterns.add(pattern);
    }

    public void removeAcceptPattern(String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }

        acceptedPatterns.remove(pattern);
    }

    @Override
    public boolean accept(File file) {
        String pathname = file.getName();

        for (String pattern : acceptedPatterns) {
            if (pathname.matches(pattern)) {
                return true;
            }
        }

        return false;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    @Override
    public String getDisplayName() {
        return displayname == null
                ? acceptedPatterns.toString()
                : displayname;
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

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof RegexFileFilter)) {
            return false;
        }

        RegexFileFilter other = (RegexFileFilter) obj;

        return acceptedPatterns.equals(other.acceptedPatterns);
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 43 * hash + acceptedPatterns.hashCode();

        return hash;
    }
}
