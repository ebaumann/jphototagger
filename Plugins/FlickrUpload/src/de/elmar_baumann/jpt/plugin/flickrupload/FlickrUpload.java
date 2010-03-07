/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.plugin.flickrupload;

import com.adobe.xmp.properties.XMPPropertyInfo;

import com.aetrion.flickr.uploader.Uploader;
import com.aetrion.flickr.uploader.UploadMetaData;

import de.elmar_baumann.jpt.plugin.Plugin;
import de.elmar_baumann.jpt.plugin.PluginEvent;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.image.metadata.xmp.Xmp;
import de.elmar_baumann.lib.image.util.IconUtil;

import java.awt.event.ActionEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-02-13
 */
public final class FlickrUpload extends Plugin implements Serializable {
    private static final long serialVersionUID = -2935460271965834936L;
    private static final Icon icon             =
        IconUtil.getImageIcon(
            "/de/elmar_baumann/jpt/plugin/flickrupload/flickr.png");
    private final UploadAction uploadAction = new UploadAction();

    @Override
    public String getName() {
        return FlickrBundle.INSTANCE.getString("FlickrUpload.Name");
    }

    @Override
    public String getDescription() {
        return FlickrBundle.INSTANCE.getString("FlickrUpload.Description");
    }

    @Override
    public JPanel getSettingsPanel() {
        SettingsPanel panel = new SettingsPanel();

        panel.setProperties(getProperties());

        return panel;
    }

    @Override
    public String getHelpContentsPath() {
        return "/de/elmar_baumann/jpt/plugin/flickrupload/help/contents.xml";
    }

    @Override
    public String getFirstHelpPageName() {
        return "index.html";
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    private class UploadAction extends AbstractAction {
        private static final long serialVersionUID = -5807124252712511456L;

        public UploadAction() {
            putValue(Action.NAME, getName());
            putValue(Action.SMALL_ICON, icon);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new Upload(getFiles()).start();
        }
    }


    @Override
    public List<? extends Action> getActions() {
        return Arrays.asList(uploadAction);
    }

    private class Upload extends Thread {
        private final List<File> files;

        Upload(List<File> files) {
            this.files = new ArrayList<File>(files);
            setName("Uploading images to Flickr  @ "
                    + FlickrUpload.class.getSimpleName());
        }

        @Override
        public void run() {
            if (!confirmUpload()) {
                return;
            }

            Authorization auth = new Authorization(getProperties());

            if (!auth.authenticate()) {
                return;
            }

            Uploader uploader =
                new Uploader("1efba3cf4198b683047512bec1429f19",
                             "b58bc39d8aedd4c5");
            int    size              = files.size();
            int    index             = 0;
            String progressBarString = FlickrBundle.INSTANCE.getString(
                                           "FlickrUpload.ProgressBar.String");
            FileInputStream is             = null;
            Settings        settings       = new Settings(getProperties());
            boolean         success        = true;
            List<File>      processedFiles = new ArrayList<File>(size);

            notifyPluginListeners(new PluginEvent(PluginEvent.Type.STARTED));
            progressStarted(0, size, 0, progressBarString);

            for (File file : files) {
                try {
                    is = new FileInputStream(file);
                    uploader.upload(is, getUploadMetaData(file, settings));
                    is.close();
                    processedFiles.add(file);
                    progressPerformed(0, size, ++index, progressBarString);
                } catch (Exception ex) {
                    Logger.getLogger(FlickrUpload.class.getName()).log(
                        Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(
                        ComponentUtil.getFrameWithIcon(),
                        FlickrBundle.INSTANCE.getString(
                            "FlickrUpload.Error.Upload", file));
                    success = false;

                    break;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception ex) {
                            Logger.getLogger(FlickrUpload.class.getName()).log(
                                Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            progressEnded();
            JOptionPane.showMessageDialog(
                ComponentUtil.getFrameWithIcon(),
                FlickrBundle.INSTANCE.getString(
                    "FlickrUpload.Info.UploadCount", index));
            notifyFinished(processedFiles, success);
        }

        private void notifyFinished(List<File> processedFiles,
                                    boolean success) {
            PluginEvent evt = new PluginEvent(success
                                              ? PluginEvent.Type
                                                  .FINISHED_SUCCESS
                                              : PluginEvent.Type
                                                  .FINISHED_ERRORS);

            evt.setProcessedFiles(processedFiles);
            notifyPluginListeners(evt);
        }

        private UploadMetaData getUploadMetaData(File imageFile,
                Settings settings) {
            UploadMetaData umd         = new UploadMetaData();
            File           sidecarFile = Xmp.getSidecarfileOf(imageFile);

            if (sidecarFile == null) {
                return umd;
            }

            List<XMPPropertyInfo> pInfos =
                Xmp.getPropertyInfosOfSidecarFile(sidecarFile);

            if (pInfos == null) {
                return umd;
            }

            List<String> values = null;
            String       value  = null;

            if (settings.isAddDcDescription()) {
                value =
                    Xmp.getPropertyValueFrom(pInfos,
                                             Xmp.PropertyValue.DC_DESCRIPTION);

                if ((value != null) &&!value.isEmpty()) {
                    umd.setDescription(value);
                }
            }

            if (settings.isAddPhotoshopHeadline()) {
                value = Xmp.getPropertyValueFrom(
                    pInfos, Xmp.PropertyValue.PHOTOSHOP_HEADLINE);

                if ((value != null) &&!value.isEmpty()) {
                    umd.setTitle(value);
                }
            }

            if (settings.isAddDcSubjects()) {
                values =
                    Xmp.getPropertyValuesFrom(pInfos,
                                              Xmp.PropertyValue.DC_SUBJECT);

                if (!values.isEmpty()) {
                    umd.setTags(values);
                }
            }

            return umd;
        }

        private boolean confirmUpload() {
            return JOptionPane
                .showConfirmDialog(
                    ComponentUtil.getFrameWithIcon(),
                    FlickrBundle.INSTANCE
                        .getString(
                            "FlickrUpload.Confirm.Upload",
                            files.size()), FlickrBundle.INSTANCE
                                .getString(
                                    "FlickrUpload.Confirm.Upload.Title"), JOptionPane
                                        .YES_NO_OPTION) == JOptionPane
                                            .YES_OPTION;
        }
    }
}
