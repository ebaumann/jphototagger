package de.elmar_baumann.lib.util.logging;

import java.util.Vector;
import java.util.logging.Level;

/**
 * Datensatz einer Logdatei, geschrieben von <code>java.util.logging.Logger</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/04
 */
public class LogfileRecord {

    private String date;
    private Long millis;
    private String sequence;
    private String logger;
    private Level level;
    private String classname;
    private String methodname;
    private String thread;
    private String message;
    private String key;
    private String catalog;
    private LogfileRecordException exception;
    private Vector<String> params;

    public LogfileRecord() {
    }

    /**
     * Liefert bei lokalisierten Nachrichten den Namen des Resource-Bundles
     * des Loggers.
     * 
     * @return Name des Resource-Bundles oder null, falls nicht gesetzt
     * @see    #hasCatalog()
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * Setzt bei lokalisierten Nachrichten den Namen des Resource-Bundles
     * des Loggers.
     * 
     * @param catalog Name des Resource-Bundles
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    /**
     * Liefert bei lokalisierten Nachrichten den Schlüssel der Nachricht
     * zum Laden aus der Properties-Datei.
     * 
     * @return Schlüssel oder null, falls nicht gesetzt
     * @see    #getMessage()
     * @see    #hasKey()
     */
    public String getKey() {
        return key;
    }

    /**
     * Setzt bei lokalisierten Nachrichten den Schlüssel der Nachricht.
     * 
     * @param key Schlüssel
     * @see       #setMessage(java.lang.String)
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Liefert die Zeit der Erzeugung des Logdatei-Datensatzes in Millisekunden,
     * die vergingen seit 1. Januar 1970 Mitternacht, UTC.
     *
     * @return Millisekunden seit 1. Januar 1970 Mitternacht, UTC
     */
    public Long getMillis() {
        return millis;
    }

    /**
     * Setzt die Zeit der Erzeugung des Logdatei-Datensatzes in Millisekunden,
     * die vergingen seit 1. Januar 1970 Mitternacht, UTC.
     *
     * @param millis Millisekunden
     */
    public void setMillis(Long millis) {
        this.millis = millis;
    }

    /**
     * Liefert den Namen der Klasse, die den Logeintrag veranlasste.
     * 
     * @return className Klassenname oder null, falls nicht gesetzt
     * @see              #hasClassname()
     */
    public String getClassname() {
        return classname;
    }

    /**
     * Setzt den vollständig qualifizierten Namen der Klasse, die den Logeintrag 
     * veranlasste, z.B. <code>javax.marsupial.Wombat</code>.
     * 
     * @param classname Klasenname
     */
    public void setClassname(String classname) {
        this.classname = classname;
    }

    /**
     * Liefert Datum und Uhrzeit der Erzeugung des Logdatei-Datensatzes im
     * ISO 8601-Format.
     * 
     * @return Datum und Uhrzeit der Erzeugung
     */
    public String getDate() {
        return date;
    }

    /**
     * Setzt Datum und Uhrzeit der Erzeugung des Logdatei-Datensatzes im
     * ISO 8601-Format.
     * 
     * @param date Datum und Uhrzeit der Erzeugung im ISO 8601-Format
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Liefert den Loglevel.
     * 
     * @return Loglevel
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Setzt den Loglevel.
     * 
     * @param level Loglevel
     */
    public void setLevel(String level) {
        this.level = Level.parse(level);
    }

    /**
     * Liefert den Namen des Logger-Objekts.
     * 
     * @return Name des Logger-Objekts oder null, falls nicht gesetzt
     * @see    #hasLogger()
     */
    public String getLogger() {
        return logger;
    }

    /**
     * Setzt den Namen des Logger-Objekts.
     * 
     * @param logger Name des Logger-Objekts
     */
    public void setLogger(String logger) {
        this.logger = logger;
    }

    /**
     * Liefert die Nachricht des Logdatensatzes.
     * 
     * @return Nachricht
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setzt die Nachricht des Logdatensatzes.
     * 
     * @param message Nachricht
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Liefert den Namen der Methode, die den Logeintrag veranlasste. Dies kann
     * ein unqualifizierte Methodenname sein wie z.B. <code>fred</code> oder
     * Informationen über die Parametertypen in Klammern enthalten, z.B.
     * <code>fred(int,String)</code>.
     * 
     * @return Methodenname oder null, falls nicht gesetzt
     * @see #hasMethodname()
     */
    public String getMethodname() {
        return methodname;
    }

    /**
     * Setzt den Namen der Methode, die den Logeintrag veranlasste. Dies kann
     * ein unqualifizierte Methodenname sein wie z.B. <code>fred</code> oder
     * Informationen über die Parametertypen in Klammern enthalten, z.B.
     * <code>fred(int,String)</code>.
     * 
     * @param methodname Methodenname
     */
    public void setMethodname(String methodname) {
        this.methodname = methodname;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    /**
     * Liefert eine eindeutige Sequenznummer in der Quell-VM.
     * 
     * @return Sequenznummer in der Quell-VM
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Setzt eine eindeutige Sequenznummer in der Quell-VM.
     * 
     * @param sequence Sequenznummer in der Quell-VM
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Liefert die ID des Threads.
     * 
     * @return Thread-ID (Integer) oder null, falls nicht gesetzt
     * @see    #hasThread()
     */
    public String getThread() {
        return thread;
    }

    /**
     * Setzt die ID des Threads.
     * 
     * @param thread Thread-ID (Integer)
     */
    public void setThread(String thread) {
        this.thread = thread;
    }

    /**
     * Liefert bei lokalisierten Nachrichten jeden der Parameter des Strings
     * (erhalten mit <code>Object.toString()</code>) des korrespondierenden
     * Logdatensatz-Parameters.
     * 
     * @return Parameter oder null, falls nicht gesetzt
     * @see    #getMessage()
     * @see    #hasParams()
     */
    public Vector<String> getParams() {
        return params;
    }

    /**
     * Setzt bei lokalisierten Nachrichten jeden der Parameter des Strings
     * (erhalten mit <code>Object.toString()</code>) des korrespondierenden
     * Logdatensatz-Parameters.
     * 
     * @param params Parameter
     * @see          #setMessage(java.lang.String)
     */
    public void setParams(Vector<String> params) {
        this.params = params;
    }

    /**
     * Fügt bei lokalisierten Nachrichten einen der Parameter des Strings
     * (erhalten mit <code>Object.toString()</code>) des korrespondierenden
     * Logdatensatz-Parameters hinzu.
     * 
     * @param param Parameter
     * @see         #setMessage(java.lang.String)
     */
    public void addParam(String param) {
        if (params == null) {
            params = new Vector<String>();
        }
        params.add(param);
    }

    /**
     * Liefert eine Ausnahme.
     * 
     * @return Ausnahme oder null, falls nicht gesetzt
     * @see    #hasException()
     */
    public LogfileRecordException getException() {
        return exception;
    }

    /**
     * Setzt eine Ausnahme.
     * 
     * @param ex Ausnahme
     */
    public void setException(LogfileRecordException ex) {
        this.exception = ex;
    }

    /**
     * Liefert, ob im Logdateidatensatz ein Logger existiert.
     * 
     * @return true, falls existent
     */
    public boolean hasLogger() {
        return logger != null;
    }

    /**
     * Liefert, ob im Logdateidatensatz ein Klassenname existiert.
     * 
     * @return true, falls existent
     */
    public boolean hasClassname() {
        return classname != null;
    }

    /**
     * Liefert, ob im Logdateidatensatz ein Methodenname existiert.
     * 
     * @return true, falls existent
     */
    public boolean hasMethodname() {
        return methodname != null;
    }

    /**
     * Liefert, ob im Logdateidatensatz ein Thread existiert.
     * 
     * @return true, falls existent
     */
    public boolean hasThread() {
        return thread != null;
    }

    /**
     * Liefert, ob im Logdateidatensatz ein Key existiert.
     * 
     * @return true, falls existent
     */
    public boolean hasKey() {
        return key != null;
    }

    /**
     * Liefert, ob im Logdateidatensatz ein Katalog existiert.
     * 
     * @return true, falls existent
     */
    public boolean hasCatalog() {
        return catalog != null;
    }

    /**
     * Liefert, ob im Logdateidatensatz Parameter existieren.
     * 
     * @return true, falls existent
     */
    public boolean hasParams() {
        return params != null;
    }

    /**
     * Liefert, ob im Logdateidatensatz eine Ausnahme existiert.
     * 
     * @return true, falls existent
     */
    public boolean hasException() {
        return exception != null;
    }

    /**
     * Liefert, ob ein Teilstring in irgendeinem der Inhalte vorkommt.
     * Nicht gesucht wird nach dem Level und Inhalten für lokalisierte
     * Ausgabe. Die Suche ignoriert Groß- und Kleinschreibung.
     * 
     * @param substring Teilstring
     * @return          true, wenn der Teilstring in irgendeinem der Inhalte vorkommt
     */
    public boolean contains(String substring) {
        return containsSubstring(getMessage(), substring) ||
            containsSubstring(getClassname(), substring) ||
            containsSubstring(getMethodname(), substring) ||
            containsSubstring(getDate(), substring) ||
            containsSubstring(getLogger(), substring) ||
            containsSubstring(getSequence(), substring) ||
            containsSubstring(getThread(), substring) ||
            (hasException()
            ? getException().contains(substring)
            : false);
    }

    private boolean containsSubstring(String string, String substring) {
        return string == null
            ? false
            : string.toLowerCase().contains(substring.toLowerCase());
    }
}
