package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.tasks.InsertImageFilesIntoDatabase;
import java.awt.Image;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Daten einer Bilddatei.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/28
 */
public final class ImageFile {

    private String filename;
    private long lastmodified = -1;
    private Image thumbnail;
    private Iptc iptc;
    private Xmp xmp;
    private Exif exif;
    private Set<InsertImageFilesIntoDatabase.Insert> insertIntoDb =
            new HashSet<InsertImageFilesIntoDatabase.Insert>();

    /**
     * Liefert den Namen der Bilddatei.
     * 
     * @return Dateiname oder null, wenn nicht definiert
     */
    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return new File(filename);
    }

    /**
     * Setzt den Namen der Bilddatei.
     * 
     * @param filename Dateiname
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Liefert die IPTC-Metadaten der Bilddatei.
     * 
     * @return IPTC-Metadaten oder null, wenn nicht definiert
     */
    public Iptc getIptc() {
        return iptc;
    }

    /**
     * Setzt die IPTC-Metadaten der Bilddatei.
     * 
     * @param iptc IPTC-Metadaten
     */
    public void setIptc(Iptc iptc) {
        this.iptc = iptc;
    }

    /**
     * Liefert die XMP-Metadaten der Bilddatei.
     * 
     * @return XMP-Metadaten oder null, wenn nicht definiert
     */
    public Xmp getXmp() {
        return xmp;
    }

    /**
     * Setzt die XMP-Metadaten der Bilddatei.
     * 
     * @param xmp XMP-Metadaten
     */
    public void setXmp(Xmp xmp) {
        this.xmp = xmp;
    }

    /**
     * Liefert, wann die Bilddatei das letzte Mal modifiziert wurde.
     * 
     * @return Millisekunden seit 1970 der letzten Modifikation oder -1, wenn nicht definiert
     */
    public long getLastmodified() {
        return lastmodified;
    }

    /**
     * Setzt, wann die Bilddatei das letzte Mal modifiziert wurde.
     * 
     * @param lastmodified Millisekunden seit 1970 der letzten Modifikation
     */
    public void setLastmodified(long lastmodified) {
        this.lastmodified = lastmodified;
    }

    /**
     * Liefert ein Thumbnail der Bilddatei.
     * 
     * @return Thumbnail oder null, wenn nicht definiert
     */
    public Image getThumbnail() {
        return thumbnail;
    }

    /**
     * Setzt das Thumbnail der Bilddatei.
     * 
     * @param thumbnail Thumbnail
     */
    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * Liefert die EXIF-Daten der Bilddatei.
     * 
     * @return EXIF-Daten oder null, wenn nicht definiert
     */
    public Exif getExif() {
        return exif;
    }

    /**
     * Setzt die EXIF-Daten der Bilddatei.
     * 
     * @param exif EXIF-Daten
     */
    public void setExif(Exif exif) {
        this.exif = exif;
    }

    /**
     * Returns wheter a specific metadadata is to insert into the database.
     *
     * @param  insert Is that to insert?
     * @return        true if <code>insert</code> is to insert into the database
     *                (that means at least one call was made to
     *                {@link #addInsertIntoDb(de.elmar_baumann.imv.tasks.InsertImageFilesIntoDatabase.Insert)})
     *
     */
    public boolean isInsertIntoDb(InsertImageFilesIntoDatabase.Insert insert) {
        return insertIntoDb.contains(insert);
    }

    public boolean isInsertXmpIntoDb() {
        return insertIntoDb.contains(InsertImageFilesIntoDatabase.Insert.XMP);
    }

    public boolean isInsertExifIntoDb() {
        return insertIntoDb.contains(InsertImageFilesIntoDatabase.Insert.EXIF);
    }

    public boolean isInsertThumbnailIntoDb() {
        return insertIntoDb.contains(
                InsertImageFilesIntoDatabase.Insert.THUMBNAIL);
    }

    /**
     * Adds metadata to add into the database.
     *
     * @param insert metadata to insert
     */
    public void addInsertIntoDb(InsertImageFilesIntoDatabase.Insert insert) {
        insertIntoDb.add(insert);
    }

    /**
     * Liefert, ob keine Daten vorhanden sind.
     * 
     * @return true, wenn leer
     */
    public boolean isEmpty() {
        return filename == null &&
                lastmodified == -1 &&
                thumbnail == null &&
                iptc == null &&
                xmp == null &&
                exif == null;
    }
}
