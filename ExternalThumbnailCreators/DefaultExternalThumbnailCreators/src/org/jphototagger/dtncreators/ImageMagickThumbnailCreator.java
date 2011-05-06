package org.jphototagger.dtncreators;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.FileUtil.FileChooserProperties;
import org.jphototagger.lib.io.filefilter.AcceptExactFilenameNameFileFilter;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.services.ExternalThumbnailCreator;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageMagickThumbnailCreator implements ExternalThumbnailCreator {

    private final ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/dtncreators/Bundle");
    private static final String COMMANDLINE_PARAMETERS = "-thumbnail %ix%i -auto-orient \"%s\" jpg:-";
    private File lastDir;

    @Override
    public String getThumbnailCreationCommand() {
        ImageMagickThumbnailCreatorDialog dialog = new ImageMagickThumbnailCreatorDialog();
        
        dialog.setVisible(true);
        
        if (dialog.isBrowse()) {
            browseImageMagickSite();
            return null;
        } else if (dialog.isChooseConvert()) {
            File convertExecutable = chooseConvertExecutable();
            
            return createCommand(convertExecutable);
        }
        
        return null;
    }

    @Override
    public String getDisplayName() {
        return bundle.getString("ImageMagickThumbnailCreator.DisplayName");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
    private String createCommand(File file) {
        if (file == null) {
            return null;
        }
        
        return "\"" + file.getAbsolutePath() + "\" " + COMMANDLINE_PARAMETERS;

    }
    
    private File chooseConvertExecutable() {
        FileChooserProperties fcProps = new FileChooserProperties();
        String title = bundle.getString("ImageMagickThumbnailCreator.ChooseFile.Dialogtitle");

        fcProps.dialogTitle(title);
        fcProps.currentDirectoryPath(lastDir == null ? "" : lastDir.getAbsolutePath());
        fcProps.multiSelectionEnabled(false);
        fcProps.fileFilter(createFileFilter());
        fcProps.fileSelectionMode(JFileChooser.FILES_ONLY);

        File file = FileUtil.chooseFile(fcProps);

        if (file != null) {
            lastDir = file.getParentFile();
        }
        
        return file;
    }
    
    private FileFilter createFileFilter() {
        boolean isWindows = SystemUtil.isWindows();
        String exactFilename = isWindows 
                                   ? "convert.exe" 
                                   : "convert";
        String bundleKeyDescription = isWindows 
                                          ? "ImageMagickThumbnailCreator.FileFilter.Description.Windows" 
                                          : "ImageMagickThumbnailCreator.FileFilter.Description.OtherOS";
        String description = bundle.getString(bundleKeyDescription);
        AcceptExactFilenameNameFileFilter filter = new AcceptExactFilenameNameFileFilter(exactFilename);

        return filter.forFileChooser(description);
    }
    
    private void browseImageMagickSite() {
        try {
            Desktop.getDesktop().browse(new URI("http://www.imagemagick.org/"));
        } catch (Throwable t) {
            Logger.getLogger(ImageMagickThumbnailCreator.class.getName()).log(Level.SEVERE, null, t);
        }
    }
}
