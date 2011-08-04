package org.jphototagger.repositoryfilebrowser;

import java.awt.Image;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.Icon;
import org.jphototagger.api.image.ThumbnailProvider;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 *
 *
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
    private Boolean imageFileExists;
    private Boolean xmpFileExists;
    private String timeImageFileWarning;
    private String timeXmpFileWarning;
    private final ImageFileRepository imageFileRepository = Lookup.getDefault().lookup(ImageFileRepository.class);
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    private static final String TIME_WARNING_STRING = Bundle.getString(RepositoryImageFileInfo.class, "RepositoryImageFileInfo.TimeWarningString");

    public RepositoryImageFileInfo(FileNode fileNode) {
        if (fileNode != null) {
            imageFile = fileNode.getFile();
            imageFileIcon = fileNode.getSmallIcon();
            setThumbnail();
            setImageFileTimeStamps();
            setXmpFileTimeStamps();
            setFilesExists();
        }
    }

    private void setThumbnail() {
        ThumbnailProvider thumbnailProvider = Lookup.getDefault().lookup(ThumbnailProvider.class);
        thumbnail = thumbnailProvider.getThumbnail(imageFile);
    }

    private void setImageFileTimeStamps() {
        long repoTimestamp = imageFileRepository.getRepositoryImageFileTimestamp(imageFile);
        timeImageFileInRepository = createDateStringOfTimestamp(repoTimestamp);
        timeImageFileInFileSystem = createDateStringOfFile(imageFile);
        setTimeImageFileWarning(repoTimestamp);
    }

    private void setXmpFileTimeStamps() {
        long repoTimestamp = imageFileRepository.getRepositoryXmpFileTimestamp(imageFile);
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
        timeXmpFileWarning = lastModified != repoTimeStamp ? TIME_WARNING_STRING : "";
    }

    private void setTimeImageFileWarning(long repoTimeStamp) {
        long lastModified = imageFile == null ? Long.MIN_VALUE : imageFile.lastModified();
        timeImageFileWarning = lastModified != repoTimeStamp ? TIME_WARNING_STRING : "";
    }

    private void setFilesExists() {
        imageFileExists = imageFile.exists();
        xmpFileExists = resolveXmpFile(imageFile).exists();
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

    public Boolean getImageFileExists() {
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

    public Boolean getXmpFileExists() {
        return xmpFileExists;
    }

    public String getTimeImageFileWarning() {
        return timeImageFileWarning;
    }

    public String getTimeXmpFileWarning() {
        return timeXmpFileWarning;
    }
}