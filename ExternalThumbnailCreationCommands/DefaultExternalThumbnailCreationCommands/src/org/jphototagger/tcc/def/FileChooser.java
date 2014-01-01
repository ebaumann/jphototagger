package org.jphototagger.tcc.def;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.jphototagger.lib.io.filefilter.AcceptExactFilenamesFileFilter;
import org.jphototagger.lib.swing.FileChooserHelper;
import org.jphototagger.lib.swing.FileChooserProperties;

/**
 * @author Elmar Baumann
 */
public final class FileChooser {

    private final Set<String> fixedFileNames;
    private final String fileDescription;
    private final String fileChooserTitle;
    private String fileChooserDirPath;

    private FileChooser(Builder builder) {
        fixedFileNames = builder.fixedFileNames;
        fileDescription = builder.fileDescription;
        fileChooserTitle = builder.fileChooserTitle;
        fileChooserDirPath = builder.fileChooserDirPath;
    }

    public File chooseFileFixedName() {
        FileChooserProperties fcProps = new FileChooserProperties();

        fcProps.dialogTitle(fileChooserTitle);
        fcProps.currentDirectoryPath(fileChooserDirPath);
        fcProps.multiSelectionEnabled(false);
        fcProps.fileFilter(createFileFilter());
        fcProps.fileSelectionMode(JFileChooser.FILES_ONLY);

        File file = FileChooserHelper.chooseFile(fcProps);

        if (file != null) {
            fileChooserDirPath = file.getParentFile().getAbsolutePath();
        }

        return file;
    }

    private FileFilter createFileFilter() {
        AcceptExactFilenamesFileFilter filter = new AcceptExactFilenamesFileFilter(fixedFileNames);

        return filter.forFileChooser(fileDescription);
    }

    public static class Builder {

        private final Set<String> fixedFileNames;
        private String fileDescription = "";
        private String fileChooserTitle = "";
        private String fileChooserDirPath = "";

        public Builder(Set<String> fixedFileNames) {
            if (fixedFileNames == null) {
                throw new NullPointerException("fixedFileNames == null");
            }

            this.fixedFileNames = new HashSet<>(fixedFileNames);
        }

        public Builder fileChooserDirPath(String fileChooserDirPath) {
            if (fileChooserDirPath == null) {
                throw new NullPointerException("fileChooserDirPath == null");
            }

            this.fileChooserDirPath = fileChooserDirPath;
            return this;
        }

        public Builder fileChooserTitle(String fileChooserTitle) {
            if (fileChooserTitle == null) {
                throw new NullPointerException("fileChooserTitle == null");
            }

            this.fileChooserTitle = fileChooserTitle;
            return this;
        }

        public Builder fileDescription(String fileDescription) {
            if (fileDescription == null) {
                throw new NullPointerException("fileDescription == null");
            }

            this.fileDescription = fileDescription;
            return this;
        }

        public FileChooser build() {
            return new FileChooser(this);
        }
    }
}
