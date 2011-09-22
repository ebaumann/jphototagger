package org.jphototagger.program.controller.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.properties.XMPPropertyInfo;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.repository.event.exif.ExifInsertedEvent;
import org.jphototagger.domain.repository.event.exif.ExifUpdatedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.iptc.IptcPreferencesKeys;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.componentutil.TableUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppPreferencesKeys;
import org.jphototagger.program.model.ExifTableModel;
import org.jphototagger.program.model.IptcTableModel;
import org.jphototagger.program.model.MetadataTableModels;
import org.jphototagger.program.model.XmpTableModel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.WaitDisplay;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.xmp.EmbeddedXmpCache;
import org.jphototagger.xmp.XmpMetadata;

/**
 *
 * @author Elmar Baumann
 */
public final class ShowMetadataController implements ChangeListener {

    private static final Logger LOGGER = Logger.getLogger(ShowMetadataController.class.getName());
    private final Map<XmpTableModel, String[]> namespacesOfXmpTableModel = new HashMap<XmpTableModel, String[]>();
    private final MetadataTableModels metadataTableModels = new MetadataTableModels();
    private final AppPanel appPanel = GUI.getAppPanel();
    private final ThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private final JTabbedPane metadataPane = appPanel.getTabbedPaneMetadata();
    private File selectedImageFile;
    private boolean exifReadFromImageFile;
    private boolean iptcReadFromImageFile;
    private boolean xmpReadFromImageFile;

    public ShowMetadataController() {
        initMetadatModels();
        initNamespacesOfXmpTableModelMap();
        listen();
    }

    private enum Metadata {

        EXIF, IPTC, XMP;
    }

    private void listen() {
        AnnotationProcessor.process(this);
        metadataPane.addChangeListener(this);
    }

    private void initMetadatModels() {
        metadataTableModels.setIptcTableModel((IptcTableModel) appPanel.getTableIptc().getModel());
        metadataTableModels.setExifTableModel((ExifTableModel) appPanel.getTableExif().getModel());
        metadataTableModels.setXmpTableModelDc((XmpTableModel) appPanel.getTableXmpDc().getModel());
        metadataTableModels.setXmpTableModelExif((XmpTableModel) appPanel.getTableXmpExif().getModel());
        metadataTableModels.setXmpTableModelIptc((XmpTableModel) appPanel.getTableXmpIptc().getModel());
        metadataTableModels.setXmpTableModelLightroom((XmpTableModel) appPanel.getTableXmpLightroom().getModel());
        metadataTableModels.setXmpTableModelPhotoshop((XmpTableModel) appPanel.getTableXmpPhotoshop().getModel());
        metadataTableModels.setXmpTableModelTiff((XmpTableModel) appPanel.getTableXmpTiff().getModel());
        metadataTableModels.setXmpTableModelCameraRawSettings((XmpTableModel) appPanel.getTableXmpCameraRawSettings().getModel());
        metadataTableModels.setXmpTableModelXap((XmpTableModel) appPanel.getTableXmpXap().getModel());

        List<JTable> xmpTables = appPanel.getXmpTables();
        Set<XmpTableModel> xmpTableModels = new HashSet<XmpTableModel>();

        for (JTable xmpTable : xmpTables) {
            xmpTableModels.add((XmpTableModel) xmpTable.getModel());
        }

        metadataTableModels.setXmpTableModels(xmpTableModels);
    }

    private void initNamespacesOfXmpTableModelMap() {
        namespacesOfXmpTableModel.put(metadataTableModels.getXmpTableModelDc(),
                new String[]{XMPConst.NS_DC, XMPConst.NS_DC_DEPRECATED});
        namespacesOfXmpTableModel.put(metadataTableModels.getXmpTableModelExif(),
                new String[]{XMPConst.NS_EXIF, XMPConst.NS_EXIF_AUX});
        namespacesOfXmpTableModel.put(metadataTableModels.getXmpTableModelIptc(),
                new String[]{XMPConst.NS_IPTCCORE});
        namespacesOfXmpTableModel.put(metadataTableModels.getXmpTableModelLightroom(),
                new String[]{"http://ns.adobe.com/lightroom/1.0/"});
        namespacesOfXmpTableModel.put(metadataTableModels.getXmpTableModelPhotoshop(),
                new String[]{XMPConst.NS_PHOTOSHOP});
        namespacesOfXmpTableModel.put(metadataTableModels.getXmpTableModelTiff(),
                new String[]{XMPConst.NS_TIFF});
        namespacesOfXmpTableModel.put(metadataTableModels.getXmpTableModelCameraRawSettings(),
                new String[]{XMPConst.NS_CAMERARAW, "http://ns.adobe.com/camera-raw-saved-settings/1.0/"});
        namespacesOfXmpTableModel.put(metadataTableModels.getXmpTableModelXap(),
                new String[]{XMPConst.NS_XMP, XMPConst.NS_XMP_RIGHTS});
    }

    private void setSelectedImageFileUndefined() {
        selectedImageFile = null;
        exifReadFromImageFile = false;
        iptcReadFromImageFile = false;
        xmpReadFromImageFile = false;
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        showUpdates(evt.getImageFile(), Collections.singleton(Metadata.XMP));
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        showUpdates(evt.getImageFile(), Collections.singleton(Metadata.XMP));
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        showUpdates(evt.getImageFile(), Collections.singleton(Metadata.XMP));
    }

    @EventSubscriber(eventClass = ExifUpdatedEvent.class)
    public void exifUpdated(ExifUpdatedEvent evt) {
        showUpdates(evt.getImageFile(), Collections.singleton(Metadata.EXIF));
    }

    @EventSubscriber(eventClass = ExifInsertedEvent.class)
    public void exifInserted(ExifInsertedEvent evt) {
        showUpdates(evt.getImageFile(), Collections.singleton(Metadata.EXIF));
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        setSelectedImageFileUndefined();
        removeMetadataFromTables(EnumSet.allOf(Metadata.class));
        showMetadataOfSelectedThumbnails(evt.getSelectedImageFiles());
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
                && isDisplayIptc();
    }

    private boolean isDisplayIptc() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(IptcPreferencesKeys.KEY_DISPLAY_IPTC)
                ? storage.getBoolean(IptcPreferencesKeys.KEY_DISPLAY_IPTC)
                : false;
    }

    private boolean isUpdateXmp(Collection<? extends Metadata> metadata) {
        return metadata.contains(Metadata.XMP)
                && !xmpReadFromImageFile
                && appPanel.isTabMetadataXmpSelected();
    }

    private void showMetadataOfSelectedThumbnails(List<File> selectedFiles) {
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
            for (XmpTableModel model : metadataTableModels.getXmpTableModels()) {
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
                    Bundle.getString(RemoveAllMetadata.class, "ShowMetadataController.Info.MetadataIsShownOnlyIfOneImageIsSelected"));
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
                    : Bundle.getString(ShowMetadata.class, "ShowMetadataController.Embedded")));
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
                        : isScanForEmbeddedXmp()
                        ? EmbeddedXmpCache.INSTANCE.getXmpPropertyInfos(imageFile)
                        : null;
                xmpReadFromImageFile = true;
            } catch (Throwable throwable) {
                LOGGER.log(Level.SEVERE, null, throwable);
            }

            if (allInfos != null) {
                for (XmpTableModel model : metadataTableModels.getXmpTableModels()) {
                    setPropertyInfosToXmpTableModel(imageFile, model, allInfos, namespacesOfXmpTableModel.get(model));
                }
            }
        }

        private boolean isScanForEmbeddedXmp() {
            Preferences storage = Lookup.getDefault().lookup(Preferences.class);

            return storage.containsKey(AppPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                    ? storage.getBoolean(AppPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                    : false;
        }

        private void setPropertyInfosToXmpTableModel(File imageFile, XmpTableModel model, List<XMPPropertyInfo> allInfos, String[] namespaces) {
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
