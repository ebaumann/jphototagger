package de.elmar_baumann.imagemetadataviewer.data;

import java.awt.Image;

/**
 * Daten einer Bilddatei.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/28
 */
public class ImageFile {

    private String filename;
    private long lastmodified = -1;
    private Image thumbnail;
    private Iptc iptc;
    private Xmp xmp;
    private Exif exif;

    public ImageFile() {
    }

    /**
     * Liefert den Namen der Bilddatei.
     * 
     * @return Dateiname oder null, wenn nicht definiert
     */
    public String getFilename() {
        return filename;
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
