package org.jphototagger.plugin.htmlreports;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.bushe.swing.event.EventBus;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.plugin.fileprocessor.FileProcessedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingFinishedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingStartedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.IoUtil;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringEscapeUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileProcessorPlugin.class)
public final class HtmlReports implements FileProcessorPlugin {

    private static final String THUMBNAILS_DIR_NAME = "thumbnails";
    private static final String CSS_FILE_NAME = "reports.css";
    private static final String CSS_TEMPLATE_RESOURCE = "/org/jphototagger/plugin/htmlreports/templates/report.css";
    private static final String HTML_FILE_NAME = "report.html";
    private static final String HTML_TEMPLATE_RESOURCE = "/org/jphototagger/plugin/htmlreports/templates/DefaultReport.html";
    private static final String HTML_TEMPLATE_REPLACE_PATTERN_TITLE = "<!--TITLE-->";
    private static final String HTML_TEMPLATE_REPLACE_PATTERN_TABLE = "<!--TABLE-->";
    private String filenameSuggestion = HTML_FILE_NAME;
    private File directory;

    @Override
    public void processFiles(Collection<? extends File> files) {
        boolean success = false;
        try {
            EventBus.publish(new FileProcessingStartedEvent(this));
            ensureDirectoriesExisting();
            ensureFilesExisting();
            writeHtmlFile(files);
            openReport();
            success = true;
        } catch (Throwable t) {
            handleErrorProcessingFiles(files, t);
        } finally {
            EventBus.publish(new FileProcessingFinishedEvent(this, success));
        }
    }

    private void writeHtmlFile(Collection<? extends File> files) throws IOException {
        showSettingsDialog();
        String title = getTitle();
        File htmlFile = getHtmlFile();
        String template = IoUtil.getTextResource(HTML_TEMPLATE_RESOURCE);
        template = template.replace(HTML_TEMPLATE_REPLACE_PATTERN_TITLE, title);
        StringBuilder table = new StringBuilder(createTableHeading());
        for (File file : files) {
            table.append(createTableRow(file));
            EventBus.publish(new FileProcessedEvent(this, file, false));
        }
        template = template.replace(HTML_TEMPLATE_REPLACE_PATTERN_TABLE, table.toString());
        FileUtil.writeStringAsFile(template, htmlFile);
    }

    private void showSettingsDialog() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean isShow = prefs.getBoolean(HtmlReportsPreferencesKeys.KEY_SHOW_SETTINGS_BEFORE_CREATING);
        if (isShow) {
            HtmlReportsSettingsDialog settingsDialog = new HtmlReportsSettingsDialog();
            settingsDialog.setVisible(true);
        }
    }

    private void openReport() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean isOpen = prefs.getBoolean(HtmlReportsPreferencesKeys.KEY_OPEN_REPORT_AFTER_CREATING);
        if (isOpen) {
        }
    }

    private String createTableHeading() {
        return "";
    }

    private String createTableRow(File file) {
        return "";
    }

    private String getTitle() {
        String message = Bundle.getString(HtmlReports.class, "HtmlReports.GetTitle.Message");
        String input = Bundle.getString(HtmlReports.class, "HtmlReports.GetTitle.Input");
        String title = MessageDisplayer.input(message, input);
        return StringEscapeUtil.escapeHTML(StringUtil.hasContent(title) ? title : "");
    }

    private File getHtmlFile() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean isInput = prefs.getBoolean(HtmlReportsPreferencesKeys.KEY_INPUT_FILENAME_BEFORE_CREATING);
        String filename = HTML_FILE_NAME;
        if (isInput) {
            String message = Bundle.getString(HtmlReports.class, "HtmlReports.GetHtmlFile.Message");
            String input = MessageDisplayer.input(message, filenameSuggestion);
            if (StringUtil.hasContent(input)) {
                filename = input;
                if (!filename.toLowerCase().endsWith(".html")) {
                    filename += ".html";
                }
                filenameSuggestion = filename;
            }
        }
        if (filename.contains(File.separator)) {
            throw new IllegalStateException("File name '" + filename + "' contains path separator!");
        }
        File htmlFile = new File(directory.getAbsolutePath() + File.separator + filename);
        return FileUtil.getNotExistingFile(htmlFile);
    }

    private void handleErrorProcessingFiles(Collection<? extends File> files, Throwable t) {
        Logger.getLogger(HtmlReports.class.getName()).log(Level.SEVERE, null, t);
        MessageDisplayer.thrown(Bundle.getString(HtmlReports.class, "HtmlReports.ProcessFiles.Error"), t);
        boolean retry = MessageDisplayer.confirmYesNo(
                ComponentUtil.findFrameWithIcon(),
                Bundle.getString(HtmlReports.class, "HtmlReports.ProcessFiles.Confirm.ChangeSettingsAntRetry"));
        if (retry) {
            HtmlReportsSettingsDialog settingsDialog = new HtmlReportsSettingsDialog();
            settingsDialog.setVisible(true);
            processFiles(files); // recursive
        }
    }

    private void ensureDirectoriesExisting() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String dirPath = prefs.getString(HtmlReportsPreferencesKeys.KEY_DIRECTORY);
        if (!StringUtil.hasContent(dirPath)) {
            throw new IllegalStateException("Directory for HTML Reports is not defined!");
        }
        File dir = new File(dirPath);
        if (dir.exists() && !dir.isDirectory()) {
            throw new IllegalStateException("File '" + dir + "' is a file and not a valid dircetory for HTML Reports!");
        }
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IllegalStateException("Error while creating directory'" + dir + "'for HTML Reports!");
            }
        }
        directory = dir;
        File thumbnailsDir = new File(dirPath + File.separator + THUMBNAILS_DIR_NAME);
        if (thumbnailsDir.isDirectory()) {
            return;
        }
        if (!thumbnailsDir.mkdir()) {
            throw new IllegalStateException("Error while creating thumbnails directory'" + dir + "'for HTML Reports!");
        }
    }

    private void ensureFilesExisting() throws IOException {
        ensureCssFileExists();
    }

    private void ensureCssFileExists() throws IOException {
        File cssFile = new File(directory.getAbsolutePath() + File.separator + CSS_FILE_NAME);
        if (!cssFile.exists()) {
            String css = IoUtil.getTextResource(CSS_TEMPLATE_RESOURCE);
            FileUtil.writeStringAsFile(css, cssFile);
        }
        if (!cssFile.isFile()) {
            throw new IllegalStateException("CSS file '" + cssFile + "' for HTML reports is not a file!");
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getDescription() {
        return Bundle.getString(HtmlReports.class, "HtmlReports.Description");
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(HtmlReports.class, "HtmlReports.Name");
    }

    @Override
    public Icon getSmallIcon() {
        return IconUtil.getImageIcon(HtmlReports.class, "html.png");
    }

    @Override
    public Icon getLargeIcon() {
        return IconUtil.getImageIcon(HtmlReports.class, "html32.png");
    }

    @Override
    public Component getSettingsComponent() {
        return new HtmlReportsSettingsPanel();
    }
}
