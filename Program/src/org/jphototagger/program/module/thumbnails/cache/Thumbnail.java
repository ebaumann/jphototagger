package org.jphototagger.program.module.thumbnails.cache;

import java.awt.Image;
import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 * @author Elmar Baumann
 */
public final class Thumbnail implements Serializable {

    private static final long serialVersionUID = 1L;
    private final long imageFileLastModified;
    private final long imageFileLength;
    private final byte[] thumbnailBytes;

    public Thumbnail(byte[] thumbnailBytes, long imageFileLength, long imageFileLastModified) {
        if (thumbnailBytes == null) {
            throw new NullPointerException("thumbnailBytes == null");
        }
        this.thumbnailBytes = thumbnailBytes;
        this.imageFileLength = imageFileLength;
        this.imageFileLastModified = imageFileLastModified;
    }

    public Thumbnail(Thumbnail other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }
        this.thumbnailBytes = other.thumbnailBytes;
        this.imageFileLength = other.imageFileLength;
        this.imageFileLastModified = other.imageFileLastModified;
    }

    public byte[] getThumbnailBytes() {
        return thumbnailBytes;
    }

    public long getImageFileLastModified() {
        return imageFileLastModified;
    }

    public long getImageFileLength() {
        return imageFileLength;
    }

    public Image createImage() {
        return new ImageIcon(thumbnailBytes).getImage();
    }
}
