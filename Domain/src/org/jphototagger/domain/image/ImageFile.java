package org.jphototagger.domain.image;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.lib.io.FileUtil;

/**
 * @author Elmar Baumann
 */
public final class ImageFile {

    private long lastmodified = -1;
    private Set<SaveOrUpdate> insertIntoDb = EnumSet.noneOf(SaveOrUpdate.class);
    private Exif exif;
    private File file;
    private Image thumbnail;
    private Xmp xmp;
    private String checkSum;

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

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    /**
     * Uses {@link FileUtil#getMd5OfFileContent(java.io.File)}.
     *
     * <p>{@link #getFile()} has to return an existing file!
     * @param checkSum arbitrary checksum
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public boolean matchesCheckSum(String checkSum) throws IOException, NoSuchAlgorithmException {
        if (checkSum == null) {
            throw new NullPointerException("checkSum == null");
        }
        if (file == null) {
            throw new IllegalStateException("No file set");
        }
        if (this.checkSum == null) {
            return false;
        }
        return FileUtil.getMd5OfFileContent(file).equals(checkSum);
    }

    /**
     * Shortcut for {@link #matchesCheckSum(java.lang.String)} with {@link #getCheckSum()} as parameter.
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public boolean matchesCheckSum() throws IOException, NoSuchAlgorithmException {
        return matchesCheckSum(checkSum);
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
        return Collections.unmodifiableSet(insertIntoDb);
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
