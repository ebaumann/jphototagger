package org.jphototagger.program.misc;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JMenuItem;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.dialog.HelpBrowser;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.event.HelpBrowserEvent;
import org.jphototagger.lib.event.listener.HelpBrowserListener;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.Main;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.logging.AppLoggingSystem;
import org.jphototagger.program.resource.GUI;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class HelpController implements ActionListener, HelpBrowserListener {

    private static final String HELP_CONTENTS_URL = "/org/jphototagger/program/resource/doc/de/contents.xml";
    private static final String KEY_CURRENT_URL = HelpController.class.getName() + ".CurrentURL";
    private String currentUrl = Lookup.getDefault().lookup(Preferences.class).getString(KEY_CURRENT_URL);
    private static final Logger LOGGER = Logger.getLogger(HelpController.class.getName());

    public HelpController() {
        listen();
    }

    private JMenuItem getAcceleratorKeysMenuItem() {
        return GUI.getAppFrame().getMenuItemAcceleratorKeys();
    }

    private JMenuItem getHelpMenuItem() {
        return GUI.getAppFrame().getMenuItemHelp();
    }

    private JMenuItem getOpenPdfUserManualMenuItem() {
        return GUI.getAppFrame().getMenuItemOpenPdfUserManual();
    }

    private JMenuItem getBrowseWebsiteMenuItem() {
        return GUI.getAppFrame().getMenuItemBrowseWebsite();
    }

    private JMenuItem getBrowseUserForumMenuItem() {
        return GUI.getAppFrame().getMenuItemBrowseUserForum();
    }

    private JMenuItem getBrowseChangelogMenuItem() {
        return GUI.getAppFrame().getMenuItemBrowseChangelog();
    }

    private JMenuItem getSendBugMailMenuItem() {
        return GUI.getAppFrame().getMenuItemSendBugMail();
    }

    private JMenuItem getSendFeatureMailMenuItem() {
        return GUI.getAppFrame().getMenuItemSendFeatureMail();
    }

    private void listen() {
        HelpBrowser.INSTANCE.addHelpBrowserListener(this);
        getOpenPdfUserManualMenuItem().addActionListener(this);
        getAcceleratorKeysMenuItem().addActionListener(this);
        getBrowseUserForumMenuItem().addActionListener(this);
        getBrowseWebsiteMenuItem().addActionListener(this);
        getBrowseChangelogMenuItem().addActionListener(this);
        getSendBugMailMenuItem().addActionListener(this);
        getSendFeatureMailMenuItem().addActionListener(this);
    }

    @Override
    public void actionPerformed(HelpBrowserEvent action) {
        if (action == null) {
            throw new NullPointerException("action == null");
        }

        if (action.getType().equals(HelpBrowserEvent.Type.URL_CHANGED)) {
            setCurrentUrl(action);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source.equals(getHelpMenuItem())) {
            showHelp();
        } else if (source == getAcceleratorKeysMenuItem()) {
            showAcceleratorKeyHelp();
        } else if (source == getOpenPdfUserManualMenuItem()) {
            openPdfUserManual();
        } else if (source == getBrowseUserForumMenuItem()) {
            browse(AppInfo.URI_USER_FORUM);
        } else if (source == getBrowseWebsiteMenuItem()) {
            browse(AppInfo.URI_WEBSITE);
        } else if (source == getBrowseChangelogMenuItem()) {
            browse(AppInfo.URI_CHANGELOG);
        } else if (source == getSendBugMailMenuItem()) {
            sendBugMail();
        } else if (source == getSendFeatureMailMenuItem()) {
            sendFeatureMail();
        }
    }

    private void setCurrentUrl(HelpBrowserEvent action) {
        URL url = action.getUrl();

        if (!url.getProtocol().startsWith("http")) {
            currentUrl = HelpBrowser.getLastPathComponent(url);

            Preferences storage = Lookup.getDefault().lookup(Preferences.class);

            storage.setString(KEY_CURRENT_URL, currentUrl);
        }
    }

    private void initHelp() {
        if ((HelpBrowser.INSTANCE.getContentsUrl() == null)
                || !HelpBrowser.INSTANCE.getContentsUrl().equals(HELP_CONTENTS_URL)) {
            HelpBrowser.INSTANCE.setContentsUrl(HELP_CONTENTS_URL);
        }
    }

    private void showHelp() {
        initHelp();

        if (!currentUrl.isEmpty()) {
            HelpBrowser.INSTANCE.setDisplayUrl(currentUrl);
        }

        ComponentUtil.show(HelpBrowser.INSTANCE);
    }

    private void showAcceleratorKeyHelp() {
        initHelp();
        HelpBrowser.INSTANCE.setDisplayUrl("accelerator_keys.html");
        ComponentUtil.show(HelpBrowser.INSTANCE);
    }

    private void sendBugMail() {
        String mailto = AppInfo.MAIL_TO_ADDRESS_BUGS;
        String subject = AppInfo.MAIL_SUBJECT_BUGS;
        String message = Bundle.getString(HelpController.class, "SendMailController.Info.AttachLogfile", AppLoggingSystem.getAllMessagesLogfilePath());

        sendMail(mailto, subject, message);
    }

    private void sendFeatureMail() {
        sendMail(AppInfo.MAIL_TO_ADDRESS_FEATURES, AppInfo.MAIL_SUBJECT_FEATURES, null);
    }

    private void sendMail(String to, String subject, String body) {
        try {
            URI uri = getMailtoUri(to, subject, body);

            LOGGER.log(Level.INFO, "Sending email to this URI: ''{0}''", uri);
            Desktop.getDesktop().mail(uri);
        } catch (Exception ex) {
            Logger.getLogger(HelpController.class.getName()).log(Level.SEVERE, null, ex);
            String message = Bundle.getString(HelpController.class, "SendMailController.Error.SendMail");
            MessageDisplayer.error(null, message);
        }
    }

    private URI getMailtoUri(String to, String subject, String body) throws URISyntaxException, UnsupportedEncodingException {
        String bodyPart = ((body == null) || body.trim().isEmpty())
                ? ""
                : "&body=" + body;

        return new URI("mailto", to + "?subject=" + subject + bodyPart, null);
    }

    private void browse(String uri) {
        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (Exception ex) {
            Logger.getLogger(HelpController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void openPdfUserManual() {
        File manual = getPdfUserManualPath();

        if (manual == null) {
            return;
        }

        try {
            Desktop.getDesktop().open(manual);
        } catch (IOException ex) {
            Logger.getLogger(HelpController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the PDF user manual file.
     *
     * @return file or null if the file does not exist
     */
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
        } catch (Exception ex) {
            Logger.getLogger(HelpController.class.getName()).log(Level.SEVERE, null, ex);
        }

        String message = Bundle.getString(HelpController.class, "HelpController.Error.NoPdfFile", manualPath);
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
