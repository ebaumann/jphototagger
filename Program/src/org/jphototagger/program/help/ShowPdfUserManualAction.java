package org.jphototagger.program.help;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.Main;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
final class ShowPdfUserManualAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ShowPdfUserManualAction.class.getName());

    ShowPdfUserManualAction() {
        super(Bundle.getString(ShowPdfUserManualAction.class, "ShowPdfUserManualAction.Name"));
        putValue(SMALL_ICON, Icons.getIcon("icon_pdf_manual.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        openPdfUserManual();
    }

    private void openPdfUserManual() {
        File manualPath = getPdfUserManualPath();

        if (manualPath != null) {
            DesktopUtil.open(manualPath, "JPhotoTagger.PdfViewer");
        }
    }

    private static File getPdfUserManualPath() {
        String manualPath = "";

        try {
            File jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            logJar(jarPath);

            if (jarPath.exists() && (jarPath.getParentFile() != null)) {
                File dir = jarPath.getParentFile();
                String pathPrefix = dir.getAbsolutePath() + File.separator + "Manual";

                // Trying to get Locale specific manual
                manualPath = pathPrefix + "_" + Locale.getDefault().getLanguage() + ".pdf";

                File fileLocaleSensitive = new File(manualPath);

                logIfNotExists(fileLocaleSensitive);

                if (fileLocaleSensitive.exists()) {
                    return fileLocaleSensitive;
                }

                // Trying to get default language manual
                manualPath = pathPrefix + "_de.pdf";

                File fileDefault = new File(manualPath);

                logIfNotExists(fileDefault);

                if (fileDefault.exists()) {
                    return fileDefault;
                }
            }
        } catch (Throwable t) {
            Logger.getLogger(ShowPdfUserManualAction.class.getName()).log(Level.SEVERE, null, t);
        }

        String message = Bundle.getString(ShowPdfUserManualAction.class, "HelpController.Error.NoPdfFile", manualPath);
        MessageDisplayer.error(null, message);

        return null;
    }

    private static void logJar(File jarPath) {
        logJarFile(jarPath);
        logJarDir(jarPath.getParentFile());
        logIfNotExists(jarPath);
        logIfNotExists(jarPath.getParentFile());
    }

    private static void logJarDir(File jarPath) {
        File parentFile = jarPath.getParentFile();

        LOGGER.log(Level.FINEST, "Got folder to JAR file: ''{0}''", parentFile);
    }

    private static void logJarFile(File jarPath) {
        LOGGER.log(Level.FINEST, "Got path to JAR file: ''{0}''", jarPath);
    }

    private static void logIfNotExists(File file) {
        if (file == null) {
            return;
        }

        if (!file.exists()) {
            LOGGER.log(Level.FINEST, "File ''{0}'' does not exist", file);
        }
    }
}
