package org.jphototagger.plugin.htmlreports;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.plugin.fileprocessor.FileProcessedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingFinishedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingStartedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.PreferencesDirectoryProvider;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.domain.metadata.MetaDataValueProviderSupport;
import org.jphototagger.domain.metadata.file.FilesFilenameMetaDataValue;
import org.jphototagger.domain.metadata.thumbnails.ThumbnailsThumbnailMetaDataValue;
import org.jphototagger.image.util.ImageUtil;
import org.jphototagger.lib.api.AppIconProvider;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.help.HelpContentProvider;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.IoUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.HtmlUtil;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.lib.util.SystemProperties;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileProcessorPlugin.class, position = 100000)
public final class HtmlReports implements FileProcessorPlugin, HelpContentProvider {

    private static final String THUMBNAILS_DIR_NAME = "thumbnails";
    private static final String CSS_FILE_NAME = "report.css";
    private static final String CSS_TEMPLATE_RESOURCE = "/org/jphototagger/plugin/htmlreports/templates/report.css";
    private static final String HTML_FILE_NAME = "report.html";
    private static final String HTML_TEMPLATE_RESOURCE = "/org/jphototagger/plugin/htmlreports/templates/DefaultReport.html";
    private static final String HTML_TEMPLATE_REPLACE_PATTERN_TITLE = "<!--TITLE-->";
    private static final String HTML_TEMPLATE_REPLACE_PATTERN_TABLE = "<!--TABLE-->";
    private static final String LINE_SEPARATOR = SystemProperties.getLineSeparator();
    private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private String filenameSuggestion = HTML_FILE_NAME;
    private MetaDataValueProviderSupport metaDataValueProviderSupport;
    private File reportDirectory;
    private File thumbnailsDirectory;
    private File htmlFile;
    private String title;

    @Override
    public void processFiles(Collection<? extends File> files) {
        boolean success = false;
        try {
            EventBus.publish(new FileProcessingStartedEvent(this));
            createMetaDataValueProviderSupport();
            ensureDirectoriesExisting();
            ensureFilesExisting();
            showSettingsDialog();
            setTitle();
            setHtmlFile();
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
        String template = IoUtil.getTextResource(HTML_TEMPLATE_RESOURCE);
        template = template.replace(HTML_TEMPLATE_REPLACE_PATTERN_TITLE, title);
        StringBuilder table = new StringBuilder(createTableHeading());
        for (File file : files) {
            table.append(LINE_SEPARATOR);
            table.append(createTableRow(file));
            EventBus.publish(new FileProcessedEvent(this, file, false));
        }
        template = template.replace(HTML_TEMPLATE_REPLACE_PATTERN_TABLE, table.toString());
        FileUtil.writeStringAsFile(template, "UTF-8", htmlFile);
    }

    private String createTableHeading() {
        StringBuilder sb = new StringBuilder("<tr>");
        for (MetaDataValue value : DefaultMetaDataValues.INSTANCE.getValues()) {
            sb.append("<th ").append(createHtmlClassAttribute(value)).append(">");
            sb.append(HtmlUtil.escapeHTML(value.getDescription()));
            sb.append("</th>");
        }
        sb.append("</tr>");
        return sb.toString();
    }

    private String createTableRow(File file) {
        StringBuilder sb = new StringBuilder("<tr>");
        for (MetaDataValue value : DefaultMetaDataValues.INSTANCE.getValues()) {
            sb.append("<td ").append(createHtmlClassAttribute(value)).append(">");
            sb.append(createCellValue(value, file));
            sb.append("</td>");
        }
        sb.append("</tr>");
        return sb.toString();
    }

    private String createCellValue(MetaDataValue value, File file) {
        Collection<MetaDataValueData> valueDataCollection = metaDataValueProviderSupport.lookupMetaDataForFile(file, value);
        StringBuilder sb = new StringBuilder();
        for (MetaDataValueData valueData : valueDataCollection) {
            String formattedData = isThumbnail(valueData)
                    ? formatThumbnail(file, valueData.getData())
                    : isFilename(valueData)
                    ? formatFilename(file, valueData.getData())
                    : HtmlUtil.escapeHTML(format(valueData));
            sb.append(formattedData);
        }
        return sb.toString();
    }

    private boolean isFilename(MetaDataValueData valueData) {
        MetaDataValue metaDataValue = valueData.getMetaDataValue();
        return metaDataValue == FilesFilenameMetaDataValue.INSTANCE;
    }

    private String formatFilename(File file, Object valueData) {
        if (!(valueData instanceof String)) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder("<a href=\"").append(file.toURI().toURL()).append("\">");
            sb.append((String) valueData);
            sb.append("</a>");
            return sb.toString();
        } catch (Throwable t) {
            Logger.getLogger(HtmlReports.class.getName()).log(Level.SEVERE, "Error creating thumbnail file for " + file, t);
            return "";
        }
    }

    private boolean isThumbnail(MetaDataValueData valueData) {
        MetaDataValue metaDataValue = valueData.getMetaDataValue();
        return metaDataValue == ThumbnailsThumbnailMetaDataValue.INSTANCE;
    }

    private String formatThumbnail(File file, Object valueData) {
        if (!(valueData instanceof Image)) {
            return "";
        }
        try {
            String thumbnailFilenameBase = FileUtil.getMd5FilenameOfAbsolutePath(file) + ".jpg";
            createThumbnail(thumbnailFilenameBase, (Image) valueData);
            StringBuilder sb = new StringBuilder("<a href=\"").append(file.toURI().toURL()).append("\">");
            sb.append("<img src=\"" + THUMBNAILS_DIR_NAME + "/").append(thumbnailFilenameBase).append("\"/>");
            sb.append("</a>");
            return sb.toString();
        } catch (Throwable t) {
            Logger.getLogger(HtmlReports.class.getName()).log(Level.SEVERE, "Error creating thumbnail file for " + file, t);
            return "";
        }
    }

    private void createThumbnail(String thumbnailFilenameBase, Image image) throws IOException {
        File tnFile = new File(thumbnailsDirectory.getAbsolutePath() + File.separator + thumbnailFilenameBase);
        ImageUtil.writeJpegImage(image, tnFile);
    }

    private String format(MetaDataValueData valueData) {
        Object data = valueData.getData();
        ValueType valueType = valueData.getMetaDataValue().getValueType();
        if (data instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) data;
            StringBuilder sb = new StringBuilder();
            boolean isFirstElement = true;
            for (Object element : collection) {
                sb.append(isFirstElement ? "" : ", ");
                sb.append(format(valueType, element));
                isFirstElement = false;
            }
            return sb.toString();
        }
        return format(valueType, data);
    }

    private String format(MetaDataValue.ValueType valueType, Object value) {
        if (value == null) {
            return "";
        }
        switch (valueType) {
            case STRING:
                return (String) value;
            case DATE:
                return dateFormat.format((Date) value);
            case SMALLINT: // falls through to numberFormat
            case BIGINT: // falls through to numberFormat
            case INTEGER: // falls through to numberFormat
            case REAL:
                return numberFormat.format(value);
            default:
                return "No formatting rule for " + valueType + " (" + value + ")";
        }
    }

    private String createHtmlClassAttribute(MetaDataValue metaDataValue) {
        return "class=\"" + metaDataValue.getValueName() + "\"";
    }

    private void showSettingsDialog() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean isShow = prefs.getBoolean(HtmlReportsPreferencesKeys.KEY_SHOW_SETTINGS_BEFORE_CREATING);
        if (isShow) {
            HtmlReportsSettingsDialog settingsDialog = new HtmlReportsSettingsDialog();
            settingsDialog.setVisible(true);
        }
    }

    private void setTitle() {
        String message = Bundle.getString(HtmlReports.class, "HtmlReports.GetTitle.Message");
        String input = Bundle.getString(HtmlReports.class, "HtmlReports.GetTitle.Input");
        title = MessageDisplayer.input(message, input);
        title = HtmlUtil.escapeHTML(StringUtil.hasContent(title) ? title : "");
    }

    private void setHtmlFile() {
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
        htmlFile = new File(reportDirectory.getAbsolutePath() + File.separator + filename);
        htmlFile = FileUtil.getNotExistingFile(htmlFile);
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

    private void ensureDirectoriesExisting() throws IOException {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String dirPath = prefs.getString(HtmlReportsPreferencesKeys.KEY_DIRECTORY);
        if (!StringUtil.hasContent(dirPath)) {
            dirPath = getDefaultReportsDirectoryName();
            FileUtil.ensureDirectoryExists(new File(dirPath));
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
        reportDirectory = dir;
        thumbnailsDirectory = new File(dirPath + File.separator + THUMBNAILS_DIR_NAME);
        if (thumbnailsDirectory.isDirectory()) {
            return;
        }
        if (!thumbnailsDirectory.mkdir()) {
            throw new IllegalStateException("Error while creating thumbnails directory'" + dir + "'for HTML Reports!");
        }
    }

    static String getDefaultReportsDirectoryName() {
        PreferencesDirectoryProvider provider = Lookup.getDefault().lookup(PreferencesDirectoryProvider.class);
        String parentDirname = provider.getPluginPreferencesDirectory().getAbsolutePath();
        return parentDirname + File.separator + "Html-Reports";
    }

    private void ensureFilesExisting() throws IOException {
        ensureCssFileExists();
    }

    private void ensureCssFileExists() throws IOException {
        File cssFile = new File(reportDirectory.getAbsolutePath() + File.separator + CSS_FILE_NAME);
        if (!cssFile.exists()) {
            String css = IoUtil.getTextResource(CSS_TEMPLATE_RESOURCE);
            FileUtil.writeStringAsFile(css, cssFile);
        }
        if (!cssFile.isFile()) {
            throw new IllegalStateException("CSS file '" + cssFile + "' for HTML reports is not a file!");
        }
    }

    private void openReport() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean isOpen = prefs.getBoolean(HtmlReportsPreferencesKeys.KEY_OPEN_REPORT_AFTER_CREATING);
        if (isOpen) {
            DesktopUtil.browse(htmlFile.toURI().toString(), "HtmlReports.BrowseReport");
        }
    }

    private void createMetaDataValueProviderSupport() {
        if (metaDataValueProviderSupport == null) {
            metaDataValueProviderSupport = new MetaDataValueProviderSupport();
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
        return Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_file.png");
    }

    @Override
    public Icon getLargeIcon() {
        return Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_file.png");
    }

    @Override
    public Component getSettingsComponent() {
        return new HtmlReportsSettingsPanel();
    }

    @Override
    public String getHelpContentUrl() {
        return "/org/jphototagger/plugin/htmlreports/help/contents.xml";
    }

    @Override
    public int getPosition() {
        return 300;
    }
}
