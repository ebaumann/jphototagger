package de.elmar_baumann.imagemetadataviewer.io;

import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.imagemetadataviewer.view.panels.ThumbnailsPanel;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Utils für I/O.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/30
 */
public class IoUtil {

    /**
     * Startet eine Anwendung und zeigt im Fehlerfall einen Messagedialog.
     * 
     * @param appPath   Pfad zur Anwendung
     * @param arguments Argumente die dem Pfad angehängt werden
     */
    public static void startApplication(String appPath, String arguments) {
        if (!appPath.isEmpty()) {
            String separator = " "; // NOI18N
            String openCommand = appPath + separator + arguments;
            try {
                Runtime.getRuntime().exec(openCommand);
            } catch (IOException ex) {
                Logger.getLogger(ThumbnailsPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,
                    Bundle.getString("IoUtil.ErrorMessage.OpenFile"),
                    Bundle.getString("IoUtil.ErrorMessage.OpenFile.Title"),
                    JOptionPane.ERROR_MESSAGE,
                    AppSettings.getSmallAppIcon());
            }
        }
    }

    /**
     * Liefert die Zeit der letzten Modifikation einer Datei unter
     * Berücksichtigung einer eventuell existierenden XMP-Filialdatei (sidecar
     * file): Existiert eine Filialdatei und sie hat ein neueres Datum, wird
     * ihr Datum geliefert.
     * 
     * @param  filename Dateiname
     * @return          Zeit der letzten Modifikation in Millisekunden seit 1970
     */
    public static long getFileTime(String filename) {
        long fileTime = new File(filename).lastModified();
        long sidecarFileTime = -1;
        String sidecarFilename = XmpMetadata.getSidecarFilename(filename);
        if (sidecarFilename != null) {
            sidecarFileTime = new File(sidecarFilename).lastModified();
        }
        return fileTime > sidecarFileTime ? fileTime : sidecarFileTime;
    }

    /**
     * Liefert Argumente als String für die Kommandozeile zum Anhängen an den
     * Aufruf eines Programms.
     * 
     * @param args Argumente
     * @return     Argumente separiert
     */
    public static String getArgsAsCommandline(Vector<String> args) {
        final String separator = " "; // NOI18N
        StringBuffer arguments = new StringBuffer();
        for (String filename : args) {
            arguments.append(separator + filename);
        }
        return arguments.toString();
    }
}
