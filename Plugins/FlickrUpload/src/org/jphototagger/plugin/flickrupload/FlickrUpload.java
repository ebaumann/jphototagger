package org.jphototagger.plugin.flickrupload;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.aetrion.flickr.uploader.Uploader;
import com.aetrion.flickr.uploader.UploadMetaData;
import java.awt.Component;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.xmp.XmpProperties;
import org.jphototagger.lib.image.util.IconUtil;
import org.jphototagger.image.util.ImageUtil;
import org.jphototagger.plugin.flickrupload.FlickrImageInfoPanel.ImageInfo;
import org.jphototagger.plugin.AbstractFileProcessorPlugin;
import org.jphototagger.services.plugin.FileProcessorPluginEvent;
import java.awt.HeadlessException;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import org.jphototagger.lib.io.IoUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.services.core.ThumbnailProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FlickrUpload extends AbstractFileProcessorPlugin implements Serializable {

    private static final long serialVersionUID = -2935460271965834936L;
    private static final Icon icon = IconUtil.getImageIcon("/org/jphototagger/plugin/flickrupload/flickr.png");
    private static final String PROGRESS_BAR_STRING = Bundle.getString(FlickrUpload.class, "FlickrUpload.ProgressBar.String");

    @Override
    public String getDisplayName() {
        return Bundle.getString(FlickrUpload.class, "FlickrUpload.Name");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(FlickrUpload.class, "FlickrUpload.Description");
    }

    @Override
    public Component getSettingsComponent() {
        return new SettingsPanel();
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

    @Override
    public void processFiles(Collection<? extends File> files) {
        new Upload(files).start();
    }

    private class Upload extends Thread {

        private final Collection<? extends File> files;

        Upload(Collection<? extends File> files) {
            this.files = new ArrayList<File>(files);
            setName("Uploading images to Flickr  @ " + FlickrUpload.class.getSimpleName());
        }

        @Override
        public void run() {
            if (!new Authorization().authenticate()) {
                return;
            }

            Uploader uploader = createUploader();
            FlickrImageInfoDialog dlg = new FlickrImageInfoDialog();

            addImages(dlg);
            dlg.setVisible(true);

            if (!dlg.isUpload()) {
                return;
            }

            List<ImageInfo> uploadImages = dlg.getUploadImages();
            int size = uploadImages.size();
            int index = 0;
            FileInputStream is = null;
            boolean success = true;
            List<File> processedFiles = new ArrayList<File>(size);

            notifyFileProcessorPluginListeners(new FileProcessorPluginEvent(FileProcessorPluginEvent.Type.PROCESSING_STARTED));
            progressStarted(0, size, 0, PROGRESS_BAR_STRING);

            File imageFile = null;

            for (ImageInfo imageInfo : uploadImages) {
                try {
                    imageFile = imageInfo.getImageFile();
                    is = new FileInputStream(imageFile);
                    uploader.upload(is, getUploadMetaData(imageInfo));
                    is.close();
                    processedFiles.add(imageFile);
                    progressPerformed(0, size, ++index, PROGRESS_BAR_STRING);
                } catch (Exception ex) {
                    logDisplayUploadException(ex, imageFile);
                    success = false;

                    break;
                } finally {
                    IoUtil.close(is);
                }
            }

            uploadFinished(index, processedFiles, success);
        }

        private Uploader createUploader() {
            return new Uploader("1efba3cf4198b683047512bec1429f19", "b58bc39d8aedd4c5");
        }

        private void uploadFinished(int index, List<File> processedFiles, boolean success) throws HeadlessException {
            progressEnded();
            JOptionPane.showMessageDialog(ComponentUtil.getFrameWithIcon(), Bundle.getString(FlickrUpload.class, "FlickrUpload.Info.UploadCount", index));
            notifyFinished(processedFiles, success);
        }

        private void logDisplayUploadException(Exception ex, File imageFile) throws HeadlessException {
            Logger.getLogger(FlickrUpload.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(ComponentUtil.getFrameWithIcon(), Bundle.getString(FlickrUpload.class, "FlickrUpload.Error.Upload", imageFile));
        }

        private void notifyFinished(List<File> processedFiles, boolean success) {
            FileProcessorPluginEvent evt = new FileProcessorPluginEvent(success
                    ? FileProcessorPluginEvent.Type.PROCESSING_FINISHED_SUCCESS
                    : FileProcessorPluginEvent.Type.PROCESSING_FINISHED_ERRORS);

            evt.setProcessedFiles(processedFiles);
            notifyFileProcessorPluginListeners(evt);
        }

        private UploadMetaData getUploadMetaData(ImageInfo imageInfo) {
            UploadMetaData umd = new UploadMetaData();
            String description = imageInfo.getDescription();

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

        private Image getThumbnail(File imageFile) {
            ThumbnailProvider thumbnailProvider = ServiceLookup.lookup(ThumbnailProvider.class);

            if (thumbnailProvider != null) {
                Image thumbnail = thumbnailProvider.getThumbnail(imageFile);

                if (thumbnail != null) {
                    return ImageUtil.getScaledInstance(thumbnail, FlickrImageInfoPanel.IMAGE_WIDTH);
                }
            }

            return null;
        }

        private ImageInfo getImageInfo(File imageFile, Settings settings) {
            File sidecarFile = XmpProperties.getSidecarfileOf(imageFile);
            Image image = getThumbnail(imageFile);
            ImageInfo emptyImageInfo = getEmptyImageInfo(image, imageFile);

            if (sidecarFile == null) {
                return emptyImageInfo;
            }

            List<XMPPropertyInfo> pInfos = XmpProperties.getPropertyInfosOfSidecarFile(sidecarFile);

            if (pInfos == null) {
                return emptyImageInfo;
            }

            List<String> values = null;
            String value = null;
            String description = "";
            String title = "";
            List<String> tags = Collections.<String>emptyList();

            if (settings.isAddDcDescription()) {
                value = XmpProperties.getPropertyValueFrom(pInfos, XmpProperties.PropertyValue.DC_DESCRIPTION);

                if ((value != null) && !value.isEmpty()) {
                    description = value;
                }
            }

            if (settings.isAddPhotoshopHeadline()) {
                value = XmpProperties.getPropertyValueFrom(pInfos, XmpProperties.PropertyValue.PHOTOSHOP_HEADLINE);

                if ((value != null) && !value.isEmpty()) {
                    title = value;
                }
            }

            if (settings.isAddDcSubjects()) {
                values = XmpProperties.getPropertyValuesFrom(pInfos, XmpProperties.PropertyValue.DC_SUBJECT);

                if (!values.isEmpty()) {
                    tags = values;
                }
            }

            return new ImageInfo(image, imageFile, title, description, tags);
        }

        private ImageInfo getEmptyImageInfo(Image image, File imageFile) {
            return new ImageInfo(image, imageFile, "", "", Collections.<String>emptyList());
        }

        private void addImages(FlickrImageInfoDialog dlg) {
            Settings settings = new Settings();

            for (File file : files) {
                dlg.addImage(getImageInfo(file, settings));
            }
        }
    }
}
