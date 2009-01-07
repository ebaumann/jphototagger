package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.model.TableModelExif;
import de.elmar_baumann.imv.model.TableModelIptc;
import de.elmar_baumann.imv.model.TableModelXmp;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * Enthält Objekte zur Anzeige von Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public final class MetadataDisplay {

    private List<JTable> metadataTables;
    private List<JTable> xmpTables;
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
    private AppPanel appPanel;
    private ImageFileThumbnailsPanel thumbnailsPanel;
    private EditMetadataPanelsArray editPanelsArray;
    private List<TableModelXmp> xmpTableModels;
    private JLabel labelMetadataFilename;

    /**
     * @return the metadataTables
     */
    public List<JTable> getMetadataTables() {
        return metadataTables;
    }

    /**
     * @param metadataTables the metadataTables to set
     */
    public void setMetadataTables(List<JTable> metadataTables) {
        this.metadataTables = metadataTables;
    }

    /**
     * @return the xmpTables
     */
    public List<JTable> getXmpTables() {
        return xmpTables;
    }

    /**
     * @param xmpTables the xmpTables to set
     */
    public void setXmpTables(List<JTable> xmpTables) {
        this.xmpTables = xmpTables;
    }

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
     * @param xmpTableModelCameraRawSettings the xmpTableModelCameraRawSettings to set
     */
    public void setXmpTableModelCameraRawSettings(TableModelXmp xmpTableModelCameraRawSettings) {
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
     * @return the appPanel
     */
    public AppPanel getAppPanel() {
        return appPanel;
    }

    /**
     * @param appPanel the appPanel to set
     */
    public void setAppPanel(AppPanel appPanel) {
        this.appPanel = appPanel;
    }

    /**
     * @return the thumbnailsPanel
     */
    public ImageFileThumbnailsPanel getThumbnailsPanel() {
        return thumbnailsPanel;
    }

    /**
     * @param thumbnailsPanel the thumbnailsPanel to set
     */
    public void setThumbnailsPanel(ImageFileThumbnailsPanel thumbnailsPanel) {
        this.thumbnailsPanel = thumbnailsPanel;
    }

    /**
     * @return the editPanelsArray
     */
    public EditMetadataPanelsArray getEditPanelsArray() {
        return editPanelsArray;
    }

    /**
     * @param editPanelsArray the editPanelsArray to set
     */
    public void setEditPanelsArray(EditMetadataPanelsArray editPanelsArray) {
        this.editPanelsArray = editPanelsArray;
    }

    /**
     * @return the xmpTableModels
     */
    public List<TableModelXmp> getXmpTableModels() {
        return xmpTableModels;
    }

    /**
     * @param xmpTableModels the xmpTableModels to set
     */
    public void setXmpTableModels(List<TableModelXmp> xmpTableModels) {
        this.xmpTableModels = xmpTableModels;
    }

    /**
     * @return the labelMetadataFilename
     */
    public JLabel getLabelMetadataFilename() {
        return labelMetadataFilename;
    }

    /**
     * @param labelMetadataFilename the labelMetadataFilename to set
     */
    public void setLabelMetadataFilename(JLabel labelMetadataFilename) {
        this.labelMetadataFilename = labelMetadataFilename;
    }
}
