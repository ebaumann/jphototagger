package org.jphototagger.domain.image;

import java.awt.Image;
import java.io.File;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.repository.SaveOrUpdate;

/**
 * @author Elmar Baumann
 */
public final class ImageFile {

    private final Set<SaveOrUpdate> saveIntoRepository = EnumSet.noneOf(SaveOrUpdate.class);
    private File file;
    private Exif exif;
    private Xmp xmp;
    private Image thumbnail;

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
        return file == null || !file.exists()
                ? -1
                : file.lastModified();
    }

    public long getSizeInBytes() {
        return file == null || !file.exists()
                ? 0
                : file.length();
    }

    /**
     * @return Thumbnail or null
     */
    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * @return EXIF or null
     */
    public Exif getExif() {
        return exif;
    }

    public void setExif(Exif exif) {
        this.exif = exif;
    }

    public boolean isSaveXmpIntoRepository() {
        return saveIntoRepository.contains(SaveOrUpdate.XMP);
    }

    public boolean isSaveExifIntoRepository() {
        return saveIntoRepository.contains(SaveOrUpdate.EXIF);
    }

    public boolean isSaveThumbnailIntoRepository() {
        return saveIntoRepository.contains(SaveOrUpdate.THUMBNAIL);
    }

    /**
     * Adds metadata to save into the repository.
     *
     * @param insert metadata to insert
     */
    public void addToSaveIntoRepository(SaveOrUpdate insert) {
        if (insert == null) {
            throw new NullPointerException("insert == null");
        }

        saveIntoRepository.add(insert);
    }

    /**
     * Two image files are equals if their file objects are equals.
     *
     * @param  obj object
     * @return     true if equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ImageFile)) {
            return false;
        }

        final ImageFile other = (ImageFile) obj;

        return Objects.equals(this.file, other.file);
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
