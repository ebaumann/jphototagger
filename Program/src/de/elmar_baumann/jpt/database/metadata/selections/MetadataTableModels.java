/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.database.metadata.selections;

import de.elmar_baumann.jpt.model.TableModelExif;
import de.elmar_baumann.jpt.model.TableModelIptc;
import de.elmar_baumann.jpt.model.TableModelXmp;
import java.util.Set;

/**
 * Contains all metadata table models.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-25
 */
public final class MetadataTableModels {

    private TableModelIptc iptcTableModel;
    private TableModelExif exifTableModel;
    private TableModelXmp xmpTableModelDc;
    private TableModelXmp xmpTableModelExif;
    private TableModelXmp xmpTableModelIptc;
    private TableModelXmp xmpTableModelLightroom;
    private TableModelXmp xmpTableModelPhotoshop;
    private TableModelXmp xmpTableModelTiff;
    private TableModelXmp xmpTableModelCameraRawSettings;
    private TableModelXmp xmpTableModelXap;
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
    public void setXmpTableModelLightroom(TableModelXmp xmpTableModelLightroom) {
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
    public void setXmpTableModelPhotoshop(TableModelXmp xmpTableModelPhotoshop) {
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
        this.xmpTableModelXap = xmpTableModelXap;
    }

    /**
     * @return the xmpTableModels
     */
    public Set<TableModelXmp> getXmpTableModels() {
        return xmpTableModels;
    }

    /**
     * @param xmpTableModels the xmpTableModels to set
     */
    public void setXmpTableModels(Set<TableModelXmp> xmpTableModels) {
        this.xmpTableModels = xmpTableModels;
    }
}
