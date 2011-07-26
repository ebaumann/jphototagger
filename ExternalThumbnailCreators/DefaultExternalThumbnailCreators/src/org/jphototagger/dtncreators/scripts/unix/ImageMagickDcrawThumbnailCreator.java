package org.jphototagger.dtncreators.scripts.unix;

import java.io.File;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jphototagger.dtncreators.Util;
import org.jphototagger.dtncreators.scripts.ScriptWriter;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.services.plugin.ExternalThumbnailCreator;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageMagickDcrawThumbnailCreator implements ExternalThumbnailCreator {

    private final ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/dtncreators/scripts/unix/Bundle");

    @Override
    public String getThumbnailCreationCommand() {
        ImageMagickDcrawThumbnailCreatorDialog dialog = new ImageMagickDcrawThumbnailCreatorDialog();

        dialog.setVisible(true);

        if (dialog.isAccepted()) {
            File dcraw = dialog.getDcraw();
            File identify = dialog.getIdentify();
            File convert = dialog.getConvert();
            File mplayer = dialog.getMplayer();

            return createCommand(dcraw, identify, convert, mplayer);
        }

        return null;
    }

    @Override
    public String getDisplayName() {
        return bundle.getString("ImageMagickDcrawThumbnailCreator.DisplayName");
    }

    @Override
    public boolean isEnabled() {
        return !SystemUtil.isWindows();
    }

    private String createCommand(File dcraw, File identify, File convert, File mplayer) {
        if (dcraw == null || identify == null || convert == null) {
            return null;
        }

        File userDirectory = Util.lookupUserDirectory();

        if (userDirectory == null) {
            errorMessageUserDirectory();
            return null;
        }

        ScriptWriter scriptWriter = new ScriptWriter();
        String scriptName = mplayer == null ? "image_magick_dcraw.sh" : "image_magick_dcraw_mplayer.sh";
        String scriptPath = userDirectory.getAbsolutePath() + File.separator + scriptName;

        try {
            setReplace(scriptWriter, dcraw, identify, convert, mplayer);
            writeScript(scriptName, scriptWriter, new File(scriptPath));

            return "\"" + scriptPath + "\" \"%s\" %i";
        } catch (Exception ex) {
            Logger.getLogger(ImageMagickDcrawThumbnailCreator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void setReplace(ScriptWriter scriptWriter, File dcraw, File identify, File convert, File mplayer) {
        scriptWriter.addReplace("${dcraw}", dcraw.getAbsolutePath());
        scriptWriter.addReplace("${identify}", identify.getAbsolutePath());
        scriptWriter.addReplace("${convert}", convert.getAbsolutePath());

        if (mplayer != null) {
            scriptWriter.addReplace("${mplayer}", mplayer.getAbsolutePath());
        }
    }

    private void writeScript(String templateName, ScriptWriter scriptWriter, File scriptFile) throws Exception {
        try {
            String readScript = scriptWriter.readScript("/org/jphototagger/dtncreators/scripts/unix/" + templateName);

            readScript = scriptWriter.replaceIn(readScript);
            FileUtil.writeStringAsFile(readScript, scriptFile);
            scriptFile.setExecutable(true);
        } catch (Exception ex) {
            errorMessageGetScript();
            throw ex;
        }
    }

    private void errorMessageUserDirectory() {
        String message = bundle.getString("ImageMagickDcrawThumbnailCreator.Error.UserDirectory");
        String title = bundle.getString("ImageMagickDcrawThumbnailCreator.Error.Title");

        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void errorMessageGetScript() {
        String message = bundle.getString("ImageMagickDcrawThumbnailCreator.Error.GetScript");
        String title = bundle.getString("ImageMagickDcrawThumbnailCreator.Error.Title");

        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
