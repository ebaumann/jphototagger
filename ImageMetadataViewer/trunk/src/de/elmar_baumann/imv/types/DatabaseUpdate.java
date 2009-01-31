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

    /**
     * Returns whether the database is to be updated <em>if the type is
     * NOT {@link #COMPLETE}</em>.
     * 
     * @param   update update
     * @return  true if update equals this or update ore this is {@link #COMPLETE},
     *          false if this or update equals {@link #IF_LAST_MODIFIED_CHANGED}
     */
    public boolean isUpdate(DatabaseUpdate update) {
        return !this.equals(IF_LAST_MODIFIED_CHANGED) &&
            !update.equals(IF_LAST_MODIFIED_CHANGED) &&
            (this.equals(update) ||
            this.equals(COMPLETE) ||
            update.equals(COMPLETE));
    }
}
