package de.elmar_baumann.imv.types;

public enum DatabaseUpdate {

    /**
     * Update all metadata, regardless of the modification date
     */
    COMPLETE,
    /**
     * Update the EXIF data, regardless of the modification date
     */
    EXIF,
    /**
     * Update the EXIF- and XMP data, regardless of the modification date
     */
    EXIF_AND_XMP,
    /**
     * Update when the last modification time of the metadata in the file
     * not equals with the last 
     */
    IF_LAST_MODIFIED_CHANGED,
    /**
     * Update the thumbnail, regardless of the modification date
     */
    THUMBNAIL,
    /**
     * Update the XMP data, regardless of the modification date
     */
    XMP;
}
