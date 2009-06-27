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
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.componentutil.TableUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

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
    private final MetadataDisplay metadataDisplay = new MetadataDisplay();

    public ControllerShowMetadata() {
        initData();
        initNamespacesOfXmpTableModelMap();
        listen();
    }

    private void listen() {
        metadataDisplay.getThumbnailsPanel().addThumbnailsPanelListener(this);
        DatabaseImageFiles.INSTANCE.addDatabaseListener(this);
    }

    private void initData() {
        metadataDisplay.setMetadataTables(appPanel.getMetadataTables());
        metadataDisplay.setXmpTables(appPanel.getXmpTables());
        metadataDisplay.setIptcTableModel(
                (TableModelIptc) appPanel.getTableIptc().getModel());
        metadataDisplay.setExifTableModel(
                (TableModelExif) appPanel.getTableExif().getModel());
        metadataDisplay.setXmpTableModelDc((TableModelXmp) appPanel.
                getTableXmpDc().
                getModel());
        metadataDisplay.setXmpTableModelExif((TableModelXmp) appPanel.
                getTableXmpExif().
                getModel());
        metadataDisplay.setXmpTableModelIptc((TableModelXmp) appPanel.
                getTableXmpIptc().
                getModel());
        metadataDisplay.setXmpTableModelLightroom((TableModelXmp) appPanel.
                getTableXmpLightroom().getModel());
        metadataDisplay.setXmpTableModelPhotoshop((TableModelXmp) appPanel.
                getTableXmpPhotoshop().getModel());
        metadataDisplay.setXmpTableModelTiff((TableModelXmp) appPanel.
                getTableXmpTiff().
                getModel());
        metadataDisplay.setXmpTableModelCameraRawSettings((TableModelXmp) appPanel.
                getTableXmpCameraRawSettings().getModel());
        metadataDisplay.setXmpTableModelXap((TableModelXmp) appPanel.
                getTableXmpXap().
                getModel());
        metadataDisplay.setAppPanel(appPanel);
        metadataDisplay.setThumbnailsPanel(appPanel.getPanelThumbnails());
        metadataDisplay.setEditPanelsArray(appPanel.getEditPanelsArray());
        List<JTable> xmpTables = appPanel.getXmpTables();
        List<TableModelXmp> xmpTableModels = new ArrayList<TableModelXmp>();
        for (JTable xmpTable : xmpTables) {
            xmpTableModels.add((TableModelXmp) xmpTable.getModel());
        }
        metadataDisplay.setXmpTableModels(xmpTableModels);
        metadataDisplay.setLabelMetadataFilename(appPanel.
                getLabelMetadataFilename());
    }

    private void initNamespacesOfXmpTableModelMap() {
        namespacesOfXmpTableModel.put(metadataDisplay.getXmpTableModelDc(),
                new String[]{XMPConst.NS_DC, XMPConst.NS_DC_DEPRECATED});
        namespacesOfXmpTableModel.put(metadataDisplay.getXmpTableModelExif(),
                new String[]{
                    XMPConst.NS_EXIF,
                    XMPConst.NS_EXIF_AUX
                });
        namespacesOfXmpTableModel.put(metadataDisplay.getXmpTableModelIptc(),
                new String[]{XMPConst.NS_IPTCCORE});
        namespacesOfXmpTableModel.put(
                metadataDisplay.getXmpTableModelLightroom(),
                new String[]{"http://ns.adobe.com/lightroom/1.0/"}); // NOI18N;
        namespacesOfXmpTableModel.put(
                metadataDisplay.getXmpTableModelPhotoshop(),
                new String[]{XMPConst.NS_PHOTOSHOP});
        namespacesOfXmpTableModel.put(metadataDisplay.getXmpTableModelTiff(),
                new String[]{XMPConst.NS_TIFF});
        namespacesOfXmpTableModel.put(metadataDisplay.
                getXmpTableModelCameraRawSettings(),
                new String[]{
                    XMPConst.NS_CAMERARAW,
                    "http://ns.adobe.com/camera-raw-saved-settings/1.0/" // NOI18N
                });
        namespacesOfXmpTableModel.put(metadataDisplay.getXmpTableModelXap(),
                new String[]{XMPConst.NS_XMP, XMPConst.NS_XMP_RIGHTS});
    }

    @Override
    public void selectionChanged(ThumbnailsPanelEvent action) {
        if (metadataDisplay.getThumbnailsPanel().getSelectionCount() == 1) {
            SwingUtilities.invokeLater(new ShowMetadata(
                    metadataDisplay.getThumbnailsPanel().getFile(
                    action.getThumbnailIndex())));
        } else {
            SwingUtilities.invokeLater(new RemoveMetadata());
        }
    }

    @Override
    public void thumbnailsChanged() {
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
        if (metadataDisplay.getThumbnailsPanel().getSelectionCount() == 1) {
            File selectedFile =
                    metadataDisplay.getThumbnailsPanel().getSelectedFiles().get(
                    0);
            if (file.equals(selectedFile)) {
                SwingUtilities.invokeLater(new ShowMetadata(file));
            }
        }
    }

    private class RemoveMetadata implements Runnable {

        @Override
        public void run() {
            removeMetadataFromTables();
            repaintMetadataTables();
            metadataDisplay.getLabelMetadataFilename().setText(
                    Bundle.getString(
                    "ControllerShowMetadata.InformationMessage.MetadataIsShownOnlyIfOneImageIsSelected"));
            metadataDisplay.getEditPanelsArray().emptyPanels(false);
        }
    }

    private class ShowMetadata implements Runnable {

        private final File file;

        public ShowMetadata(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            removeMetadataFromTables();
            setFileToTableModels(file);
            setXmpModels(file.getAbsolutePath());
            metadataDisplay.getLabelMetadataFilename().setText(file.getName());
            resizeMetadataTables();
            repaintMetadataTables();
        }

        private void resizeMetadataTables() {
            for (JTable table : metadataDisplay.getMetadataTables()) {
                TableUtil.resizeColumnWidthsToFit(table);
            }
        }

        private void setFileToTableModels(File file) {
            metadataDisplay.getIptcTableModel().setFile(file);
            metadataDisplay.getExifTableModel().setFile(file);
        }

        private void setXmpModels(String filename) {
            List<XMPPropertyInfo> allInfos = XmpMetadata.getPropertyInfosOfFile(
                    filename);
            if (allInfos != null) {
                for (TableModelXmp model : metadataDisplay.getXmpTableModels()) {
                    setPropertyInfosToXmpTableModel(filename,
                            model, allInfos,
                            namespacesOfXmpTableModel.get(model));
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
    }

    private void repaintMetadataTables() {
        for (JTable table : metadataDisplay.getMetadataTables()) {
            ComponentUtil.forceRepaint(table);
        }
    }

    private void removeMetadataFromTables() {
        for (TableModelXmp model : metadataDisplay.getXmpTableModels()) {
            model.removeAllElements();
        }
        metadataDisplay.getIptcTableModel().removeAllElements();
        metadataDisplay.getExifTableModel().removeAllElements();
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // nothing to do
    }
}
