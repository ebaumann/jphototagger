package de.elmar_baumann.imagemetadataviewer;

import com.imagero.reader.AbstractImageReader;
import de.elmar_baumann.lib.componentutil.LookAndFeelUtil;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.imagemetadataviewer.resource.ImageProperties;
import de.elmar_baumann.imagemetadataviewer.view.frames.AppFrame;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.resource.Settings;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Startet das Programm.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/02/17
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        init();
        SplashScreen.setMessageToSplashScreen(Bundle.getString("Main.Main.InformationMessage.SplashScreen.InitGui"));
        showFrame();
    }

    private static void showFrame() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new AppFrame().setVisible(true);
            }
        });
    }

    private static void init() {
        LookAndFeelUtil.setSystemLookAndFeel();
        PersistentSettings.getInstance().setAppName("ImageMetaDataViewer");  // NOI18N
        Settings.getInstance().setIconImagesPath(AppSettings.getAppIconPaths());
        initLogger();
        SplashScreen.setMessageToSplashScreen(Bundle.getString("Main.Init.InformationMessage.SplashScreen.ConnectToDatabase"));
        initDatabase();
        AbstractImageReader.install(ImageProperties.class);
    }

    private static void initLogger() {
        try {
            FileUtil.ensureDirectoryExists(PersistentSettings.getInstance().getDirectoryName());
            Handler fileHandler = new FileHandler(AppSettings.getLogfileName());
            Level level = Level.parse(UserSettings.getInstance().getLogLevel());
            fileHandler.setLevel(level);
            fileHandler.setFormatter((Formatter) UserSettings.getInstance().getLogfileFormatterClass().newInstance());
            Logger.getLogger("de.elmar_baumann").addHandler(fileHandler); // NOI18N
            Logger.getLogger("de.elmar_baumann").setLevel(level); // NOI18N
        } catch (InstantiationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void initDatabase() {
        Database db = Database.getInstance();
        db.connect();
        if (!db.isConnected()) {
            JOptionPane.showMessageDialog(null,
                Bundle.getString("Main.InitDatabase.ErrorMessage.ConnectionFailed"), // NOI18N
                Bundle.getString("Main.InitDatabase.ErrorMessage.ConnectionFailed.Title"), // NOI18N
                JOptionPane.ERROR_MESSAGE,
                AppSettings.getSmallAppIcon());
            System.exit(1);
        }
    }
}

// TODO: Delete XmpUpdaterRenameInColumnsArray 
