package de.elmar_baumann.lib.util.logging;

/**
 * (Stack-) Frame in einer Logdatei von <code>java.util.logging.Logger</code>.
 * Ein Frame beschreibt eine Zeile im Backtrace eines Throwable-Objekts.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 1.0 2008/08/04
 */
public final class LogfileRecordFrame {

    private String className;
    private String methodName;
    private String line;

    /**
     * Liefert den Namen der Klasse, die den Logeintrag veranlasste.
     *
     * @return className Klassenname
     */
    public String getClassName() {
        return className;
    }

    /**
     * Setzt den vollständig qualifizierten Namen der Klasse, die den Logeintrag
     * veranlasste, z.B. <code>javax.marsupial.Wombat</code>.
     *
     * @param className Klassenname
     */
    public void setClassName(String className) {
        if (className == null)
            throw new NullPointerException("className == null");

        this.className = className;
    }

    /**
     * Liefert die Zeilennummer innerhalb der Quellcodedatei der Klasse.
     *
     * @return Zeilennummer (Integer) oder null, falls nicht existent
     * @see    #hasLine()
     */
    public String getLine() {
        return line;
    }

    /**
     * Liefert ob die Zeilennummer innerhalb der Quellcodedatei der Klasse
     * existiert.
     * 
     * @return true, falls existent
     */
    public boolean hasLine() {
        return line != null;
    }

    /**
     * Setzt die Zeilennummer innerhalb der Quellcodedatei der Klasse.
     *
     * @param line Zeilennummer (Integer)
     */
    public void setLine(String line) {
        if (line == null)
            throw new NullPointerException("line == null");

        this.line = line;
    }

    /**
     * Liefert den Namen der Methode, die den Logeintrag veranlasste. Dies kann
     * ein unqualifizierte Methodenname sein wie z.B. <code>fred</code> oder
     * Informationen über die Parametertypen in Klammern enthalten, z.B.
     * <code>fred(int,String)</code>.
     *
     * @return Methodenname
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Setzt den Namen der Methode, die den Logeintrag veranlasste. Dies kann
     * ein unqualifizierte Methodenname sein wie z.B. <code>fred</code> oder
     * Informationen über die Parametertypen in Klammern enthalten, z.B.
     * <code>fred(int,String)</code>.
     *
     * @param methodname Methodenname
     */
    public void setMethodName(String methodname) {
        if (methodname == null)
            throw new NullPointerException("methodname == null");

        this.methodName = methodname;
    }

    /**
     * Liefert, ob ein Teilstring in irgendeinem der Inhalte vorkommt.
     * 
     * @param substring Teilstring
     * @return          true, wenn der Teilstring in irgendeinem der Inhalte vorkommt
     */
    boolean contains(String substring) {
        if (substring == null)
            throw new NullPointerException("substring == null");

        return containsSubstring(getLine(), substring) ||
            containsSubstring(getClassName(), substring) ||
            containsSubstring(getMethodName(), substring);
    }

    private boolean containsSubstring(String string, String substring) {
        assert string != null : string;
        assert substring != null : substring;

        return string.toLowerCase().contains(substring.toLowerCase());
    }
}
