package org.jphototagger.program;

import java.awt.EventQueue;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jphototagger.program.app.AppInit;


/**
 * @author Elmar Baumann
 */
public final class Main {

    private static final int MIN_JAVA_MAJOR_VERSION = 1;
    private static final int MIN_JAVA_MINOR_VERSION = 7;

    public static void main(String[] args) {
        if (checkJavaVersion()) {
            AppInit.INSTANCE.init(args);
        }
    }

    // Version check code does not use other classes from project to minimize the risk of incompatibility
    // (due unknown build computers, JPhotoTagger does not use the javac option "-bootclasspath")
    private static boolean checkJavaVersion() {
        Logger logger = Logger.getLogger(Main.class.getName());
        logger.info("Checking Java version");
        String version = System.getProperty("java.version"); //NOI18N
        String[] versionToken = version.split("\\."); //NOI18N
        if (versionToken.length < 2) {
            logger.log(Level.SEVERE, "Can''t get valid Java Version! Got: ''{0}''", version); //NOI18N
            return true;
        }
        int major;
        int minor;
        try {
            major = Integer.parseInt(versionToken[0]);
            minor = Integer.parseInt(versionToken[1]);
            boolean tooOld = major < MIN_JAVA_MAJOR_VERSION || major == MIN_JAVA_MAJOR_VERSION && minor < MIN_JAVA_MINOR_VERSION;
            if (tooOld) {
                errorMessageJavaVersion(version);
                return false;
            }
        } catch (Throwable t) {
            logger.log(Level.SEVERE, null, t);
        }
        return true;
    }

    private static void errorMessageJavaVersion(final String version) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Java version ''{0}'' is too old! The required minimum Java version is ''{1}.{2}''.", new Object[]{version, MIN_JAVA_MAJOR_VERSION, MIN_JAVA_MINOR_VERSION});
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/Bundle");
                String message = MessageFormat.format(bundle.getString("Main.Error.JavaVersion.Message"), version, MIN_JAVA_MAJOR_VERSION, MIN_JAVA_MINOR_VERSION);
                String title = bundle.getString("Main.Error.JavaVersion.MessageTitle");
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private Main() {
    }
}
