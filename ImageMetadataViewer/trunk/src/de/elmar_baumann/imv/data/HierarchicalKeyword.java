package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.database.DatabaseHierarchicalKeywords;
import java.io.Serializable;

/**
 * A hierarchical keyword is a keyword (Dublin core subject) with one or zero
 * parents. Because every keyword can have a parent deep hierarchies are
 * possible.
 *
 * Persistent instances resists in the {@link DatabaseHierarchicalKeywords}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/10
 */
public final class HierarchicalKeyword implements Serializable {

    private Long id;
    private Long idParent;
    private String keyword;
    private Boolean real = true;

    /**
     * Creates a new instance of this class.
     *
     * @param id        database ID. <em>Only
     *                  {@link DatabaseHierarchicalKeywords}</em> shall set this
     *                  ID. Ohter callers shall set null
     * @param idParent  database ID of the keyword's parent
     * @param keyword   keyword
     * @param real      true if this keyword is a real keyword
     */
    public HierarchicalKeyword(
            Long id, Long idParent, String keyword, Boolean real) {
        this.id = id;
        this.idParent = idParent;
        this.keyword = keyword;
        this.real = real;
    }

    /**
     * Sets the database ID. <em>Only {@link DatabaseHierarchicalKeywords}</em>
     * shall call this mehtod!
     *
     * @param id ID. Default: null.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the dataase ID.
     *
     * @return database ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the database ID of the keyword's parent.
     *
     * @return database ID of the parent or null if undefined
     */
    public Long getIdParent() {
        return idParent;
    }

    /**
     * Sets the database ID of the keyword's parent.
     *
     * @param idParent ID of the parent. Default: null.
     */
    public void setIdParent(Long idParent) {
        this.idParent = idParent;
    }

    /**
     * Returns the keyword.
     *
     * @return keyword or null if undefined
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Sets the keyword.
     *
     * @param keyword keyword. Default: null.
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns whether this is a real keyword or just a helping container
     * object with no real keyword in it.
     *
     * @return true if this keyword is a real keyword or null if undefined
     */
    public Boolean isReal() {
        return real;
    }

    /**
     * Sets this to be a real keyword.
     *
     * @param real true if this keyword is a real keyword. Default: true.
     */
    public void setReal(Boolean real) {
        this.real = real;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final HierarchicalKeyword other = (HierarchicalKeyword) obj;
        if (this.id != other.id &&
                (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null
                            ? this.id.hashCode()
                            : 0);
        hash =
                59 * hash +
                (this.idParent != null
                 ? this.idParent.hashCode()
                 : 0);
        hash = 59 * hash + (this.keyword != null
                            ? this.keyword.hashCode()
                            : 0);
        return hash;
    }

    @Override
    public String toString() {
        return keyword;
    }
}
