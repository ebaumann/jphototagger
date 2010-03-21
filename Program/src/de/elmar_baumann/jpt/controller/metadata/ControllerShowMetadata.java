/*
 * @(#)ControllerShowMetadata.java    Created on 2008-10-05
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.controller.metadata;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.adobe.xmp.XMPConst;

import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.selections.MetadataTableModels;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.model.TableModelExif;
import de.elmar_baumann.jpt.model.TableModelIptc;
import de.elmar_baumann.jpt.model.TableModelXmp;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.componentutil.TableUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 * Listens for selection changes in the {@link ThumbnailsPanel} and
 * displays metadata in the metadata tables if <strong>one</strong> thumbnail
 * was selected. If multiple thumbnails or no thumbnail were selected, this
 * controller empties the metadata tables.
 *
 * Listens also to the {@link DatabaseImageFiles} and refreshes the displayed
 * metadata of a file if that file was changed in the database.
 *
 * @author  Elmar Baumann
 */
public final class ControllerShowMetadata
        implements DatabaseImageFilesListener, ThumbnailsPanelListener {
    private final Map<TableModelXmp, String[]> namespacesOfXmpTableModel =
        new HashMap<TableModelXmp, String[]>();
    private final MetadataTableModels metadataTableModels =
        new MetadataTableModels();
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();

    public ControllerShowMetadata() {
        initMetadatModels();
        initNamespacesOfXmpTableModelMap();
        listen();
    }

    private enum Metadata {
        EXIF, IPTC, XMP;

        public static Set<Metadata> getAll() {
            return new HashSet<Metadata>(Arrays.asList(values()));
        }
    }

    private void listen() {
        appPanel.getPanelThumbnails().addThumbnailsPanelListener(this);
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    private void initMetadatModels() {
        metadataTableModels.setIptcTableModel(
            (TableModelIptc) appPanel.getTableIptc().getModel());
        metadataTableModels.setExifTableModel(
            (TableModelExif) appPanel.getTableExif().getModel());
        metadataTableModels.setXmpTableModelDc(
            (TableModelXmp) appPanel.getTableXmpDc().getModel());
        metadataTableModels.setXmpTableModelExif(
            (TableModelXmp) appPanel.getTableXmpExif().getModel());
        metadataTableModels.setXmpTableModelIptc(
            (TableModelXmp) appPanel.getTableXmpIptc().getModel());
        metadataTableModels.setXmpTableModelLightroom(
            (TableModelXmp) appPanel.getTableXmpLightroom().getModel());
        metadataTableModels.setXmpTableModelPhotoshop(
            (TableModelXmp) appPanel.getTableXmpPhotoshop().getModel());
        metadataTableModels.setXmpTableModelTiff(
            (TableModelXmp) appPanel.getTableXmpTiff().getModel());
        metadataTableModels.setXmpTableModelCameraRawSettings(
            (TableModelXmp) appPanel.getTableXmpCameraRawSettings().getModel());
        metadataTableModels.setXmpTableModelXap(
            (TableModelXmp) appPanel.getTableXmpXap().getModel());

        List<JTable>       xmpTables      = appPanel.getXmpTables();
        Set<TableModelXmp> xmpTableModels = new HashSet<TableModelXmp>();

        for (JTable xmpTable : xmpTables) {
            xmpTableModels.add((TableModelXmp) xmpTable.getModel());
        }

        metadataTableModels.setXmpTableModels(xmpTableModels);
    }

    private void initNamespacesOfXmpTableModelMap() {
        namespacesOfXmpTableModel.put(metadataTableModels.getXmpTableModelDc(),
                                      new String[] { XMPConst.NS_DC,
                XMPConst.NS_DC_DEPRECATED });
        namespacesOfXmpTableModel.put(
            metadataTableModels.getXmpTableModelExif(),
            new String[] { XMPConst.NS_EXIF,
                           XMPConst.NS_EXIF_AUX });
        namespacesOfXmpTableModel.put(
            metadataTableModels.getXmpTableModelIptc(),
            new String[] { XMPConst.NS_IPTCCORE });
        namespacesOfXmpTableModel.put(
            metadataTableModels.getXmpTableModelLightroom(),
            new String[] { "http://ns.adobe.com/lightroom/1.0/" });
        namespacesOfXmpTableModel.put(
            metadataTableModels.getXmpTableModelPhotoshop(),
            new String[] { XMPConst.NS_PHOTOSHOP });
        namespacesOfXmpTableModel.put(
            metadataTableModels.getXmpTableModelTiff(),
            new String[] { XMPConst.NS_TIFF });
        namespacesOfXmpTableModel.put(
            metadataTableModels.getXmpTableModelCameraRawSettings(),
            new String[] { XMPConst.NS_CAMERARAW,
                           "http://ns.adobe.com/camera-raw-saved-settings/1.0/" });
        namespacesOfXmpTableModel.put(
            metadataTableModels.getXmpTableModelXap(),
            new String[] { XMPConst.NS_XMP,
                           XMPConst.NS_XMP_RIGHTS });
    }

    @Override
    public void thumbnailsSelectionChanged() {
        final ThumbnailsPanel panel = appPanel.getPanelThumbnails();

        if (panel.getSelectionCount() == 1) {
            SwingUtilities.invokeLater(
                new ShowMetadata(
                    panel.getSelectedFiles().get(0), Metadata.getAll()));
        } else {
            appPanel.getButtonIptcToXmp().setEnabled(false);
            appPanel.getButtonExifToXmp().setEnabled(false);
            SwingUtilities.invokeLater(new RemoveAllMetadata());
        }
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

    private void showUpdates(File file, Set<Metadata> metadata) {
        if (appPanel.getPanelThumbnails().getSelectionCount() == 1) {
            File selectedFile =
                appPanel.getPanelThumbnails().getSelectedFiles().get(0);

            if (file.equals(selectedFile)) {
                SwingUtilities.invokeLater(new ShowMetadata(file, metadata));
            }
        }
    }

    private void repaintMetadataTables(Set<Metadata> metadata) {
        if (metadata.contains(Metadata.EXIF)) {
            repaintTables(Collections.singleton(appPanel.getTableExif()));
        }

        if (metadata.contains(Metadata.XMP)) {
            repaintTables(appPanel.getXmpTables());
        }
    }

    private void repaintTables(Collection<JTable> tables) {
        for (JTable table : tables) {
            ComponentUtil.forceRepaint(table);
        }
    }

    private void removeMetadataFromTables(Set<Metadata> metadata) {
        if (metadata.contains(Metadata.XMP)) {
            for (TableModelXmp model : metadataTableModels
                    .getXmpTableModels()) {
                model.removeAllRows();
            }
        }

        if (metadata.contains(Metadata.EXIF)) {
            metadataTableModels.getExifTableModel().removeAllElements();
        }
    }

    private class RemoveAllMetadata implements Runnable {
        @Override
        public void run() {
            Set<Metadata> allMetadata = Metadata.getAll();

            removeMetadataFromTables(allMetadata);
            repaintMetadataTables(allMetadata);
            appPanel.getLabelMetadataFilename().setText(
                JptBundle.INSTANCE.getString(
                    "ControllerShowMetadata.Info.MetadataIsShownOnlyIfOneImageIsSelected"));
        }
    }


    private class ShowMetadata implements Runnable {
        private final File          file;
        private final Set<Metadata> metadata;

        public ShowMetadata(File file, Set<Metadata> metadata) {
            this.file     = file;
            this.metadata = metadata;
        }

        @Override
        public void run() {
            removeMetadataFromTables(metadata);

            if (metadata.contains(Metadata.EXIF)) {
                metadataTableModels.getExifTableModel().setFile(file);
            }

            if (metadata.contains(Metadata.IPTC)
                    && UserSettings.INSTANCE.isDisplayIptc()) {
                metadataTableModels.getIptcTableModel().setFile(file);
                appPanel.getButtonIptcToXmp().setEnabled(hasIptcData());
            }

            appPanel.getButtonExifToXmp().setEnabled(hasExifData());

            if (metadata.contains(Metadata.XMP)) {
                setXmpModels(file);
            }

            appPanel.getLabelMetadataFilename().setText(file.getName()
                    + (XmpMetadata.hasImageASidecarFile(file)
                       ? ""
                       : JptBundle.INSTANCE.getString(
                           "ControllerShowMetadata.Embedded")));
            resizeMetadataTables(metadata);
            repaintMetadataTables(metadata);
        }

        private void resizeMetadataTables(Set<Metadata> metadata) {
            if (metadata.contains(Metadata.EXIF)) {
                resizeTables(Collections.singleton(appPanel.getTableExif()));
            }

            if (metadata.contains(Metadata.IPTC)
                    && UserSettings.INSTANCE.isDisplayIptc()) {
                resizeTables(Collections.singleton(appPanel.getTableIptc()));
            }

            if (metadata.contains(Metadata.XMP)) {
                resizeTables(appPanel.getXmpTables());
            }
        }

        private void resizeTables(Collection<JTable> tables) {
            for (JTable table : tables) {
                TableUtil.resizeColumnWidthsToFit(table);
            }
        }

        private void setXmpModels(File imageFile) {
            List<XMPPropertyInfo> allInfos    = null;
            File                  sidecarFile =
                XmpMetadata.getSidecarFile(imageFile);

            allInfos = (sidecarFile != null)
                       ? XmpMetadata.getPropertyInfosOfSidecarFile(sidecarFile)
                       : UserSettings.INSTANCE.isScanForEmbeddedXmp()
                         ? XmpMetadata.getEmbeddedPropertyInfos(imageFile)
                         : null;

            if (allInfos != null) {
                for (TableModelXmp model :
                        metadataTableModels.getXmpTableModels()) {
                    setPropertyInfosToXmpTableModel(
                        imageFile, model, allInfos,
                        namespacesOfXmpTableModel.get(model));
                }
            }
        }

        private void setPropertyInfosToXmpTableModel(File imageFile,
                TableModelXmp model, List<XMPPropertyInfo> allInfos,
                String[] namespaces) {
            List<XMPPropertyInfo> infos = new ArrayList<XMPPropertyInfo>();

            for (int index = 0; index < namespaces.length; index++) {
                infos.addAll(
                    XmpMetadata.filterPropertyInfosOfNamespace(
                        allInfos, namespaces[index]));
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
