package org.jphototagger.repositoryfilebrowser;

import java.awt.Image;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.Icon;

import org.openide.util.Lookup;

import org.jphototagger.domain.thumbnails.ThumbnailProvider;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public class RepositoryImageFileInfo {

    private File imageFile;
    private Icon imageFileIcon;
    private Image thumbnail;
    private String timeImageFileInRepository = "-";
    private String timeImageFileInFileSystem = "-";
    private String timeXmpFileInRepository = "-";
    private String timeXmpFileInFileSystem = "-";
    private boolean imageFileExists;
    private boolean xmpFileExists;
    private String timeImageFileWarning;
    private String timeXmpFileWarning;
    private String thumbnailSizeInfo;
    private final ImageFilesRepository imageFileRepository = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    private static final String TIME_WARNING_STRING = Bundle.getString(RepositoryImageFileInfo.class, "RepositoryImageFileInfo.TimeWarningString");

    public RepositoryImageFileInfo(FileNode fileNode) {
        if (fileNode != null) {
            imageFile = fileNode.getFile();
            imageFileIcon = fileNode.getSmallIcon();
            setThumbnail();
            setThumbnailSizeInfo();
            setImageFileTimeStamps();
            setXmpFileTimeStamps();
            setFilesExists();
        }
    }

    private void setThumbnail() {
        ThumbnailProvider thumbnailProvider = Lookup.getDefault().lookup(ThumbnailProvider.class);
        thumbnail = thumbnailProvider.getThumbnail(imageFile);
    }

    private void setThumbnailSizeInfo() {
        if (thumbnail == null) {
            return;
        }

        int width = thumbnail.getWidth(null);
        int height = thumbnail.getHeight(null);

        thumbnailSizeInfo = Bundle.getString(RepositoryImageFileInfo.class, "RepositoryImageFileInfo.ThumbnailSizeInfo", width, height);
    }

    private void setImageFileTimeStamps() {
        long repoTimestamp = imageFileRepository.findImageFilesLastModifiedTimestamp(imageFile);
        timeImageFileInRepository = createDateStringOfTimestamp(repoTimestamp);
        timeImageFileInFileSystem = createDateStringOfFile(imageFile);
        setTimeImageFileWarning(repoTimestamp);
    }

    private void setXmpFileTimeStamps() {
        long repoTimestamp = imageFileRepository.findXmpFilesLastModifiedTimestamp(imageFile);
        timeXmpFileInRepository = createDateStringOfTimestamp(repoTimestamp);
        File xmpFile = resolveXmpFile(imageFile);
        timeXmpFileInFileSystem = createDateStringOfFile(xmpFile);
        setTimeXmpFileWarning(repoTimestamp, xmpFile);
    }

    private String createDateStringOfTimestamp(long timestamp) {
        return timestamp < 0 ? "-" : DATE_FORMAT.format(new Date(timestamp));
    }

    private String createDateStringOfFile(File file) {
        if (file.exists()) {
            long lastModified = file.lastModified();
            return createDateStringOfTimestamp(lastModified);
        } else {
            return "-";
        }
    }

    private File resolveXmpFile(File imageFile) {
        return new File(resolveXmpFilepath(imageFile.getAbsolutePath()));
    }

    private String resolveXmpFilepath(String imageFilePath) {
        int indexOfLastDot = imageFilePath.lastIndexOf('.');

        if (indexOfLastDot < 1) {
            return "";
        }

        return imageFilePath.substring(0, indexOfLastDot + 1) + "xmp"; // may fail on case sensitive file systems
    }

    private void setTimeXmpFileWarning(long repoTimeStamp, File xmpFile) {
        long lastModified = xmpFile.lastModified();
        timeXmpFileWarning = xmpFile.exists() && lastModified != repoTimeStamp ? TIME_WARNING_STRING : "";
    }

    private void setTimeImageFileWarning(long repoTimeStamp) {
        long lastModified = imageFile == null ? Long.MIN_VALUE : imageFile.lastModified();
        timeImageFileWarning = imageFile != null && imageFile.exists() && lastModified != repoTimeStamp ? TIME_WARNING_STRING : "";
    }

    private void setFilesExists() {
        imageFileExists = existsImageFile();
        xmpFileExists = imageFile != null && resolveXmpFile(imageFile).exists();
    }

    private boolean existsImageFile() {
        return imageFile != null && imageFile.exists();
    }

    public File getImageFile() {
        return imageFile;
    }

    public String getImageFilepath() {
        return imageFile == null ? null : imageFile.getAbsolutePath();
    }

    public Icon getImageFileIcon() {
        return imageFileIcon;
    }

    public boolean getImageFileExists() {
        return imageFileExists;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public String getTimeImageFileInFileSystem() {
        return timeImageFileInFileSystem;
    }

    public String getTimeImageFileInRepository() {
        return timeImageFileInRepository;
    }

    public String getTimeXmpFileInFileSystem() {
        return timeXmpFileInFileSystem;
    }

    public String getTimeXmpFileInRepository() {
        return timeXmpFileInRepository;
    }

    public boolean getXmpFileExists() {
        return xmpFileExists;
    }

    public String getTimeImageFileWarning() {
        return timeImageFileWarning;
    }

    public String getTimeXmpFileWarning() {
        return timeXmpFileWarning;
    }

    public String getThumbnailSizeInfo() {
        return thumbnailSizeInfo;
    }
}
