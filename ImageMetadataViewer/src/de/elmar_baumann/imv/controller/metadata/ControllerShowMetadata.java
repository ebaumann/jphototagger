package de.elmar_baumann.imv.controller.metadata;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.properties.XMPPropertyInfo;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.MetaDataDisplay;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.model.TableModelExif;
import de.elmar_baumann.imv.model.TableModelIptc;
import de.elmar_baumann.imv.model.TableModelXmp;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.componentutil.TableUtil;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTable;

/**
 * Überprüft, ob die Metadaten <strong>einer</strong> Bilddatei angezeigt werden
 * sollen und zeigt diese im Bedarfsfall an.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerShowMetadata extends Controller
    implements DatabaseListener, ThumbnailsPanelListener {

    private HashMap<TableModelXmp, String[]> namespacesOfXmpTableModel = new HashMap<TableModelXmp, String[]>();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private MetaDataDisplay data = new MetaDataDisplay();

    public ControllerShowMetadata() {
        initData();
        initNamespacesOfXmpTableModelMap();
        listenToActionSources();
    }

    private void initData() {
        data.metadataTables = appPanel.getMetaDataTables();
        data.xmpTables = appPanel.getXmpTables();
        data.iptcTableModel = (TableModelIptc) appPanel.getTableIptc().getModel();
        data.exifTableModel = (TableModelExif) appPanel.getTableExif().getModel();
        data.xmpTableModelDc = (TableModelXmp) appPanel.getTableXmpDc().getModel();
        data.xmpTableModelExif = (TableModelXmp) appPanel.getTableXmpExif().getModel();
        data.xmpTableModelIptc = (TableModelXmp) appPanel.getTableXmpIptc().getModel();
        data.xmpTableModelLightroom = (TableModelXmp) appPanel.getTableXmpLightroom().getModel();
        data.xmpTableModelPhotoshop = (TableModelXmp) appPanel.getTableXmpPhotoshop().getModel();
        data.xmpTableModelTiff = (TableModelXmp) appPanel.getTableXmpTiff().getModel();
        data.xmpTableModelCameraRawSettings = (TableModelXmp) appPanel.getTableXmpCameraRawSettings().getModel();
        data.xmpTableModelXap = (TableModelXmp) appPanel.getTableXmpXap().getModel();
        data.appPanel = appPanel;
        data.thumbnailsPanel = appPanel.getPanelImageFileThumbnails();
        data.editPanelsArray = appPanel.getEditPanelsArray();
        List<JTable> xmpTables = appPanel.getXmpTables();
        List<TableModelXmp> xmpTableModels = new ArrayList<TableModelXmp>();
        for (JTable xmpTable : xmpTables) {
            xmpTableModels.add((TableModelXmp) xmpTable.getModel());
        }
        data.xmpTableModels = xmpTableModels;
        data.labelMetadataFilename = appPanel.getLabelMetadataFilename();
    }

    private void initNamespacesOfXmpTableModelMap() {
        namespacesOfXmpTableModel.put(data.xmpTableModelDc,
            new String[]{XMPConst.NS_DC, XMPConst.NS_DC_DEPRECATED});
        namespacesOfXmpTableModel.put(data.xmpTableModelExif,
            new String[]{
                XMPConst.NS_EXIF,
                XMPConst.NS_EXIF_AUX
            });
        namespacesOfXmpTableModel.put(data.xmpTableModelIptc,
            new String[]{XMPConst.NS_IPTCCORE});
        namespacesOfXmpTableModel.put(data.xmpTableModelLightroom,
            new String[]{"http://ns.adobe.com/lightroom/1.0/"}); // NOI18N;
        namespacesOfXmpTableModel.put(data.xmpTableModelPhotoshop,
            new String[]{XMPConst.NS_PHOTOSHOP});
        namespacesOfXmpTableModel.put(data.xmpTableModelTiff,
            new String[]{XMPConst.NS_TIFF});
        namespacesOfXmpTableModel.put(data.xmpTableModelCameraRawSettings,
            new String[]{
                XMPConst.NS_CAMERARAW,
                "http://ns.adobe.com/camera-raw-saved-settings/1.0/" // NOI18N
            });
        namespacesOfXmpTableModel.put(data.xmpTableModelXap,
            new String[]{XMPConst.NS_XMP, XMPConst.NS_XMP_RIGHTS});
    }

    private void listenToActionSources() {
        data.thumbnailsPanel.addThumbnailsPanelListener(this);
        Database.getInstance().addDatabaseListener(this);
    }

    @Override
    public void thumbnailSelected(ThumbnailsPanelAction action) {
        if (isStarted()) {
            if (data.thumbnailsPanel.getSelectionCount() == 1) {
                showMetaDataOfFile(data.thumbnailsPanel.getFile(action.getThumbnailIndex()));
            } else {
                emptyMetadata();
            }
        }
    }

    @Override
    public void allThumbnailsDeselected(ThumbnailsPanelAction action) {
        if (isStarted()) {
            emptyMetadata();
        }
    }

    @Override
    public void thumbnailsChanged() {
        // Nichts tun
    }

    private void repaintMetadataTables() {
        for (JTable table : data.metadataTables) {
            repaintComponent(table);
        }
    }

    public void emptyMetadata() {
        removeMetadataFromTables();
        repaintMetadataTables();
        setMetadataFilename(Bundle.getString("ControllerShowMetadata.InformationMessage.MetadataIsShownOnlyIfOneImageIsSelected"));
        data.editPanelsArray.emptyPanels();
    }

    private void resizeMetadataTables() {
        for (JTable table : data.metadataTables) {
            TableUtil.resizeColumnWidthsToFit(table);
        }
    }

    private void removeMetadataFromTables() {
        for (TableModelXmp model : data.xmpTableModels) {
            model.removeAllElements();
        }
        data.iptcTableModel.removeAllElements();
        data.exifTableModel.removeAllElements();
    }

    public void showMetaDataOfFile(File file) {
        removeMetadataFromTables();
        setFileToTableModels(file);
        setXmpModels(file.getAbsolutePath());
        setMetadataFilename(file.getName());
        resizeMetadataTables();
        repaintMetadataTables();
    }

    private void setFileToTableModels(File file) {
        data.iptcTableModel.setFile(file);
        data.exifTableModel.setFile(file);
    }

    private void setXmpModels(String filename) {
        XmpMetadata xmpMetadata = new XmpMetadata();
        List<XMPPropertyInfo> allInfos = xmpMetadata.getPropertyInfosOfFile(filename);
        if (allInfos != null) {
            for (TableModelXmp model : data.xmpTableModels) {
                setPropertyInfosToXmpTableModel(filename,
                    model, allInfos, namespacesOfXmpTableModel.get(model));
            }
        }
    }

    private void setPropertyInfosToXmpTableModel(String filename, TableModelXmp model,
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
        data.labelMetadataFilename.setText(filename);
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        DatabaseAction.Type actionType = action.getType();
        if (isStarted() && (actionType.equals(DatabaseAction.Type.ImageFileInserted) ||
            actionType.equals(DatabaseAction.Type.ImageFileUpdated))) {
            showUpdates(action.getImageFileData().getFile());
        } else if (isStarted() && actionType.equals(DatabaseAction.Type.XmpUpdated)) {
            showUpdates(action.getFile());
        }
    }

    private void showUpdates(File file) {
        if (data.thumbnailsPanel.getSelectionCount() == 1) {
            File selectedFile = data.thumbnailsPanel.getSelectedFiles().get(0);
            if (file.equals(selectedFile)) {
                showMetaDataOfFile(selectedFile);
            }
        }
    }
}
