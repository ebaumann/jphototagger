package org.jphototagger.program.controller.misc;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.dialog.HelpBrowser;
import org.jphototagger.lib.event.HelpBrowserEvent;
import org.jphototagger.lib.event.listener.HelpBrowserListener;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLoggingSystem;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.Main;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;

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

import javax.swing.JMenuItem;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerHelp
        implements ActionListener, HelpBrowserListener {
    private static final String HELP_CONTENTS_URL =
        JptBundle.INSTANCE.getString("Help.Url.Contents");
    private static final String KEY_CURRENT_URL =
        ControllerHelp.class.getName() + ".CurrentURL";
    private String currentUrl =
        UserSettings.INSTANCE.getSettings().getString(KEY_CURRENT_URL);

    public ControllerHelp() {
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
            UserSettings.INSTANCE.getSettings().set(currentUrl,
                    KEY_CURRENT_URL);
            UserSettings.INSTANCE.writeToFile();
        }
    }

    private void initHelp() {
        if ((HelpBrowser.INSTANCE.getContentsUrl() == null)
                ||!HelpBrowser.INSTANCE.getContentsUrl().equals(
                    HELP_CONTENTS_URL)) {
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
        HelpBrowser.INSTANCE.setDisplayUrl(
            JptBundle.INSTANCE.getString("Help.Url.AcceleratorKeys"));
        ComponentUtil.show(HelpBrowser.INSTANCE);
    }

    private void sendBugMail() {
        sendMail(
            AppInfo.MAIL_TO_ADDRESS_BUGS, AppInfo.MAIL_SUBJECT_BUGS,
            JptBundle.INSTANCE.getString(
                "ControllerSendMail.Info.AttachLogfile",
                AppLoggingSystem.geLogfilePathAllMessages()));
    }

    private void sendFeatureMail() {
        sendMail(AppInfo.MAIL_TO_ADDRESS_FEATURES,
                 AppInfo.MAIL_SUBJECT_FEATURES, null);
    }

    private void sendMail(String to, String subject, String body) {
        try {
            URI uri = getMailtoUri(to, subject, body);

            AppLogger.logInfo(ControllerHelp.class,
                              "ControllerSendMail.Info.SendMail.Uri", uri);
            Desktop.getDesktop().mail(uri);
        } catch (Exception ex) {
            MessageDisplayer.error(null, "ControllerSendMail.Error.SendMail");
            AppLogger.logSevere(ControllerHelp.class, ex);
        }
    }

    private URI getMailtoUri(String to, String subject, String body)
            throws URISyntaxException, UnsupportedEncodingException {
        String bodyPart = ((body == null) || body.trim().isEmpty())
                          ? ""
                          : "&body=" + body;

        return new URI("mailto", to + "?subject=" + subject + bodyPart, null);
    }

    private void browse(String uri) {
        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (Exception ex) {
            AppLogger.logSevere(ControllerHelp.class, ex);
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
            AppLogger.logSevere(ControllerHelp.class, ex);
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
            File jarPath =
                new File(Main.class.getProtectionDomain().getCodeSource()
                    .getLocation().toURI());

            logJar(jarPath);

            if (jarPath.exists() && (jarPath.getParentFile() != null)) {
                File   dir = jarPath.getParentFile();
                String pathPrefix = dir.getAbsolutePath() + File.separator
                                    + "Manual";

                // Trying to get Locale specific manual
                manualPath = pathPrefix + "_"
                             + Locale.getDefault().getLanguage() + ".pdf";

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
            AppLogger.logSevere(AppInfo.class, ex);
        }

        MessageDisplayer.error(null, "ControllerHelp.Error.NoPdfFile",
                               manualPath);

        return null;
    }

    private static void logJar(File jarPath) {
        logJarFile(jarPath);
        logJarDir(jarPath.getParentFile());
        logIfNotExists(jarPath);
        logIfNotExists(jarPath.getParentFile());
    }

    private static void logJarDir(File jarPath) {
        AppLogger.logFinest(ControllerHelp.class,
                            "ControllerHelp.ManualPath.ParentDir",
                            jarPath.getParentFile());
    }

    private static void logJarFile(File jarPath) {
        AppLogger.logFinest(ControllerHelp.class,
                            "ControllerHelp.ManualPath.JarPath", jarPath);
    }

    private static void logIfNotExists(File file) {
        if (file == null) {
            return;
        }

        if (!file.exists()) {
            AppLogger.logFinest(ControllerHelp.class,
                                "ControllerHelp.Info.FileNotExists", file);
        }
    }
}
