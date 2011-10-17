package org.jphototagger.program.module.metadata;

import org.jphototagger.program.module.xmp.XmpTableModel;
import org.jphototagger.program.module.exif.ExifTableModel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Elmar Baumann
 */
public final class MetadataTableModels {

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

    public ExifTableModel getExifTableModel() {
        return exifTableModel;
    }

    public void setExifTableModel(ExifTableModel exifTableModel) {
        if (exifTableModel == null) {
            throw new NullPointerException("exifTableModel == null");
        }

        this.exifTableModel = exifTableModel;
    }

    public XmpTableModel getXmpTableModelDc() {
        return xmpTableModelDc;
    }

    public void setXmpTableModelDc(XmpTableModel xmpTableModelDc) {
        if (xmpTableModelDc == null) {
            throw new NullPointerException("xmpTableModelDc == null");
        }

        this.xmpTableModelDc = xmpTableModelDc;
    }

    public XmpTableModel getXmpTableModelExif() {
        return xmpTableModelExif;
    }

    public void setXmpTableModelExif(XmpTableModel xmpTableModelExif) {
        if (xmpTableModelExif == null) {
            throw new NullPointerException("xmpTableModelExif == null");
        }

        this.xmpTableModelExif = xmpTableModelExif;
    }

    public XmpTableModel getXmpTableModelIptc() {
        return xmpTableModelIptc;
    }

    public void setXmpTableModelIptc(XmpTableModel xmpTableModelIptc) {
        if (xmpTableModelIptc == null) {
            throw new NullPointerException("xmpTableModelIptc == null");
        }

        this.xmpTableModelIptc = xmpTableModelIptc;
    }

    public XmpTableModel getXmpTableModelLightroom() {
        return xmpTableModelLightroom;
    }

    public void setXmpTableModelLightroom(XmpTableModel xmpTableModelLightroom) {
        if (xmpTableModelLightroom == null) {
            throw new NullPointerException("xmpTableModelLightroom == null");
        }

        this.xmpTableModelLightroom = xmpTableModelLightroom;
    }

    public XmpTableModel getXmpTableModelPhotoshop() {
        return xmpTableModelPhotoshop;
    }

    public void setXmpTableModelPhotoshop(XmpTableModel xmpTableModelPhotoshop) {
        if (xmpTableModelPhotoshop == null) {
            throw new NullPointerException("xmpTableModelPhotoshop == null");
        }

        this.xmpTableModelPhotoshop = xmpTableModelPhotoshop;
    }

    public XmpTableModel getXmpTableModelTiff() {
        return xmpTableModelTiff;
    }

    public void setXmpTableModelTiff(XmpTableModel xmpTableModelTiff) {
        if (xmpTableModelTiff == null) {
            throw new NullPointerException("xmpTableModelTiff == null");
        }

        this.xmpTableModelTiff = xmpTableModelTiff;
    }

    public XmpTableModel getXmpTableModelCameraRawSettings() {
        return xmpTableModelCameraRawSettings;
    }

    public void setXmpTableModelCameraRawSettings(XmpTableModel xmpTableModelCameraRawSettings) {
        if (xmpTableModelCameraRawSettings == null) {
            throw new NullPointerException("xmpTableModelCameraRawSettings == null");
        }

        this.xmpTableModelCameraRawSettings = xmpTableModelCameraRawSettings;
    }

    public XmpTableModel getXmpTableModelXap() {
        return xmpTableModelXap;
    }

    public void setXmpTableModelXap(XmpTableModel xmpTableModelXap) {
        if (xmpTableModelXap == null) {
            throw new NullPointerException("xmpTableModelXap == null");
        }

        this.xmpTableModelXap = xmpTableModelXap;
    }

    public Set<XmpTableModel> getXmpTableModels() {
        return Collections.unmodifiableSet(xmpTableModels);
    }

    public void setXmpTableModels(Set<XmpTableModel> xmpTableModels) {
        if (xmpTableModels == null) {
            throw new NullPointerException("xmpTableModels == null");
        }

        this.xmpTableModels = new HashSet<XmpTableModel>(xmpTableModels);
    }
}
