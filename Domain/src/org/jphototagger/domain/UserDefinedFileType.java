package org.jphototagger.domain;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class UserDefinedFileType {

    private Long id;
    private String suffix;
    private String description;
    private boolean externalThumbnailCreator;

    public UserDefinedFileType(UserDefinedFileType other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }
        this.id = other.id;
        this.suffix = other.suffix;
        this.description = other.description;
        this.externalThumbnailCreator = other.externalThumbnailCreator;
    }

    public UserDefinedFileType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        if (suffix == null) {
            throw new NullPointerException("suffix == null");
        }

        this.suffix = suffix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExternalThumbnailCreator() {
        return externalThumbnailCreator;
    }

    public void setExternalThumbnailCreator(boolean externalThumbnailCreator) {
        this.externalThumbnailCreator = externalThumbnailCreator;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof UserDefinedFileType)) {
            return false;
        }

        UserDefinedFileType other = (UserDefinedFileType) obj;

        return id == null
                ? other.id == null
                : id.equals(other.id);
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return description == null
                ? ""
                : description;
    }
}
