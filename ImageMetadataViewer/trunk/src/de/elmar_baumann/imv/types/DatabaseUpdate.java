package de.elmar_baumann.imv.types;

public enum DatabaseUpdate {

    /**
     * Update all metadata, regardless of the modification date
     */
    Complete,
    /**
     * Update the EXIF data, regardless of the modification date
     */
    Exif,
    /**
     * Update the EXIF- and XMP data, regardless of the modification date
     */
    ExifAndXmp,
    /**
     * Update when the last modification time of the metadata in the file
     * not equals with the last 
     */
    LastModifiedChanged,
    /**
     * Update the thumbnail, regardless of the modification date
     */
    Thumbnail,
    /**
     * Update the XMP data, regardless of the modification date
     */
    Xmp;

    /**
     * Returns whether the database is to be updated <em>if the type is
     * NOT {@link #Complete}</em>.
     * 
     * @param   update update
     * @return  true if update equals this or update ore this is {@link #Complete},
     *          false if this or update equals {@link #LastModifiedChanged}
     */
    public boolean isUpdate(DatabaseUpdate update) {
        return !this.equals(LastModifiedChanged) &&
            !update.equals(LastModifiedChanged) &&
            (this.equals(update) ||
            this.equals(Complete) ||
            update.equals(Complete));
    }
}
