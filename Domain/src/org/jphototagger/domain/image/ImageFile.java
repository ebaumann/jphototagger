package org.jphototagger.domain.image;

import java.awt.Image;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.domain.metadata.xmp.Xmp;

/**
 * @author Elmar Baumann
 */
public final class ImageFile {

    private long lastmodified = -1;
    private Set<SaveOrUpdate> insertIntoDb = new HashSet<SaveOrUpdate>();
    private Exif exif;
    private File file;
    private Image thumbnail;
    private Xmp xmp;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Xmp getXmp() {
        return xmp;
    }

    public void setXmp(Xmp xmp) {
        this.xmp = xmp;
    }

    public long getLastmodified() {
        return lastmodified;
    }

    public void setLastmodified(long lastmodified) {
        this.lastmodified = lastmodified;
    }

    /**
     *
     *
     * @return Thumbnail or null
     */
    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     *
     * @return EXIF or null
     */
    public Exif getExif() {
        return exif;
    }

    public void setExif(Exif exif) {
        this.exif = exif;
    }

    /**
     * Returns wheter a specific metadadata is to insert into the repository.
     *
     * @param  insert Is that to insert?
     * @return        true if <code>insert</code> is to insert into the repository
     *
     */
    public boolean isSaveInRepository(SaveOrUpdate insert) {
        return insertIntoDb.contains(insert);
    }

    public boolean isInsertXmpIntoDb() {
        return insertIntoDb.contains(SaveOrUpdate.XMP);
    }

    public boolean isInsertExifIntoDb() {
        return insertIntoDb.contains(SaveOrUpdate.EXIF);
    }

    public boolean isInsertThumbnailIntoDb() {
        return insertIntoDb.contains(SaveOrUpdate.THUMBNAIL);
    }

    public Set<SaveOrUpdate> getInsertIntoDb() {
        return new HashSet<SaveOrUpdate>(insertIntoDb);
    }

    /**
     * Adds metadata to add into the repository.
     *
     * @param insert metadata to insert
     */
    public void addToSaveIntoRepository(SaveOrUpdate insert) {
        if (insert == null) {
            throw new NullPointerException("insert == null");
        }

        insertIntoDb.add(insert);
    }

    /**
     * Two image files are equals if their file objects are equals.
     *
     * @param  obj object
     * @return     true if equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ImageFile other = (ImageFile) obj;

        if ((this.file != other.file) && ((this.file == null) || !this.file.equals(other.file))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 59 * hash + ((this.file != null)
                ? this.file.hashCode()
                : 0);

        return hash;
    }

    @Override
    public String toString() {
        return file == null
                ? ""
                : file.toString();
    }
}
