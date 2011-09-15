package org.jphototagger.program.repository.importer;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class KeywordString {

    private final String keyword;
    private final boolean real;

    public KeywordString(String keyword, boolean real) {
        this.keyword = keyword;
        this.real = real;
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean isReal() {
        return real;
    }
}
