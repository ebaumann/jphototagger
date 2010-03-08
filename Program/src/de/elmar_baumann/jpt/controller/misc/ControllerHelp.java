/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.app.AppInfo;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLoggingSystem;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.Main;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.dialog.HelpBrowser;
import de.elmar_baumann.lib.event.HelpBrowserEvent;
import de.elmar_baumann.lib.event.listener.HelpBrowserListener;

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
 * @author  Elmar Baumann
 * @version 2008-09-12
 */
public final class ControllerHelp
        implements ActionListener, HelpBrowserListener {
    private static final String HELP_CONTENTS_URL =
        JptBundle.INSTANCE.getString("Help.Url.Contents");
    private static final String KEY_CURRENT_URL =
        ControllerHelp.class.getName() + ".CurrentURL";
    private static final String TO_ADDRESS_BUGS     = "support@jphototagger.org";
    private static final String TO_ADDRESS_FEATURES = "support@jphototagger.org";
    private static final String URI_USER_FORUM      =
        JptBundle.INSTANCE.getString("ControllerHelp.URI.UserForum");
    private static final String URI_WEBSITE =
        JptBundle.INSTANCE.getString("ControllerHelp.URI.Website");
    private static final String URI_CHANGELOG =
        JptBundle.INSTANCE.getString("ControllerHelp.URI.Changelog");
    private static final String SUBJECT_FEATURES =
        JptBundle.INSTANCE.getString("ControllerSendMail.Subject.Features");
    private static final String SUBJECT_BUGS =
        JptBundle.INSTANCE.getString("ControllerSendMail.Subject.Bugs");
    private final AppFrame    appFrame   = GUI.INSTANCE.getAppFrame();
    private final HelpBrowser help       = HelpBrowser.INSTANCE;
    private String            currentUrl =
        UserSettings.INSTANCE.getSettings().getString(KEY_CURRENT_URL);
    private final JMenuItem menuItemAcceleratorKeys =
        appFrame.getMenuItemAcceleratorKeys();
    private final JMenuItem menuItemHelp              =
        appFrame.getMenuItemHelp();
    private final JMenuItem menuItemOpenPdfUserManual =
        appFrame.getMenuItemOpenPdfUserManual();
    private final JMenuItem menuItemBrowseWebsite =
        appFrame.getMenuItemBrowseWebsite();
    private final JMenuItem menuItemBrowseUserForum =
        appFrame.getMenuItemBrowseUserForum();
    private final JMenuItem menuItemBrowseChangelog =
        appFrame.getMenuItemBrowseChangelog();
    private final JMenuItem menuItemSendBugMail =
        appFrame.getMenuItemSendBugMail();
    private final JMenuItem menuItemSendFeatureMail =
        appFrame.getMenuItemSendFeatureMail();

    public ControllerHelp() {
        listen();
    }

    private void listen() {
        help.addHelpBrowserListener(this);
        menuItemOpenPdfUserManual.addActionListener(this);
        menuItemAcceleratorKeys.addActionListener(this);
        menuItemBrowseUserForum.addActionListener(this);
        menuItemBrowseWebsite.addActionListener(this);
        menuItemBrowseChangelog.addActionListener(this);
        menuItemSendBugMail.addActionListener(this);
        menuItemSendFeatureMail.addActionListener(this);
    }

    @Override
    public void actionPerformed(HelpBrowserEvent action) {
        if (action.getType().equals(HelpBrowserEvent.Type.URL_CHANGED)) {
            setCurrentUrl(action);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(menuItemHelp)) {
            showHelp();
        } else if (source == menuItemAcceleratorKeys) {
            showAcceleratorKeyHelp();
        } else if (source == menuItemOpenPdfUserManual) {
            openPdfUserManual();
        } else if (source == menuItemBrowseUserForum) {
            browse(URI_USER_FORUM);
        } else if (source == menuItemBrowseWebsite) {
            browse(URI_WEBSITE);
        } else if (source == menuItemBrowseChangelog) {
            browse(URI_CHANGELOG);
        } else if (source == menuItemSendBugMail) {
            sendBugMail();
        } else if (source == menuItemSendFeatureMail) {
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
        if ((help.getContentsUrl() == null)
                ||!help.getContentsUrl().equals(HELP_CONTENTS_URL)) {
            help.setContentsUrl(HELP_CONTENTS_URL);
        }
    }

    private void showHelp() {
        initHelp();

        if (!currentUrl.isEmpty()) {
            help.setDisplayUrl(currentUrl);
        }

        ComponentUtil.show(help);
    }

    private void showAcceleratorKeyHelp() {
        initHelp();
        help.setDisplayUrl(
            JptBundle.INSTANCE.getString("Help.Url.AcceleratorKeys"));
        ComponentUtil.show(help);
    }

    private void sendBugMail() {
        sendMail(
            TO_ADDRESS_BUGS, SUBJECT_BUGS,
            JptBundle.INSTANCE.getString(
                "ControllerSendMail.Info.AttachLogfile",
                AppLoggingSystem.getCurrentLogfileName()));
    }

    private void sendFeatureMail() {
        sendMail(TO_ADDRESS_FEATURES, SUBJECT_FEATURES, null);
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
                File   dir        = jarPath.getParentFile();
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
