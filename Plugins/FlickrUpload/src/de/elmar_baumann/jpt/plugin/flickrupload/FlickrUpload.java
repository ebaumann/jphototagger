/*
 * FlickrUpload - plugin for JPhotoTagger
 * Copyright (C) 2009 by Elmar Baumann<eb@elmar-baumann.de>
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
package de.elmar_baumann.jpt.plugin.flickrupload;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.aetrion.flickr.uploader.UploadMetaData;
import com.aetrion.flickr.uploader.Uploader;
import de.elmar_baumann.jpt.plugin.ImageUtil;
import de.elmar_baumann.jpt.plugin.Plugin;
import de.elmar_baumann.jpt.plugin.PluginListener.Event;
import de.elmar_baumann.jpt.plugin.Xmp;
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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-13
 */
public final class FlickrUpload extends Plugin implements Serializable {

    private static final long serialVersionUID = -2935460271965834936L;
    private static final Icon icon = ImageUtil.getImageIcon("/de/elmar_baumann/jpt/plugin/flickrupload/flickr.png");


    private final UploadAction uploadAction = new UploadAction();

    @Override
    public String getName() {
        return Bundle.getString("FlickrUpload.Name");
    }

    @Override
    public String getDescription() {
        return Bundle.getString("FlickrUpload.Description");
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
            setName("Uploading images to Flickr  @ " + FlickrUpload.class.getSimpleName());
        }

        @Override
        public void run() {
            if (!confirmUpload()) return;
            Authorization auth = new Authorization(getProperties());
            if (!auth.authenticate()) return;

            Uploader        uploader          = new Uploader("1efba3cf4198b683047512bec1429f19", "b58bc39d8aedd4c5");
            int             size              = files.size();
            int             index             = 0;
            String          progressBarString = Bundle.getString("FlickrUpload.ProgressBar.String");
            FileInputStream is                = null;
            Settings        settings          = new Settings(getProperties());

            progressStarted(0, size, 0, progressBarString);
            for (File file : files) {
                try {
                    is = new FileInputStream(file);
                    uploader.upload(is, getUploadMetaData(file, settings));
                    is.close();
                    progressPerformed(0, size, ++index, progressBarString);
                } catch (Exception ex) {
                    Logger.getLogger(FlickrUpload.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, Bundle.getString("FlickrUpload.Error.Upload", file));
                    break;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception ex) {
                            Logger.getLogger(FlickrUpload.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            progressEnded();
            JOptionPane.showMessageDialog(null, Bundle.getString("FlickrUpload.Info.UploadCount", index));
            notifyPluginListeners(Event.FINISHED_NO_ERRORS);
        }

        private UploadMetaData getUploadMetaData(File imageFile, Settings settings) {
            UploadMetaData umd = new UploadMetaData();

            File sidecarFile = Xmp.getSidecarfileOf(imageFile);
            if (sidecarFile == null) return umd;

            List<XMPPropertyInfo> pInfos = Xmp.getPropertyInfosOfSidecarFile(sidecarFile);
            if (pInfos == null) return umd;
            
            List<String> values = null;
            String       value  = null;

            if (settings.isAddDcDescription()) {
                value = Xmp.getPropertyValueFrom(pInfos, Xmp.PropertyValue.DC_DESCRIPTION);
                if (value != null && !value.isEmpty()) {
                    umd.setDescription(value);
                }
            }

            if (settings.isAddPhotoshopHeadline()) {
                value = Xmp.getPropertyValueFrom(pInfos, Xmp.PropertyValue.PHOTOSHOP_HEADLINE);
                if (value != null && !value.isEmpty()) {
                    umd.setTitle(value);
                }
            }

            if (settings.isAddDcSubjects()) {
                values = Xmp.getPropertyValuesFrom(pInfos, Xmp.PropertyValue.DC_SUBJECT);
                if (!values.isEmpty()) {
                    umd.setTags(values);
                }
            }

            return umd;
        }

        private boolean confirmUpload() {
            return JOptionPane.showConfirmDialog(null,
                    Bundle.getString("FlickrUpload.Confirm.Upload", files.size()),
                    Bundle.getString("FlickrUpload.Confirm.Upload.Title"),
                    JOptionPane.YES_NO_OPTION
                    )
                    == JOptionPane.YES_OPTION;
        }
    }
}
