package org.jphototagger.tcc.def;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import org.jphototagger.domain.thumbnails.ExternalThumbnailCreationCommand;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.SystemUtil;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExternalThumbnailCreationCommand.class)
public final class ImageMagickThumbnailCreator implements ExternalThumbnailCreationCommand {

    private static final String COMMANDLINE_PARAMETERS = "-thumbnail %ix%i -auto-orient \"%s\" jpg:-";
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
        return Bundle.getString(ImageMagickThumbnailCreator.class, "ImageMagickThumbnailCreator.DisplayName");
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
        String fileChooserTitle = Bundle.getString(ImageMagickThumbnailCreator.class, "ImageMagickThumbnailCreator.ChooseFile.Dialogtitle");

        return new FileChooser.Builder(new HashSet<>(Arrays.asList(convertFileName)))
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

        return Bundle.getString(ImageMagickThumbnailCreator.class, bundleKeyDescription); // NOI18N
    }
}
