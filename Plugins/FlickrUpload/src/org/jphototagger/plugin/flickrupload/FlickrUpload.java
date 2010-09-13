/*
 * @(#)FlickrUpload.java    Created on 2010-02-13
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

package org.jphototagger.plugin.flickrupload;

import com.adobe.xmp.properties.XMPPropertyInfo;

import com.aetrion.flickr.uploader.Uploader;
import com.aetrion.flickr.uploader.UploadMetaData;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.image.metadata.xmp.Xmp;
import org.jphototagger.lib.image.util.IconUtil;
import org.jphototagger.lib.image.util.ImageUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.plugin.flickrupload.FlickrImageInfoPanel.ImageInfo;
import org.jphototagger.plugin.Plugin;
import org.jphototagger.plugin.PluginEvent;

import java.awt.event.ActionEvent;
import java.awt.HeadlessException;
import java.awt.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
 */
public final class FlickrUpload extends Plugin implements Serializable {
    private static final long serialVersionUID = -2935460271965834936L;
    private static final Icon icon =
        IconUtil.getImageIcon(
            "/org/jphototagger/plugin/flickrupload/flickr.png");
    private final UploadAction  uploadAction = new UploadAction();
    private static final String PROGRESS_BAR_STRING =
        FlickrBundle.INSTANCE.getString("FlickrUpload.ProgressBar.String");

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
        return "/org/jphototagger/plugin/flickrupload/help/contents.xml";
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

        UploadAction() {
            putValue(Action.NAME, getName());
            putValue(Action.SMALL_ICON, icon);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
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
            if (!new Authorization(getProperties()).authenticate()) {
                return;
            }

            Uploader              uploader = createUploader();
            FlickrImageInfoDialog dlg =
                new FlickrImageInfoDialog(getProperties());

            addImages(dlg);
            dlg.setVisible(true);

            if (!dlg.isUpload()) {
                return;
            }

            List<ImageInfo> uploadImages   = dlg.getUploadImages();
            int             size           = uploadImages.size();
            int             index          = 0;
            FileInputStream is             = null;
            boolean         success        = true;
            List<File>      processedFiles = new ArrayList<File>(size);

            notifyPluginListeners(new PluginEvent(PluginEvent.Type.STARTED));
            progressStarted(0, size, 0, PROGRESS_BAR_STRING);

            File imageFile = null;

            for (ImageInfo imageInfo : uploadImages) {
                try {
                    imageFile = imageInfo.getImageFile();
                    is        = new FileInputStream(imageFile);
                    uploader.upload(is, getUploadMetaData(imageInfo));
                    is.close();
                    processedFiles.add(imageFile);
                    progressPerformed(0, size, ++index, PROGRESS_BAR_STRING);
                } catch (Exception ex) {
                    logDisplayUploadException(ex, imageFile);
                    success = false;

                    break;
                } finally {
                    FileUtil.closeStream(is);
                }
            }

            uploadFinished(index, processedFiles, success);
        }

        private Uploader createUploader() {
            return new Uploader("1efba3cf4198b683047512bec1429f19",
                                "b58bc39d8aedd4c5");
        }

        private void uploadFinished(int index, List<File> processedFiles,
                                    boolean success)
                throws HeadlessException {
            progressEnded();
            JOptionPane.showMessageDialog(
                ComponentUtil.getFrameWithIcon(),
                FlickrBundle.INSTANCE.getString(
                    "FlickrUpload.Info.UploadCount", index));
            notifyFinished(processedFiles, success);
        }

        private void logDisplayUploadException(Exception ex, File imageFile)
                throws HeadlessException {
            Logger.getLogger(FlickrUpload.class.getName()).log(Level.SEVERE,
                             null, ex);
            JOptionPane.showMessageDialog(
                ComponentUtil.getFrameWithIcon(),
                FlickrBundle.INSTANCE.getString(
                    "FlickrUpload.Error.Upload", imageFile));
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

        private UploadMetaData getUploadMetaData(ImageInfo imageInfo) {
            UploadMetaData umd         = new UploadMetaData();
            String         description = imageInfo.getDescription();

            if (!description.isEmpty()) {
                umd.setDescription(description);
            }

            String title = imageInfo.getTitle();

            if (!title.isEmpty()) {
                umd.setTitle(title);
            }

            List<String> tags = imageInfo.getTags();

            if (!tags.isEmpty()) {
                umd.setTags(tags);
            }

            return umd;
        }

        private ImageInfo getImageInfo(File imageFile, Settings settings) {
            File  sidecarFile = Xmp.getSidecarfileOf(imageFile);
            Image image = ImageUtil.getScaledInstance(getThumbnail(imageFile),
                              FlickrImageInfoPanel.IMAGE_WIDTH);
            ImageInfo emptyImageInfo = getEmptyImageInfo(image, imageFile);

            if (sidecarFile == null) {
                return emptyImageInfo;
            }

            List<XMPPropertyInfo> pInfos =
                Xmp.getPropertyInfosOfSidecarFile(sidecarFile);

            if (pInfos == null) {
                return emptyImageInfo;
            }

            List<String> values      = null;
            String       value       = null;
            String       description = "";
            String       title       = "";
            List<String> tags        = Collections.<String>emptyList();

            if (settings.isAddDcDescription()) {
                value =
                    Xmp.getPropertyValueFrom(pInfos,
                                             Xmp.PropertyValue.DC_DESCRIPTION);

                if ((value != null) &&!value.isEmpty()) {
                    description = value;
                }
            }

            if (settings.isAddPhotoshopHeadline()) {
                value = Xmp.getPropertyValueFrom(
                    pInfos, Xmp.PropertyValue.PHOTOSHOP_HEADLINE);

                if ((value != null) &&!value.isEmpty()) {
                    title = value;
                }
            }

            if (settings.isAddDcSubjects()) {
                values =
                    Xmp.getPropertyValuesFrom(pInfos,
                                              Xmp.PropertyValue.DC_SUBJECT);

                if (!values.isEmpty()) {
                    tags = values;
                }
            }

            return new ImageInfo(image, imageFile, title, description, tags);
        }

        private ImageInfo getEmptyImageInfo(Image image, File imageFile) {
            return new ImageInfo(image, imageFile, "", "",
                                 Collections.<String>emptyList());
        }

        private void addImages(FlickrImageInfoDialog dlg) {
            Settings settings = new Settings(getProperties());

            for (File file : files) {
                dlg.addImage(getImageInfo(file, settings));
            }
        }
    }
}
