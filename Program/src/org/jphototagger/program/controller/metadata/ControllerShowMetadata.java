package org.jphototagger.program.controller.metadata;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.adobe.xmp.XMPConst;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.componentutil.TableUtil;
import org.jphototagger.program.cache.EmbeddedXmpCache;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.selections.MetadataTableModels;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.model.TableModelExif;
import org.jphototagger.program.model.TableModelIptc;
import org.jphototagger.program.model.TableModelXmp;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.WaitDisplay;


import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.event.ChangeListener;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens for selection changes in the {@link ThumbnailsPanel} and
 * displays metadata in the metadata tables if <strong>one</strong> thumbnail
 * was selected. If multiple thumbnails or no thumbnail were selected, this
 * controller empties the metadata tables.
 *
 * Listens also to the {@link DatabaseImageFiles} and refreshes the displayed
 * metadata of a file if that file was changed in the database.
 *
 * @author Elmar Baumann
 */
public final class ControllerShowMetadata implements ChangeListener, DatabaseImageFilesListener, ThumbnailsPanelListener {
    private static final Logger LOGGER = Logger.getLogger(ControllerShowMetadata.class.getName());
    private final Map<TableModelXmp, String[]> namespacesOfXmpTableModel = new HashMap<TableModelXmp, String[]>();
    private final MetadataTableModels metadataTableModels = new MetadataTableModels();
    private final AppPanel appPanel = GUI.getAppPanel();
    private final ThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private final JTabbedPane metadataPane = appPanel.getTabbedPaneMetadata();
    private File selectedImageFile;
    private boolean exifReadFromImageFile;
    private boolean iptcReadFromImageFile;
    private boolean xmpReadFromImageFile;

    public ControllerShowMetadata() {
        initMetadatModels();
        initNamespacesOfXmpTableModelMap();
        listen();
    }

    private enum Metadata { EXIF, IPTC, XMP; }

    private void listen() {
        GUI.getThumbnailsPanel().addThumbnailsPanelListener(this);
        DatabaseImageFiles.INSTANCE.addListener(this);
        metadataPane.addChangeListener(this);
    }

    private void initMetadatModels() {
        metadataTableModels.setIptcTableModel((TableModelIptc) appPanel.getTableIptc().getModel());
        metadataTableModels.setExifTableModel((TableModelExif) appPanel.getTableExif().getModel());
        metadataTableModels.setXmpTableModelDc((TableModelXmp) appPanel.getTableXmpDc().getModel());
        metadataTableModels.setXmpTableModelExif((TableModelXmp) appPanel.getTableXmpExif().getModel());
        metadataTableModels.setXmpTableModelIptc((TableModelXmp) appPanel.getTableXmpIptc().getModel());
        metadataTableModels.setXmpTableModelLightroom((TableModelXmp) appPanel.getTableXmpLightroom().getModel());
        metadataTableModels.setXmpTableModelPhotoshop((TableModelXmp) appPanel.getTableXmpPhotoshop().getModel());
        metadataTableModels.setXmpTableModelTiff((TableModelXmp) appPanel.getTableXmpTiff().getModel());
        metadataTableModels.setXmpTableModelCameraRawSettings((TableModelXmp) appPanel.getTableXmpCameraRawSettings().getModel());
        metadataTableModels.setXmpTableModelXap((TableModelXmp) appPanel.getTableXmpXap().getModel());

        List<JTable> xmpTables = appPanel.getXmpTables();
        Set<TableModelXmp> xmpTableModels = new HashSet<TableModelXmp>();

        for (JTable xmpTable : xmpTables) {
            xmpTableModels.add((TableModelXmp) xmpTable.getModel());
        }

        metadataTableModels.setXmpTableModels(xmpTableModels);
    }

    private void initNamespacesOfXmpTableModelMap() {
        namespacesOfXmpTableModel.put(metadataTableModels
                .getXmpTableModelDc(),
                new String[] { XMPConst.NS_DC, XMPConst.NS_DC_DEPRECATED });
        namespacesOfXmpTableModel.put(metadataTableModels
                .getXmpTableModelExif(),
                new String[] { XMPConst.NS_EXIF, XMPConst.NS_EXIF_AUX });
        namespacesOfXmpTableModel.put(metadataTableModels
                .getXmpTableModelIptc(),
                new String[] { XMPConst.NS_IPTCCORE });
        namespacesOfXmpTableModel.put(metadataTableModels
                .getXmpTableModelLightroom(),
                new String[] { "http://ns.adobe.com/lightroom/1.0/" });
        namespacesOfXmpTableModel.put(metadataTableModels
                .getXmpTableModelPhotoshop(),
                new String[] { XMPConst.NS_PHOTOSHOP });
        namespacesOfXmpTableModel.put(metadataTableModels
                .getXmpTableModelTiff(),
                new String[] { XMPConst.NS_TIFF });
        namespacesOfXmpTableModel.put(metadataTableModels
                .getXmpTableModelCameraRawSettings(),
                new String[] { XMPConst.NS_CAMERARAW, "http://ns.adobe.com/camera-raw-saved-settings/1.0/" });
        namespacesOfXmpTableModel.put(metadataTableModels
                .getXmpTableModelXap(),
                new String[] { XMPConst.NS_XMP, XMPConst.NS_XMP_RIGHTS });
    }

    private void setSelectedImageFileUndefined() {
        selectedImageFile = null;
        exifReadFromImageFile = false;
        iptcReadFromImageFile = false;
        xmpReadFromImageFile = false;
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        showUpdates(imageFile, Collections.singleton(Metadata.XMP));
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        showUpdates(imageFile, Collections.singleton(Metadata.XMP));
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        showUpdates(imageFile, Collections.singleton(Metadata.XMP));
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {
        // ignore
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {
        showUpdates(imageFile, Collections.singleton(Metadata.EXIF));
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {
        showUpdates(imageFile, Collections.singleton(Metadata.EXIF));
    }

    @Override
    public void imageFileInserted(File imageFile) {
        // ignore
    }

    @Override
    public void thumbnailsSelectionChanged() {
        setSelectedImageFileUndefined();
        removeMetadataFromTables(EnumSet.allOf(Metadata.class));
        showMetadataOfSelectedThumbnails();
    }

    @Override
    public void thumbnailsChanged() {
        // ignore
    }

    @Override
    public void imageFileDeleted(File imageFile) {
        // ignore
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {
        // ignore
    }

    @Override
    public void thumbnailUpdated(File imageFile) {
        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {
        // ignore
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {
        // ignore
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (source == metadataPane && isExactlyOneThumbnailSelected()) {
            EventQueueUtil.invokeInDispatchThread(new ShowMetadata(EnumSet.allOf(Metadata.class)));
        }
    }

    private boolean isExactlyOneThumbnailSelected() {
        List<File> selectedFiles = thumbnailsPanel.getSelectedFiles();

        return selectedFiles.size() == 1;
    }

    private boolean isUpdateExif(Collection<? extends Metadata> metadata) {
        return metadata.contains(Metadata.EXIF)
                && !exifReadFromImageFile
                && appPanel.isTabMetadataExifSelected();
    }

    private boolean isUpdateIptc(Collection<? extends Metadata> metadata) {
        return metadata.contains(Metadata.IPTC)
                && !iptcReadFromImageFile
                && appPanel.isTabMetadataIptcSelected()
                && UserSettings.INSTANCE.isDisplayIptc();
    }

    private boolean isUpdateXmp(Collection<? extends Metadata> metadata) {
        return metadata.contains(Metadata.XMP)
                && !xmpReadFromImageFile
                && appPanel.isTabMetadataXmpSelected();
    }

    private void showMetadataOfSelectedThumbnails() {
        List<File> selectedFiles = thumbnailsPanel.getSelectedFiles();

        if (selectedFiles.size() == 1) {
            selectedImageFile = selectedFiles.get(0);
            EventQueueUtil.invokeInDispatchThread(new ShowMetadata(EnumSet.allOf(Metadata.class)));
        } else {
            removeDisplayedMetadata();
        }
    }

    private void removeDisplayedMetadata() {
        appPanel.getButtonIptcToXmp().setEnabled(false);
        appPanel.getButtonExifToXmp().setEnabled(false);
        EventQueueUtil.invokeInDispatchThread(new RemoveAllMetadata());
    }

    private void showUpdates(File updatedImageFile, Collection<? extends Metadata> metadata) {
        if (selectedImageFile != null && updatedImageFile.equals(selectedImageFile)) {
            EventQueueUtil.invokeInDispatchThread(new ShowMetadata(metadata));
        }
    }

    private void resizeMetadataTables(Collection<? extends Metadata> metadata) {
        if (metadata.contains(Metadata.EXIF)) {
            LOGGER.log(Level.FINEST, "Resizing EXIF metadata GUI table");
            resizeTables(Collections.singleton(appPanel.getTableExif()));
        }

        if (metadata.contains(Metadata.IPTC)) {
            LOGGER.log(Level.FINEST, "Resizing IPTC metadata GUI table");
            resizeTables(Collections.singleton(appPanel.getTableIptc()));
        }

        if (metadata.contains(Metadata.XMP)) {
            LOGGER.log(Level.FINEST, "Resizing XMP metadata GUI tables");
            resizeTables(appPanel.getXmpTables());
        }
    }

    private void resizeTables(Collection<? extends JTable> tables) {
        for (JTable table : tables) {
            TableUtil.resizeColumnWidthsToFit(table);
        }
    }

    private void repaintMetadataTables(Collection<? extends Metadata> metadata) {
        if (metadata.contains(Metadata.EXIF)) {
            LOGGER.log(Level.FINEST, "Repainting EXIF metadata GUI table");
            repaintTables(Collections.singleton(appPanel.getTableExif()));
        }

        if (metadata.contains(Metadata.IPTC)) {
            LOGGER.log(Level.FINEST, "Repainting IPTC metadata GUI table");
            repaintTables(Collections.singleton(appPanel.getTableIptc()));
        }

        if (metadata.contains(Metadata.XMP)) {
            LOGGER.log(Level.FINEST, "Repainting XMP metadata GUI tables");
            repaintTables(appPanel.getXmpTables());
        }
    }

    private void repaintTables(Collection<? extends JTable> tables) {
        for (JTable table : tables) {
            ComponentUtil.forceRepaint(table);
        }
    }

    private void removeMetadataFromTables(Collection<? extends Metadata> metadata) {
        if (metadata.contains(Metadata.XMP)) {
            for (TableModelXmp model : metadataTableModels.getXmpTableModels()) {
                model.removeAllRows();
            }
        }

        if (metadata.contains(Metadata.EXIF)) {
            metadataTableModels.getExifTableModel().removeAllRows();
        }
    }

    private class RemoveAllMetadata implements Runnable {
        @Override
        public void run() {
            Set<Metadata> allMetadata = EnumSet.allOf(Metadata.class);

            removeMetadataFromTables(allMetadata);
            repaintMetadataTables(allMetadata);
            appPanel.getLabelMetadataFilename().setText(
                JptBundle.INSTANCE.getString("ControllerShowMetadata.Info.MetadataIsShownOnlyIfOneImageIsSelected"));
        }
    }

    private class ShowMetadata implements Runnable {
        private final Collection<? extends Metadata> metadata;
        private final File imageFile = selectedImageFile;

        ShowMetadata(Collection<? extends Metadata> metadata) {
            this.metadata = metadata;
        }

        @Override
        public void run() {
            if (imageFile == null) {
                return;
            }

            WaitDisplay.show();

            Set<Metadata> resizeTableMetadta = new HashSet<Metadata>();

            if (isUpdateExif(metadata)) {
                LOGGER.log(Level.FINEST, "Updating EXIF metadata of image file ''{0}'' in GUI table", imageFile);
                metadataTableModels.getExifTableModel().setFile(imageFile);
                exifReadFromImageFile = true;
                resizeTableMetadta.add(Metadata.EXIF);
            }

            if (isUpdateIptc(metadata)) {
                LOGGER.log(Level.FINEST, "Updating IPTC metadata of image file ''{0}'' in GUI table", imageFile);
                metadataTableModels.getIptcTableModel().setFile(imageFile);
                iptcReadFromImageFile = true;
                appPanel.getButtonIptcToXmp().setEnabled(hasIptcData());
                resizeTableMetadta.add(Metadata.IPTC);
            }

            appPanel.getButtonExifToXmp().setEnabled(hasExifData());

            if (isUpdateXmp(metadata)) {
                LOGGER.log(Level.FINEST, "Updating XMP metadata of image file ''{0}'' in GUI tables", imageFile);
                setXmpModels(imageFile);
                resizeTableMetadta.add(Metadata.XMP);
            }

            appPanel.getLabelMetadataFilename().setText(imageFile.getName() + (XmpMetadata.hasImageASidecarFile(imageFile)
                    ? ""
                    : JptBundle.INSTANCE.getString("ControllerShowMetadata.Embedded")));
            WaitDisplay.hide();
            resizeMetadataTables(resizeTableMetadta);
            repaintMetadataTables(resizeTableMetadta);
        }

        private void setXmpModels(File imageFile) {
            List<XMPPropertyInfo> allInfos = null;
            File sidecarFile = XmpMetadata.getSidecarFile(imageFile);

            try {
                allInfos = (sidecarFile != null)
                               ? XmpMetadata.getPropertyInfosOfSidecarFile(sidecarFile)
                               : UserSettings.INSTANCE.isScanForEmbeddedXmp()
                               ? EmbeddedXmpCache.INSTANCE.getXmpPropertyInfos(imageFile)
                               : null;
                xmpReadFromImageFile = true;
            } catch (Throwable throwable) {
                LOGGER.log(Level.SEVERE, null, throwable);
            }

            if (allInfos != null) {
                for (TableModelXmp model : metadataTableModels.getXmpTableModels()) {
                    setPropertyInfosToXmpTableModel(imageFile, model, allInfos, namespacesOfXmpTableModel.get(model));
                }
            }
        }

        private void setPropertyInfosToXmpTableModel(File imageFile, TableModelXmp model, List<XMPPropertyInfo> allInfos, String[] namespaces) {
            List<XMPPropertyInfo> infos = new ArrayList<XMPPropertyInfo>();

            for (int index = 0; index < namespaces.length; index++) {
                infos.addAll(XmpMetadata.filterPropertyInfosOfNamespace(allInfos, namespaces[index]));
            }

            model.setPropertyInfosOfFile(imageFile, infos);
        }

        private boolean hasIptcData() {
            return metadataTableModels.getIptcTableModel().getRowCount() > 0;
        }

        private boolean hasExifData() {
            return metadataTableModels.getExifTableModel().getRowCount() > 0;
        }
    }
}
