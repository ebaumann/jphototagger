package org.jphototagger.dtncreators;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.jphototagger.lib.io.FileChooserHelper;
import org.jphototagger.lib.io.FileChooserProperties;
import org.jphototagger.lib.io.filefilter.AcceptExactFilenameNameFileFilter;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileChooser {

    private final String fixedFileFilename;
    private final String fileDescription;
    private final String fileChooserTitle;
    private String fileChooserDirPath;

    private FileChooser(Builder builder) {
        fixedFileFilename = builder.fixedFileName;
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
        AcceptExactFilenameNameFileFilter filter = new AcceptExactFilenameNameFileFilter(fixedFileFilename);

        return filter.forFileChooser(fileDescription);
    }
    
    public static class Builder {
        private final String fixedFileName;
        private String fileDescription = "";
        private String fileChooserTitle = "";
        private String fileChooserDirPath = "";

        public Builder(String fixedFileName) {
            if (fixedFileName == null) {
                throw new NullPointerException("fixedFileName == null");
            }
            
            this.fixedFileName = fixedFileName;
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
