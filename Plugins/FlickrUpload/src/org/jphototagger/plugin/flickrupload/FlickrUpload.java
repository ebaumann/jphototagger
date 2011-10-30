package org.jphototagger.plugin.flickrupload;

import java.awt.Component;
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

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.aetrion.flickr.uploader.UploadMetaData;
import com.aetrion.flickr.uploader.Uploader;

import org.bushe.swing.event.EventBus;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.plugin.fileprocessor.FileProcessedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingFinishedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingStartedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.api.progress.MainWindowProgressBarProvider;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.domain.thumbnails.ThumbnailProvider;
import org.jphototagger.image.util.ImageUtil;
import org.jphototagger.lib.help.HelpContentProvider;
import org.jphototagger.lib.io.IoUtil;
import org.jphototagger.lib.plugin.AbstractFileProcessorPlugin;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.plugin.flickrupload.FlickrImageInfoPanel.ImageInfo;
import org.jphototagger.xmp.XmpProperties;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileProcessorPlugin.class)
public final class FlickrUpload extends AbstractFileProcessorPlugin implements Serializable, HelpContentProvider {

    private static final long serialVersionUID = 1L;
    private static final Icon icon = IconUtil.getImageIcon(FlickrUpload.class, "flickr.png");
    private static final String PROGRESS_BAR_STRING = Bundle.getString(FlickrUpload.class, "FlickrUpload.ProgressBar.String");
    private final MainWindowProgressBarProvider progressBarProvider = Lookup.getDefault().lookup(MainWindowProgressBarProvider.class);

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
    public String getHelpContentUrl() {
        return "/org/jphototagger/plugin/flickrupload/help/contents.xml";
    }

    @Override
    public Icon getSmallIcon() {
        return icon;
    }

    @Override
    public void processFiles(Collection<? extends File> files) {
        new Upload(files).start();
    }

    private class Upload extends Thread {

        private final Collection<? extends File> files;
        private final Object pBarOwner = this;

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
            int countOfImagesToUpload = uploadImages.size();
            int countOfProcessedImages = 0;
            FileInputStream is = null;
            boolean success = true;

            EventBus.publish(new FileProcessingStartedEvent(this));
            progressBarProvider.progressStarted(createStartProgressEvent(countOfImagesToUpload));

            File imageFile = null;

            for (ImageInfo imageInfo : uploadImages) {
                try {
                    imageFile = imageInfo.getImageFile();
                    is = new FileInputStream(imageFile);
                    uploader.upload(is, getUploadMetaData(imageInfo));
                    is.close();
                    EventBus.publish(new FileProcessedEvent(this, imageFile, false));
                    countOfProcessedImages++;
                    progressBarProvider.progressPerformed(createPerformedProgressEvent(countOfImagesToUpload, countOfProcessedImages));
                } catch (Exception ex) {
                    logDisplayUploadException(ex, imageFile);
                    success = false;

                    break;
                } finally {
                    IoUtil.close(is);
                }
            }

            uploadFinished(countOfImagesToUpload, countOfProcessedImages, success);
        }

        private Uploader createUploader() {
            return new Uploader("1efba3cf4198b683047512bec1429f19", "b58bc39d8aedd4c5");
        }

        private ProgressEvent createStartProgressEvent(int maximum) {
            return new ProgressEvent.Builder().source(pBarOwner).minimum(0).maximum(maximum).value(0).stringPainted(true).stringToPaint(PROGRESS_BAR_STRING).build();
        }

        private ProgressEvent createPerformedProgressEvent(int maximum, int value) {
            return new ProgressEvent.Builder().source(pBarOwner).minimum(0).maximum(maximum).value(value).stringPainted(true).stringToPaint(PROGRESS_BAR_STRING).build();
        }

        private void uploadFinished(int countOfImagesToUpload, int countOfUploadedImages, boolean success) throws HeadlessException {
            progressBarProvider.progressEnded(pBarOwner);
            JOptionPane.showMessageDialog(ComponentUtil.findFrameWithIcon(), Bundle.getString(FlickrUpload.class, "FlickrUpload.Info.UploadCount", countOfUploadedImages));
            EventBus.publish(new FileProcessingFinishedEvent(this, success));
        }

        private void logDisplayUploadException(Exception ex, File imageFile) throws HeadlessException {
            Logger.getLogger(FlickrUpload.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(ComponentUtil.findFrameWithIcon(), Bundle.getString(FlickrUpload.class, "FlickrUpload.Error.Upload", imageFile));
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
            ThumbnailProvider thumbnailProvider = Lookup.getDefault().lookup(ThumbnailProvider.class);

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

    @Override
    public boolean isAvailable() {
        return true;
    }
}
