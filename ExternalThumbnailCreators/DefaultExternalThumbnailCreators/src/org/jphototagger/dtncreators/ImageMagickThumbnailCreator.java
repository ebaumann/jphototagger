package org.jphototagger.dtncreators;

import java.io.File;
import java.util.ResourceBundle;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.services.plugin.ExternalThumbnailCreator;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageMagickThumbnailCreator implements ExternalThumbnailCreator {

    private static final String COMMANDLINE_PARAMETERS = "-thumbnail %ix%i -auto-orient \"%s\" jpg:-";
    private final ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/dtncreators/Bundle");
    private final FileChooser fileChooser = createFileChooser();

    @Override
    public String getThumbnailCreationCommand() {
        ImageMagickThumbnailCreatorDialog dialog = new ImageMagickThumbnailCreatorDialog();
        
        dialog.setVisible(true);
        
        if (dialog.isBrowse()) {
            Util.browse("http://www.imagemagick.org/");
            return null;
        } else if (dialog.isChooseConvert()) {
            File convertExecutable = fileChooser.chooseFileFixedName();
            
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
    
    private FileChooser createFileChooser() {
        String convertFileName = getConvertFileName();
        String convertFileDescription = getConvertFileDescription();
        String fileChooserTitle = bundle.getString("ImageMagickThumbnailCreator.ChooseFile.Dialogtitle");

        return new FileChooser.Builder(convertFileName)
                .fileChooserTitle(fileChooserTitle)
                .fileDescription(convertFileDescription)
                .build();
    }
    
    private String getConvertFileName() {
        return SystemUtil.isWindows() ? "convert.exe" : "convert";
    }
    
    private String getConvertFileDescription() {
        String bundleKeyDescription = SystemUtil.isWindows() 
                                          ? "ImageMagickThumbnailCreator.FileFilter.Description.Windows" 
                                          : "ImageMagickThumbnailCreator.FileFilter.Description.OtherOS";
        
        return bundle.getString(bundleKeyDescription);
    }
}
