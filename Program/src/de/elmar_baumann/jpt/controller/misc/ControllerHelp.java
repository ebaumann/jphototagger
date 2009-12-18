/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.Main;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.app.AppInfo;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.io.IoUtil;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.SettingsDialog;
import de.elmar_baumann.jpt.view.panels.SettingsMiscPanel.Tab;
import de.elmar_baumann.lib.dialog.HelpBrowser;
import de.elmar_baumann.lib.event.HelpBrowserEvent;
import de.elmar_baumann.lib.event.listener.HelpBrowserListener;
import de.elmar_baumann.lib.runtime.External;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import javax.swing.JMenuItem;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-12
 */
public final class ControllerHelp
        implements ActionListener, HelpBrowserListener {

    private static final String HELP_CONTENTS_URL =
            Bundle.getString("Help.Url.Contents");
    private final HelpBrowser help = HelpBrowser.INSTANCE;
    private static final String KEY_CURRENT_URL =
            ControllerHelp.class.getName() + ".CurrentURL"; // NOI18N
    private String currentUrl =
            UserSettings.INSTANCE.getSettings().getString(KEY_CURRENT_URL);
    private final JMenuItem menuItemAcceleratorKeys =
            GUI.INSTANCE.getAppFrame().getMenuItemAcceleratorKeys();
    private final JMenuItem menuItemHelp =
            GUI.INSTANCE.getAppFrame().getMenuItemHelp();
    private final JMenuItem menuItemOpenPdfUserManual =
            GUI.INSTANCE.getAppFrame().getMenuItemOpenPdfUserManual();

    public ControllerHelp() {
        listen();
    }

    private void listen() {
        help.addHelpBrowserListener(this);
        menuItemOpenPdfUserManual.addActionListener(this);
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
        } else if (source.equals(menuItemAcceleratorKeys)) {
            showAcceleratorKeyHelp();
        } else if (source.equals(menuItemOpenPdfUserManual)) {
            openPdfUserManual();
        }
    }

    private void setCurrentUrl(HelpBrowserEvent action) {
        URL url = action.getUrl();
        if (!url.getProtocol().startsWith("http")) { // NOI18N
            currentUrl = HelpBrowser.getLastPathComponent(url);
            UserSettings.INSTANCE.getSettings().setString(
                    currentUrl, KEY_CURRENT_URL);
            UserSettings.INSTANCE.writeToFile();
        }
    }

    private void initHelp() {
        help.setIconImages(AppLookAndFeel.getAppIcons());
        if (help.getContentsUrl() == null ||
                !help.getContentsUrl().equals(HELP_CONTENTS_URL)) {
            help.setContentsUrl(HELP_CONTENTS_URL);
        }
    }

    private void showHelp() {
        initHelp();
        if (!currentUrl.isEmpty()) {
            help.setDisplayUrl(currentUrl);
        }
        helpToFront();
    }

    private void showAcceleratorKeyHelp() {
        initHelp();
        help.setDisplayUrl(Bundle.getString("Help.Url.AcceleratorKeys")); // NOI18N
        helpToFront();
    }

    public void helpToFront() {
        if (help.isVisible()) {
            help.toFront();
        } else {
            help.setVisible(true);
        }
    }

    private void openPdfUserManual() {
        if (!checkPdfViewer()) return;
        File manual = getPdfUserManualPath();
        if (manual == null) return;
        External.execute(logAndGetPdfManualOpenCommand(manual));
    }

    private String logAndGetPdfManualOpenCommand(File manual) {
        String command = IoUtil.quoteForCommandLine(
                UserSettings.INSTANCE.getPdfViewer(), manual);
        AppLog.logInfo(getClass(), "ControllerHelp.Info.PdfOpenCommand", command); // NOI18N
        return command;
    }

    private boolean checkPdfViewer() {
        File viewer = new File(UserSettings.INSTANCE.getPdfViewer());
        if (!viewer.exists()) {
            if (MessageDisplayer.confirm(null,
                    "ControllerHelp.Error.NoPdfViewer", // NOI18N
                    MessageDisplayer.CancelButton.HIDE).equals(
                    MessageDisplayer.ConfirmAction.YES)) {
                SettingsDialog.INSTANCE.selectTab(Tab.EXTERNAL_APPLICATIONS);
                if (SettingsDialog.INSTANCE.isVisible()) {
                    SettingsDialog.INSTANCE.toFront();
                } else {
                    SettingsDialog.INSTANCE.setVisible(true);
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Returns the PDF user manual file.
     *
     * @return file or null if the file does not exist
     */
    private static File getPdfUserManualPath() {
        String manualPath = ""; // NOI18N
        try {
            File jarPath = new File(Main.class.getProtectionDomain().
                    getCodeSource().getLocation().getPath());
            logJar(jarPath);
            if (jarPath.exists() && jarPath.getParentFile() != null) {
                File dir = jarPath.getParentFile();
                String pathPrefix =
                        dir.getAbsolutePath() + File.separator + "Manual"; // NOI18N
                // Trying to get Locale specific manual
                manualPath = pathPrefix + "_" + // NOI18N
                        Locale.getDefault().getLanguage() + ".pdf"; // NOI18N
                File fileLocaleSensitive = new File(manualPath);
                logIfNotExists(fileLocaleSensitive);
                if (fileLocaleSensitive.exists()) return fileLocaleSensitive;
                // Trying to get default language manual
                manualPath = pathPrefix + "_de.pdf"; // NOI18N
                File fileDefault = new File(manualPath);
                logIfNotExists(fileDefault);
                if (fileDefault.exists()) return fileDefault;
            }
        } catch (Exception ex) {
            AppLog.logSevere(AppInfo.class, ex);
        }
        MessageDisplayer.error(
                null, "ControllerHelp.Error.NoPdfFile", manualPath); // NOI18N
        return null;
    }

    private static void logJar(File jarPath) {
        logJarFile(jarPath);
        logJarDir(jarPath.getParentFile());
        logIfNotExists(jarPath);
        logIfNotExists(jarPath.getParentFile());
    }

    private static void logJarDir(File jarPath) {
        AppLog.logFinest(ControllerHelp.class,
                "ControllerHelp.ManualPath.ParentDir", jarPath.getParentFile()); // NOI18N
    }

    private static void logJarFile(File jarPath) {
        AppLog.logFinest(ControllerHelp.class,
                "ControllerHelp.ManualPath.JarPath", jarPath); // NOI18N
    }

    private static void logIfNotExists(File file) {
        if (file == null) return;
        if (!file.exists()) {
            AppLog.logFinest(ControllerHelp.class,
                    "ControllerHelp.Info.FileNotExists", file); // NOI18N
        }
    }
}
