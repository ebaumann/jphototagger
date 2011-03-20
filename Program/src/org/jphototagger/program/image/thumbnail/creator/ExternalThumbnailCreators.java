package org.jphototagger.program.image.thumbnail.creator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import org.jphototagger.lib.io.filefilter.AcceptExactFilenameNameFileFilter;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.program.resource.JptBundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExternalThumbnailCreators {

    private static final List<ExternalThumbnailCreator> THUMBNAIL_CREATORS = new ArrayList<ExternalThumbnailCreator>();
    public static final ExternalThumbnailCreators INSTANCE = new ExternalThumbnailCreators();

    static {
        if (SystemUtil.isWindows()) {
            THUMBNAIL_CREATORS.add(new DefaultExternalThumbnailCreator(
                    "convert.exe",
                    "ExternalThumbnailCreator.ImageMagick.Windows.Description",
                    "-thumbnail %ix%i -auto-orient \"%s\" jpg:-",
                    "http://www.imagemagick.org/"
                    ));
        } else {
            THUMBNAIL_CREATORS.add(new DefaultExternalThumbnailCreator(
                    "convert",
                    "ExternalThumbnailCreator.ImageMagick.Description",
                    "-thumbnail %ix%i -auto-orient \"%s\" jpg:-",
                    "http://www.imagemagick.org/"
                    ));
        }
    }

    public static List<ExternalThumbnailCreator> getCreators() {
        return Collections.unmodifiableList(THUMBNAIL_CREATORS);
    }

    private ExternalThumbnailCreators() {
    }

    private static class DefaultExternalThumbnailCreator implements ExternalThumbnailCreator {

        final String exactFilename;
        final String description;
        final String downloadUrl;
        final String creationCommand;
        final FileFilter fileFilter;

        DefaultExternalThumbnailCreator(String exactFilename, String descriptionBundleString, String creationCommand, String downloadUrl) {
            this.exactFilename = exactFilename;
            this.description = JptBundle.INSTANCE.getString(descriptionBundleString);
            this.creationCommand = creationCommand;
            this.downloadUrl = downloadUrl;
            fileFilter = createFileFilter(exactFilename, description);
        }

        @Override
        public String getDisplayName() {
            return description;
        }

        @Override
        public FileFilter getThumbnailCreatorFileFilter() {
            return fileFilter;
        }

        @Override
        public String getThumbnailCreationCommand(String creationProgramFilePath) {
            return "\"" + creationProgramFilePath + "\" " + creationCommand;
        }

        @Override
        public String getDownloadUrl() {
            return downloadUrl;
        }

        @Override
        public String toString() {
            return description;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof DefaultExternalThumbnailCreator)) {
                return false;
            }

            DefaultExternalThumbnailCreator other = (DefaultExternalThumbnailCreator) obj;

            return description == null
                    ? other.description == null
                    : description.equals(other.description);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 13 * hash + (this.description != null ? this.description.hashCode() : 0);
            return hash;
        }

        private FileFilter createFileFilter(String exactFilename, String description) {
            AcceptExactFilenameNameFileFilter filter = new AcceptExactFilenameNameFileFilter(exactFilename);

            return filter.forFileChooser(description);
        }
    }
}
