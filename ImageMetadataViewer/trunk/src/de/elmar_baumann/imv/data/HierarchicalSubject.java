package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.database.DatabaseHierarchicalSubjects;

/**
 * A hierarchical subject is a (Dublin core) subject with one or zero parents.
 * Because every subject can have a parent deep hierarchies are possible.
 *
 * Persistent instances resists in the {@link DatabaseHierarchicalSubjects}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/10
 */
public final class HierarchicalSubject {

    private Long id;
    private Long idParent;
    private String subject;

    /**
     * Creates a new instance of this class.
     *
     * @param id        database ID. <em>Only
     *                  {@link DatabaseHierarchicalSubjects}</em> shall set this
     *                  ID. Ohter callers shall set null
     * @param idParent  database ID of the subject's parent
     * @param subject   Dublin Core subject
     */
    public HierarchicalSubject(Long id, Long idParent, String subject) {
        this.id = id;
        this.idParent = idParent;
        this.subject = subject;
    }

    /**
     * Sets the database ID. <em>Only {@link DatabaseHierarchicalSubjects</em>}
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
     * Returns the database ID of the subject's parent.
     *
     * @return database ID of the parent
     */
    public Long getIdParent() {
        return idParent;
    }

    /**
     * Sets the database ID of the subject's parent.
     *
     * @param idParent ID of the parent. Default: null.
     */
    public void setIdParent(Long idParent) {
        this.idParent = idParent;
    }

    /**
     * Returns the Dublin Core subject.
     *
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the Dublin Core subject.
     *
     * @param subject subject. Default: null.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final HierarchicalSubject other = (HierarchicalSubject) obj;
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
        hash = 59 * hash + (this.subject != null
                ? this.subject.hashCode()
                : 0);
        return hash;
    }

    @Override
    public String toString() {
        return subject;
    }
}
