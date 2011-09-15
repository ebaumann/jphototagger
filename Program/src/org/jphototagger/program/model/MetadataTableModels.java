package org.jphototagger.program.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains all metadata table models.
 *
 * @author Elmar Baumann
 */
public final class MetadataTableModels {

    private IptcTableModel iptcTableModel;
    private ExifTableModel exifTableModel;
    private XmpTableModel xmpTableModelDc;
    private XmpTableModel xmpTableModelExif;
    private XmpTableModel xmpTableModelIptc;
    private XmpTableModel xmpTableModelLightroom;
    private XmpTableModel xmpTableModelPhotoshop;
    private XmpTableModel xmpTableModelTiff;
    private XmpTableModel xmpTableModelCameraRawSettings;
    private XmpTableModel xmpTableModelXap;
    private Set<XmpTableModel> xmpTableModels;

    /**
     * @return the iptcTableModel
     */
    public IptcTableModel getIptcTableModel() {
        return iptcTableModel;
    }

    /**
     * @param iptcTableModel the iptcTableModel to set
     */
    public void setIptcTableModel(IptcTableModel iptcTableModel) {
        if (iptcTableModel == null) {
            throw new NullPointerException("iptcTableModel == null");
        }

        this.iptcTableModel = iptcTableModel;
    }

    /**
     * @return the exifTableModel
     */
    public ExifTableModel getExifTableModel() {
        return exifTableModel;
    }

    /**
     * @param exifTableModel the exifTableModel to set
     */
    public void setExifTableModel(ExifTableModel exifTableModel) {
        if (exifTableModel == null) {
            throw new NullPointerException("exifTableModel == null");
        }

        this.exifTableModel = exifTableModel;
    }

    /**
     * @return the xmpTableModelDc
     */
    public XmpTableModel getXmpTableModelDc() {
        return xmpTableModelDc;
    }

    /**
     * @param xmpTableModelDc the xmpTableModelDc to set
     */
    public void setXmpTableModelDc(XmpTableModel xmpTableModelDc) {
        if (xmpTableModelDc == null) {
            throw new NullPointerException("xmpTableModelDc == null");
        }

        this.xmpTableModelDc = xmpTableModelDc;
    }

    /**
     * @return the xmpTableModelExif
     */
    public XmpTableModel getXmpTableModelExif() {
        return xmpTableModelExif;
    }

    /**
     * @param xmpTableModelExif the xmpTableModelExif to set
     */
    public void setXmpTableModelExif(XmpTableModel xmpTableModelExif) {
        if (xmpTableModelExif == null) {
            throw new NullPointerException("xmpTableModelExif == null");
        }

        this.xmpTableModelExif = xmpTableModelExif;
    }

    /**
     * @return the xmpTableModelIptc
     */
    public XmpTableModel getXmpTableModelIptc() {
        return xmpTableModelIptc;
    }

    /**
     * @param xmpTableModelIptc the xmpTableModelIptc to set
     */
    public void setXmpTableModelIptc(XmpTableModel xmpTableModelIptc) {
        if (xmpTableModelIptc == null) {
            throw new NullPointerException("xmpTableModelIptc == null");
        }

        this.xmpTableModelIptc = xmpTableModelIptc;
    }

    /**
     * @return the xmpTableModelLightroom
     */
    public XmpTableModel getXmpTableModelLightroom() {
        return xmpTableModelLightroom;
    }

    /**
     * @param xmpTableModelLightroom the xmpTableModelLightroom to set
     */
    public void setXmpTableModelLightroom(XmpTableModel xmpTableModelLightroom) {
        if (xmpTableModelLightroom == null) {
            throw new NullPointerException("xmpTableModelLightroom == null");
        }

        this.xmpTableModelLightroom = xmpTableModelLightroom;
    }

    /**
     * @return the xmpTableModelPhotoshop
     */
    public XmpTableModel getXmpTableModelPhotoshop() {
        return xmpTableModelPhotoshop;
    }

    /**
     * @param xmpTableModelPhotoshop the xmpTableModelPhotoshop to set
     */
    public void setXmpTableModelPhotoshop(XmpTableModel xmpTableModelPhotoshop) {
        if (xmpTableModelPhotoshop == null) {
            throw new NullPointerException("xmpTableModelPhotoshop == null");
        }

        this.xmpTableModelPhotoshop = xmpTableModelPhotoshop;
    }

    /**
     * @return the xmpTableModelTiff
     */
    public XmpTableModel getXmpTableModelTiff() {
        return xmpTableModelTiff;
    }

    /**
     * @param xmpTableModelTiff the xmpTableModelTiff to set
     */
    public void setXmpTableModelTiff(XmpTableModel xmpTableModelTiff) {
        if (xmpTableModelTiff == null) {
            throw new NullPointerException("xmpTableModelTiff == null");
        }

        this.xmpTableModelTiff = xmpTableModelTiff;
    }

    /**
     * @return the xmpTableModelCameraRawSettings
     */
    public XmpTableModel getXmpTableModelCameraRawSettings() {
        return xmpTableModelCameraRawSettings;
    }

    /**
     * @param xmpTableModelCameraRawSettings the xmpTableModelCameraRawSettings
     *        to set
     */
    public void setXmpTableModelCameraRawSettings(XmpTableModel xmpTableModelCameraRawSettings) {
        if (xmpTableModelCameraRawSettings == null) {
            throw new NullPointerException("xmpTableModelCameraRawSettings == null");
        }

        this.xmpTableModelCameraRawSettings = xmpTableModelCameraRawSettings;
    }

    /**
     * @return the xmpTableModelXap
     */
    public XmpTableModel getXmpTableModelXap() {
        return xmpTableModelXap;
    }

    /**
     * @param xmpTableModelXap the xmpTableModelXap to set
     */
    public void setXmpTableModelXap(XmpTableModel xmpTableModelXap) {
        if (xmpTableModelXap == null) {
            throw new NullPointerException("xmpTableModelXap == null");
        }

        this.xmpTableModelXap = xmpTableModelXap;
    }

    /**
     * @return the xmpTableModels
     */
    public Set<XmpTableModel> getXmpTableModels() {
        return Collections.unmodifiableSet(xmpTableModels);
    }

    /**
     * @param xmpTableModels the xmpTableModels to set
     */
    public void setXmpTableModels(Set<XmpTableModel> xmpTableModels) {
        if (xmpTableModels == null) {
            throw new NullPointerException("xmpTableModels == null");
        }

        this.xmpTableModels = new HashSet<XmpTableModel>(xmpTableModels);
    }
}
