package de.elmar_baumann.imv.controller.metadata;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.properties.XMPPropertyInfo;
import de.elmar_baumann.imv.data.MetadataDisplay;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.imv.event.ThumbnailsPanelEvent;
import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.model.TableModelExif;
import de.elmar_baumann.imv.model.TableModelIptc;
import de.elmar_baumann.imv.model.TableModelXmp;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.componentutil.TableUtil;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;

/**
 * Überprüft, ob die Metadaten <strong>einer</strong> Bilddatei angezeigt werden
 * sollen und zeigt diese im Bedarfsfall an.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerShowMetadata implements DatabaseListener,
                                                     ThumbnailsPanelListener {

    private final Map<TableModelXmp, String[]> namespacesOfXmpTableModel =
            new HashMap<TableModelXmp, String[]>();
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final MetadataDisplay data = new MetadataDisplay();

    public ControllerShowMetadata() {
        initData();
        initNamespacesOfXmpTableModelMap();
        listen();
    }

    private void listen() {
        data.getThumbnailsPanel().addThumbnailsPanelListener(this);
        DatabaseImageFiles.INSTANCE.addDatabaseListener(this);
    }

    private void initData() {
        data.setMetadataTables(appPanel.getMetadataTables());
        data.setXmpTables(appPanel.getXmpTables());
        data.setIptcTableModel(
                (TableModelIptc) appPanel.getTableIptc().getModel());
        data.setExifTableModel(
                (TableModelExif) appPanel.getTableExif().getModel());
        data.setXmpTableModelDc((TableModelXmp) appPanel.getTableXmpDc().
                getModel());
        data.setXmpTableModelExif((TableModelXmp) appPanel.getTableXmpExif().
                getModel());
        data.setXmpTableModelIptc((TableModelXmp) appPanel.getTableXmpIptc().
                getModel());
        data.setXmpTableModelLightroom((TableModelXmp) appPanel.
                getTableXmpLightroom().getModel());
        data.setXmpTableModelPhotoshop((TableModelXmp) appPanel.
                getTableXmpPhotoshop().getModel());
        data.setXmpTableModelTiff((TableModelXmp) appPanel.getTableXmpTiff().
                getModel());
        data.setXmpTableModelCameraRawSettings((TableModelXmp) appPanel.
                getTableXmpCameraRawSettings().getModel());
        data.setXmpTableModelXap((TableModelXmp) appPanel.getTableXmpXap().
                getModel());
        data.setAppPanel(appPanel);
        data.setThumbnailsPanel(appPanel.getPanelThumbnails());
        data.setEditPanelsArray(appPanel.getEditPanelsArray());
        List<JTable> xmpTables = appPanel.getXmpTables();
        List<TableModelXmp> xmpTableModels = new ArrayList<TableModelXmp>();
        for (JTable xmpTable : xmpTables) {
            xmpTableModels.add((TableModelXmp) xmpTable.getModel());
        }
        data.setXmpTableModels(xmpTableModels);
        data.setLabelMetadataFilename(appPanel.getLabelMetadataFilename());
    }

    private void initNamespacesOfXmpTableModelMap() {
        namespacesOfXmpTableModel.put(data.getXmpTableModelDc(),
                new String[]{XMPConst.NS_DC, XMPConst.NS_DC_DEPRECATED});
        namespacesOfXmpTableModel.put(data.getXmpTableModelExif(),
                new String[]{
                    XMPConst.NS_EXIF,
                    XMPConst.NS_EXIF_AUX
                });
        namespacesOfXmpTableModel.put(data.getXmpTableModelIptc(),
                new String[]{XMPConst.NS_IPTCCORE});
        namespacesOfXmpTableModel.put(data.getXmpTableModelLightroom(),
                new String[]{"http://ns.adobe.com/lightroom/1.0/"}); // NOI18N;
        namespacesOfXmpTableModel.put(data.getXmpTableModelPhotoshop(),
                new String[]{XMPConst.NS_PHOTOSHOP});
        namespacesOfXmpTableModel.put(data.getXmpTableModelTiff(),
                new String[]{XMPConst.NS_TIFF});
        namespacesOfXmpTableModel.put(data.getXmpTableModelCameraRawSettings(),
                new String[]{
                    XMPConst.NS_CAMERARAW,
                    "http://ns.adobe.com/camera-raw-saved-settings/1.0/" // NOI18N
                });
        namespacesOfXmpTableModel.put(data.getXmpTableModelXap(),
                new String[]{XMPConst.NS_XMP, XMPConst.NS_XMP_RIGHTS});
    }

    @Override
    public void selectionChanged(ThumbnailsPanelEvent action) {
        if (data.getThumbnailsPanel().getSelectionCount() == 1) {
            showMetadataOfFile(data.getThumbnailsPanel().getFile(action.
                    getThumbnailIndex()));
        } else {
            emptyMetadata();
        }
    }

    @Override
    public void thumbnailsChanged() {
    }

    private void repaintMetadataTables() {
        for (JTable table : data.getMetadataTables()) {
            repaintComponent(table);
        }
    }

    public void emptyMetadata() {
        removeMetadataFromTables();
        repaintMetadataTables();
        setMetadataFilename(
                Bundle.getString(
                "ControllerShowMetadata.InformationMessage.MetadataIsShownOnlyIfOneImageIsSelected"));
        data.getEditPanelsArray().emptyPanels(false);
    }

    private void resizeMetadataTables() {
        for (JTable table : data.getMetadataTables()) {
            TableUtil.resizeColumnWidthsToFit(table);
        }
    }

    private void removeMetadataFromTables() {
        for (TableModelXmp model : data.getXmpTableModels()) {
            model.removeAllElements();
        }
        data.getIptcTableModel().removeAllElements();
        data.getExifTableModel().removeAllElements();
    }

    public void showMetadataOfFile(File file) {
        removeMetadataFromTables();
        setFileToTableModels(file);
        setXmpModels(file.getAbsolutePath());
        setMetadataFilename(file.getName());
        resizeMetadataTables();
        repaintMetadataTables();
    }

    private void setFileToTableModels(File file) {
        data.getIptcTableModel().setFile(file);
        data.getExifTableModel().setFile(file);
    }

    private void setXmpModels(String filename) {
        List<XMPPropertyInfo> allInfos = XmpMetadata.getPropertyInfosOfFile(
                filename);
        if (allInfos != null) {
            for (TableModelXmp model : data.getXmpTableModels()) {
                setPropertyInfosToXmpTableModel(filename,
                        model, allInfos, namespacesOfXmpTableModel.get(model));
            }
        }
    }

    private void setPropertyInfosToXmpTableModel(String filename,
            TableModelXmp model,
            List<XMPPropertyInfo> allInfos, String[] namespaces) {
        List<XMPPropertyInfo> infos = new ArrayList<XMPPropertyInfo>();
        for (int index = 0; index < namespaces.length; index++) {
            infos.addAll(XmpMetadata.getPropertyInfosOfNamespace(allInfos,
                    namespaces[index]));
        }
        model.setPropertyInfosOfFile(filename, infos);
    }

    private void repaintComponent(Component component) {
        component.invalidate();
        component.validate();
        component.repaint();
    }

    private void setMetadataFilename(String filename) {
        data.getLabelMetadataFilename().setText(filename);
    }

    @Override
    public void actionPerformed(DatabaseImageEvent event) {
        DatabaseImageEvent.Type eventType = event.getType();
        if ((eventType.equals(DatabaseImageEvent.Type.IMAGEFILE_INSERTED) ||
                eventType.equals(DatabaseImageEvent.Type.IMAGEFILE_UPDATED))) {
            showUpdates(event.getImageFile().getFile());
        } else if (eventType.equals(DatabaseImageEvent.Type.XMP_UPDATED)) {
            showUpdates(event.getImageFile().getFile());
        }
    }

    private void showUpdates(File file) {
        if (data.getThumbnailsPanel().getSelectionCount() == 1) {
            File selectedFile =
                    data.getThumbnailsPanel().getSelectedFiles().get(0);
            if (file.equals(selectedFile)) {
                showMetadataOfFile(selectedFile);
            }
        }
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // nothing to do
    }
}
