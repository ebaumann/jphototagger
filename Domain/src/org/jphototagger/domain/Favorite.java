package org.jphototagger.domain;

import java.io.File;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Favorite: File system directory + alias name + order (index).
 *
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class Favorite {
    private Long id;
    private int index = Integer.MIN_VALUE;
    private String name;
    private File directory;

    public Favorite() {}

    public Favorite(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        set(favorite);
    }

    public void set(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        this.id = favorite.id;
        this.index = favorite.index;
        this.name = favorite.name;
        this.directory = favorite.directory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        // Never change that (will be used to find model items)!
        return name == null
                ? ""
                : name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Favorite other = (Favorite) obj;

        if ((this.id != other.id) && ((this.id == null) ||!this.id.equals(other.id))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 59 * hash + ((this.id != null)
                            ? this.id.hashCode()
                            : 0);

        return hash;
    }
}
