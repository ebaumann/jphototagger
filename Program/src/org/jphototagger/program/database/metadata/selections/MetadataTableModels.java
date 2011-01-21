package org.jphototagger.program.database.metadata.selections;

import java.util.Collections;
import java.util.HashSet;
import org.jphototagger.program.model.TableModelExif;
import org.jphototagger.program.model.TableModelIptc;
import org.jphototagger.program.model.TableModelXmp;

import java.util.Set;

/**
 * Contains all metadata table models.
 *
 * @author Elmar Baumann
 */
public final class MetadataTableModels {
    private TableModelIptc     iptcTableModel;
    private TableModelExif     exifTableModel;
    private TableModelXmp      xmpTableModelDc;
    private TableModelXmp      xmpTableModelExif;
    private TableModelXmp      xmpTableModelIptc;
    private TableModelXmp      xmpTableModelLightroom;
    private TableModelXmp      xmpTableModelPhotoshop;
    private TableModelXmp      xmpTableModelTiff;
    private TableModelXmp      xmpTableModelCameraRawSettings;
    private TableModelXmp      xmpTableModelXap;
    private Set<TableModelXmp> xmpTableModels;

    /**
     * @return the iptcTableModel
     */
    public TableModelIptc getIptcTableModel() {
        return iptcTableModel;
    }

    /**
     * @param iptcTableModel the iptcTableModel to set
     */
    public void setIptcTableModel(TableModelIptc iptcTableModel) {
        if (iptcTableModel == null) {
            throw new NullPointerException("iptcTableModel == null");
        }

        this.iptcTableModel = iptcTableModel;
    }

    /**
     * @return the exifTableModel
     */
    public TableModelExif getExifTableModel() {
        return exifTableModel;
    }

    /**
     * @param exifTableModel the exifTableModel to set
     */
    public void setExifTableModel(TableModelExif exifTableModel) {
        if (exifTableModel == null) {
            throw new NullPointerException("exifTableModel == null");
        }

        this.exifTableModel = exifTableModel;
    }

    /**
     * @return the xmpTableModelDc
     */
    public TableModelXmp getXmpTableModelDc() {
        return xmpTableModelDc;
    }

    /**
     * @param xmpTableModelDc the xmpTableModelDc to set
     */
    public void setXmpTableModelDc(TableModelXmp xmpTableModelDc) {
        if (xmpTableModelDc == null) {
            throw new NullPointerException("xmpTableModelDc == null");
        }

        this.xmpTableModelDc = xmpTableModelDc;
    }

    /**
     * @return the xmpTableModelExif
     */
    public TableModelXmp getXmpTableModelExif() {
        return xmpTableModelExif;
    }

    /**
     * @param xmpTableModelExif the xmpTableModelExif to set
     */
    public void setXmpTableModelExif(TableModelXmp xmpTableModelExif) {
        if (xmpTableModelExif == null) {
            throw new NullPointerException("xmpTableModelExif == null");
        }

        this.xmpTableModelExif = xmpTableModelExif;
    }

    /**
     * @return the xmpTableModelIptc
     */
    public TableModelXmp getXmpTableModelIptc() {
        return xmpTableModelIptc;
    }

    /**
     * @param xmpTableModelIptc the xmpTableModelIptc to set
     */
    public void setXmpTableModelIptc(TableModelXmp xmpTableModelIptc) {
        if (xmpTableModelIptc == null) {
            throw new NullPointerException("xmpTableModelIptc == null");
        }

        this.xmpTableModelIptc = xmpTableModelIptc;
    }

    /**
     * @return the xmpTableModelLightroom
     */
    public TableModelXmp getXmpTableModelLightroom() {
        return xmpTableModelLightroom;
    }

    /**
     * @param xmpTableModelLightroom the xmpTableModelLightroom to set
     */
    public void setXmpTableModelLightroom(
            TableModelXmp xmpTableModelLightroom) {
        if (xmpTableModelLightroom == null) {
            throw new NullPointerException("xmpTableModelLightroom == null");
        }

        this.xmpTableModelLightroom = xmpTableModelLightroom;
    }

    /**
     * @return the xmpTableModelPhotoshop
     */
    public TableModelXmp getXmpTableModelPhotoshop() {
        return xmpTableModelPhotoshop;
    }

    /**
     * @param xmpTableModelPhotoshop the xmpTableModelPhotoshop to set
     */
    public void setXmpTableModelPhotoshop(
            TableModelXmp xmpTableModelPhotoshop) {
        if (xmpTableModelPhotoshop == null) {
            throw new NullPointerException("xmpTableModelPhotoshop == null");
        }

        this.xmpTableModelPhotoshop = xmpTableModelPhotoshop;
    }

    /**
     * @return the xmpTableModelTiff
     */
    public TableModelXmp getXmpTableModelTiff() {
        return xmpTableModelTiff;
    }

    /**
     * @param xmpTableModelTiff the xmpTableModelTiff to set
     */
    public void setXmpTableModelTiff(TableModelXmp xmpTableModelTiff) {
        if (xmpTableModelTiff == null) {
            throw new NullPointerException("xmpTableModelTiff == null");
        }

        this.xmpTableModelTiff = xmpTableModelTiff;
    }

    /**
     * @return the xmpTableModelCameraRawSettings
     */
    public TableModelXmp getXmpTableModelCameraRawSettings() {
        return xmpTableModelCameraRawSettings;
    }

    /**
     * @param xmpTableModelCameraRawSettings the xmpTableModelCameraRawSettings
     *        to set
     */
    public void setXmpTableModelCameraRawSettings(
            TableModelXmp xmpTableModelCameraRawSettings) {
        if (xmpTableModelCameraRawSettings == null) {
            throw new NullPointerException("xmpTableModelCameraRawSettings == null");
        }

        this.xmpTableModelCameraRawSettings = xmpTableModelCameraRawSettings;
    }

    /**
     * @return the xmpTableModelXap
     */
    public TableModelXmp getXmpTableModelXap() {
        return xmpTableModelXap;
    }

    /**
     * @param xmpTableModelXap the xmpTableModelXap to set
     */
    public void setXmpTableModelXap(TableModelXmp xmpTableModelXap) {
        if (xmpTableModelXap == null) {
            throw new NullPointerException("xmpTableModelXap == null");
        }

        this.xmpTableModelXap = xmpTableModelXap;
    }

    /**
     * @return the xmpTableModels
     */
    public Set<TableModelXmp> getXmpTableModels() {
        return Collections.unmodifiableSet(xmpTableModels);
    }

    /**
     * @param xmpTableModels the xmpTableModels to set
     */
    public void setXmpTableModels(Set<TableModelXmp> xmpTableModels) {
        if (xmpTableModels == null) {
            throw new NullPointerException("xmpTableModels == null");
        }

        this.xmpTableModels = new HashSet<TableModelXmp>(xmpTableModels);
    }
}
