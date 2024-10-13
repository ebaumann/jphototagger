package org.jphototagger.tcc.def.scripts.unix;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jphototagger.api.storage.PreferencesDirectoryProvider;
import org.jphototagger.domain.thumbnails.ExternalThumbnailCreationCommand;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.SystemUtil;
import org.jphototagger.tcc.def.scripts.ScriptWriter;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExternalThumbnailCreationCommand.class)
public final class ImageMagickDcrawThumbnailCreator implements ExternalThumbnailCreationCommand {

    @Override
    public String getThumbnailCreationCommand() {
        ImageMagickDcrawThumbnailCreatorDialog dialog = new ImageMagickDcrawThumbnailCreatorDialog();

        dialog.setVisible(true);

        if (dialog.isAccepted()) {
            File dcraw = dialog.getDcraw();
            File identify = dialog.getIdentify();
            File magick = dialog.getMagick();
            File mplayer = dialog.getMplayer();

            return createCommand(dcraw, identify, magick, mplayer);
        }

        return null;
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(ImageMagickDcrawThumbnailCreator.class, "ImageMagickDcrawThumbnailCreator.DisplayName");
    }

    @Override
    public boolean isEnabled() {
        return !SystemUtil.isWindows();
    }

    private String createCommand(File dcraw, File identify, File magick, File mplayer) {
        if (dcraw == null || identify == null || magick == null) {
            return null;
        }

        PreferencesDirectoryProvider provider = Lookup.getDefault().lookup(PreferencesDirectoryProvider.class);
        File userDirectory = provider.getPluginPreferencesDirectory();

        if (userDirectory == null) {
            errorMessageUserDirectory();
            return null;
        }

        ScriptWriter scriptWriter = new ScriptWriter();
        String scriptName = mplayer == null ? "image_magick_dcraw.sh" : "image_magick_dcraw_mplayer.sh";
        String scriptPath = userDirectory.getAbsolutePath() + File.separator + scriptName;

        try {
            setReplace(scriptWriter, dcraw, identify, magick, mplayer);
            writeScript(scriptName, scriptWriter, new File(scriptPath));

            return "\"" + scriptPath + "\" \"%s\" %i";
        } catch (Throwable t) {
            Logger.getLogger(ImageMagickDcrawThumbnailCreator.class.getName()).log(Level.SEVERE, null, t);
            return null;
        }
    }

    private void setReplace(ScriptWriter scriptWriter, File dcraw, File identify, File magick, File mplayer) {
        scriptWriter.addReplace("${dcraw}", dcraw.getAbsolutePath());
        scriptWriter.addReplace("${identify}", identify.getAbsolutePath());
        scriptWriter.addReplace("${magick}", magick.getAbsolutePath());

        if (mplayer != null) {
            scriptWriter.addReplace("${mplayer}", mplayer.getAbsolutePath());
        }
    }

    private void writeScript(String templateName, ScriptWriter scriptWriter, File scriptFile) throws Exception {
        try {
            String readScript = scriptWriter.readScript("/org/jphototagger/tcc/def/scripts/unix/" + templateName);

            readScript = scriptWriter.replaceIn(readScript);
            FileUtil.writeStringAsFile(readScript, scriptFile);
            scriptFile.setExecutable(true);
        } catch (Throwable t) {
            errorMessageGetScript();
            throw t;
        }
    }

    private void errorMessageUserDirectory() {
        String message = Bundle.getString(ImageMagickDcrawThumbnailCreator.class, "ImageMagickDcrawThumbnailCreator.Error.UserDirectory");
        String title = Bundle.getString(ImageMagickDcrawThumbnailCreator.class, "ImageMagickDcrawThumbnailCreator.Error.Title");

        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void errorMessageGetScript() {
        String message = Bundle.getString(ImageMagickDcrawThumbnailCreator.class, "ImageMagickDcrawThumbnailCreator.Error.GetScript");
        String title = Bundle.getString(ImageMagickDcrawThumbnailCreator.class, "ImageMagickDcrawThumbnailCreator.Error.Title");

        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
