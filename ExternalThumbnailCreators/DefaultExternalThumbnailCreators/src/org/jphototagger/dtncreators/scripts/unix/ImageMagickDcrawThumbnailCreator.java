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
import org.jphototagger.services.ExternalThumbnailCreator;

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
            return createCommand(dialog.getDcraw(), dialog.getIdentify(), dialog.getConvert());
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
    
    private String createCommand(File dcraw, File identify, File convert) {
        if (dcraw == null || identify == null || convert == null) {
            return null;
        }
        
        File userDirectory = Util.lookupUserDirectory();
        
        if (userDirectory == null) {
            errorMessageUserDirectory();
            return null;
        }
        
        ScriptWriter scriptWriter = new ScriptWriter();
        String scriptPath = userDirectory.getAbsolutePath() + File.separator + "image_magick_dcraw.sh";
        
        try {
            setReplace(scriptWriter, dcraw, identify, convert);
            writeScript(scriptWriter, new File(scriptPath));
            
            return "\"" + scriptPath + "\" \"%s\" %i";
        } catch (Exception ex) {
            Logger.getLogger(ImageMagickDcrawThumbnailCreator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void setReplace(ScriptWriter scriptWriter, File dcraw, File identify, File convert) {
        scriptWriter.setReplace("${dcraw}", dcraw.getAbsolutePath());
        scriptWriter.setReplace("${identify}", identify.getAbsolutePath());
        scriptWriter.setReplace("${convert}", convert.getAbsolutePath());
    }

    private void writeScript(ScriptWriter scriptWriter, File scriptFile) throws Exception {
        try {
            String readScript = scriptWriter.readScript("/org/jphototagger/dtncreators/scripts/unix/image_magick_dcraw.sh");
            
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
