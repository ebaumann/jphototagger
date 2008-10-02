package de.elmar_baumann.imagemetadataviewer.database.metadata;

/**
 * Eine Tabellenspalte.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class Column {

    private Table table;
    private String name;
    private String description;
    private DataType dataType;
    private Column references = null;
    private boolean isIgnoreCase = true;
    private boolean isIndexed = true;
    private boolean isUnique = false;
    private boolean isPrimaryKey = false;
    private boolean canBeNull = true;
    private int length = 0;
    private ReferenceDirection referenceDirection = ReferenceDirection.backwards;

    /**
     * Typ der Spaltendaten.
     */
    public enum DataType {

        /** Binärdaten, Java-Typ: byte[] */
        binary,
        /** Datum, Java-Typ: java.sql.Date */
        date,
        /** Ganzzahl, Java-Typ: int */
        integer,
        /** Realzahl, Java-Typ: double */
        real,
        /** kleine Ganzzahl, Java-Typ: short */
        smallint,
        /** Zeichenkette variabler Länge, Java-Typ: java.lang.String */
        string
    };

    /**
     * Richtung der Referenz.
     */
    public enum ReferenceDirection {

        /** 
         * Rückwärtsgerichtete Referenz, üblich bei 1:n - Beziehungen für den
         * n-Teil
         */
        backwards,
        /** 
         * Rückwärtsgerichtete Referenz, üblich bei 1:n - Beziehungen für den
         * 1-Teil
         */
        forwards
    }

    /**
     * Erzeugt eine Instanz.
     * 
     * @param table Tabelle, in der die Spalte ist
     * @param name  Spaltenname
     * @param type  Spaltentyp
     */
    protected Column(Table table, String name, DataType type) {
        this.table = table;
        this.name = name;
        this.dataType = type;
    }

    @Override
    public String toString() {
        String desc = getDescription();
        if (desc.isEmpty()) {
            return name;
        }
        return desc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Column) {
            Column other = (Column) o;
            return getTable().equals(other.getTable()) &&
                getName().equals(other.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.getTable() != null ? this.getTable().hashCode() : 0);
        hash = 83 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
        return hash;
    }

    /**
     * Liefert, ob unterschieden wird zwischen Groß- und Kleinschreibung.
     * 
     * @return true, wenn unterschieden wird zwischen Groß- und Kleinschreibung
     */
    public boolean isIgnoreCase() {
        return isIgnoreCase;
    }

    /**
     * Setzt, ob unterschieden wird zwischen Groß- und Kleinschreibung.
     * 
     * @param isIgnoreCase true, wenn unterschieden wird zwischen Groß-
     *                     und Kleinschreibung. Default: false.
     */
    protected void setIsIgnoreCase(boolean isIgnoreCase) {
        this.isIgnoreCase = isIgnoreCase;
    }

    /**
     * Liefert, ob ein Index existiert für diese Spalte.
     * 
     * @return true, wenn ein Index existiert für diese Spalte
     */
    public boolean isIndexed() {
        return isIndexed;
    }

    /**
     * Setzt, dass ein Index existiert für diese Spalte.
     * 
     * @param isIndexed true, wenn ein Index existiert für diese Spalte.
     *                  Default: true.
     */
    protected void setIsIndexed(boolean isIndexed) {
        this.isIndexed = isIndexed;
    }

    /**
     * Liefert, ob die Spalte (Teil eines) Primärschlüssels ist.
     * 
     * @return true, wenn die Spalte (Teil eines) Primärschlüssels ist
     */
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    /**
     * Setzt, dass die Spalte (Teil eines) Primärschlüssels ist.
     * 
     * @param isPrimaryKey true, wenn die Spalte (Teil eines) Primärschlüssels
     *                     ist. Default: false.
     */
    protected void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
        if (isPrimaryKey) {
            canBeNull = false;
        }
    }

    /**
     * Liefert, ob die Spalte leer sein kann.
     * 
     * @return true, wenn die Spalte leer sein kann
     */
    public boolean isCanBeNull() {
        return canBeNull;
    }

    /**
     * Setzt, ob die Spalte leer sein kann.
     * 
     * @param canBeNull true, wenn die Spalte leer sein kann.
     *                   Default: true, bei Primärschlüsselspalten false
     */
    public void setCanBeNull(boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    /**
     * Liefert, ob alle Werte in dieser Spalte in der Tabelle nur einmal
     * vorkommen.
     * 
     * @return true, wenn alle Werte in dieser Spalte in der Tabelle nur
     *         einmal vorkommen
     */
    public boolean isIsUnique() {
        return isUnique;
    }

    /**
     * Setzt, dass alle Werte in dieser Spalte in der Tabelle nur einmal
     * vorkommen.
     * 
     * @param isUnique true, wenn alle Werte in dieser Spalte in der Tabelle
     *                 nur einmal vorkommen. Default: false.
     */
    protected void setIsUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    /**
     * Liefert die (maximale) Länge dieser Spalte.
     * 
     * @return (maximale) Länge dieser Spalte
     */
    public int getLength() {
        return length;
    }

    /**
     * Setzt die (maximale) Länge dieser Spalte.
     * 
     * @param length (maximale) Länge dieser Spalte.
     *               Default: 0.
     */
    protected void setLength(int length) {
        this.length = length;
    }

    /**
     * Liefert einen Schlüssel, z.B. zum persistenten Abspeichern.
     * 
     * @return Schlüssel
     */
    public String getKey() {
        return getClass().getName();
    }

    /**
     * Liefert die Tabelle, in der sich diese Spalte befindet.
     * 
     * @return Tabelle
     */
    public Table getTable() {
        return table;
    }

    /**
     * Setzt die Tabelle, in der sich diese Spalte befindet.
     * 
     * @param table die Tabelle, in der sich diese Spalte befindet
     */
    protected void setTable(Table table) {
        this.table = table;
    }

    /**
     * Liefert, welche Spalte (einer anderen Tabelle) diese Spalte referenziert.
     * Das heißt, diese Spalte enthält den (Teil eines) Primärschlüssel(s) einer
     * anderen Tabelle; sie ist ein Fremdschlüssel.
     * 
     * @return Referenzierte Spalte (einer anderen Tabelle) oder null, wenn
     *         diese Spalte keine andere Spalte referenziert
     */
    public Column getReferences() {
        return references;
    }

    /**
     * Setzt, welche Spalte (einer anderen Tabelle) diese Spalte refernziert.
     * Das heißt, diese Spalte enthält den (Teil eines) Primärschlüssel(s) einer
     * anderen Tabelle; sie ist ein Fremdschlüssel.
     * 
     * @param references Refernziert Spalte.
     *                   Default: null.
     */
    protected void setReferences(Column references) {
        this.references = references;
    }

    /**
     * Liefert den Namen der Spalte.
     * 
     * @return Spaltenname
     */
    public String getName() {
        return name;
    }

    /**
     * Liefert eine Beschreibung der Spalte.
     * 
     * @return Beschreibung der Spalte
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setzt die Beschreibung der Spalte.
     * 
     * @param description Beschreibung der Spalte
     */
    protected void setDescription(String description) {
        this.description = description;
    }

    /**
     * Liefert den Datentyp der Spalte.
     * 
     * @return Datentyp der Spalte
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Setzt den Datentyp der Spalte.
     * 
     * @param dataType Datentyp der Spalte
     */
    protected void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Liefert, ob die Spalte Fremdschlüssel ist. Abkürzung für
     * getReferences() != null.
     * 
     * @return true, wenn die Spalte Fremdschlüssel ist
     */
    public boolean isForeignKey() {
        return references != null;
    }

    /**
     * Liefert die Richtung der Referenz.
     * 
     * @return Richtung der Referenz
     */
    public ReferenceDirection getReferenceDirection() {
        return referenceDirection;
    }

    /**
     * Setzt die Richtung der Referenz.
     * 
     * @param referenceDirection Richtung der Referenz.
     *                           Default: backwards
     */
    protected void setReferenceDirection(ReferenceDirection referenceDirection) {
        this.referenceDirection = referenceDirection;
    }
}
