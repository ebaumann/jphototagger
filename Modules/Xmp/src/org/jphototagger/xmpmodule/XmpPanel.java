package org.jphototagger.xmpmodule;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.properties.XMPPropertyInfo;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.branding.TableLookAndFeel;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.TableTextFilter;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.TableUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.lib.util.Translation;
import org.jphototagger.resources.UiFactory;
import org.jphototagger.xmp.EmbeddedXmpCache;
import org.jphototagger.xmp.XmpMetadata;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class XmpPanel extends PanelExt {

    private static final long serialVersionUID = 1L;
    private final XmpTableModel modelXmpCameraRawSettings = new XmpTableModel();
    private final XmpTableModel modelXmpDc = new XmpTableModel();
    private final XmpTableModel modelXmpExif = new XmpTableModel();
    private final XmpTableModel modelXmpIptc = new XmpTableModel();
    private final XmpTableModel modelXmpLightroom = new XmpTableModel();
    private final XmpTableModel modelXmpPhotoshop = new XmpTableModel();
    private final XmpTableModel modelXmpTiff = new XmpTableModel();
    private final XmpTableModel modelXmpXap = new XmpTableModel();
    private final XmpTableCellRenderer xmpTableCellRenderer = new XmpTableCellRenderer();
    private final Map<JTable, XmpTableModel> modelOfTable = new HashMap<>();
    private final Map<XmpTableModel, String[]> namespacesOfXmpTableModel = new HashMap<>();
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);

    public XmpPanel() {
        initComponents();
        postInitComponents();
    }

    private void initTablelsModelsMap() {
        modelOfTable.put(tableXmpCameraRawSettings, modelXmpCameraRawSettings);
        modelOfTable.put(tableXmpDc, modelXmpDc);
        modelOfTable.put(tableXmpExif, modelXmpExif);
        modelOfTable.put(tableXmpIptc, modelXmpIptc);
        modelOfTable.put(tableXmpLightroom, modelXmpLightroom);
        modelOfTable.put(tableXmpPhotoshop, modelXmpPhotoshop);
        modelOfTable.put(tableXmpTiff, modelXmpTiff);
        modelOfTable.put(tableXmpXap, modelXmpXap);
    }

    private void initNamespacesOfXmpTableModelMap() {
        namespacesOfXmpTableModel.put(modelXmpDc, new String[]{XMPConst.NS_DC, XMPConst.NS_DC_DEPRECATED});
        namespacesOfXmpTableModel.put(modelXmpExif, new String[]{XMPConst.NS_EXIF, XMPConst.NS_EXIF_AUX});
        namespacesOfXmpTableModel.put(modelXmpIptc, new String[]{XMPConst.NS_IPTCCORE});
        namespacesOfXmpTableModel.put(modelXmpLightroom, new String[]{"http://ns.adobe.com/lightroom/1.0/"});
        namespacesOfXmpTableModel.put(modelXmpPhotoshop, new String[]{XMPConst.NS_PHOTOSHOP});
        namespacesOfXmpTableModel.put(modelXmpTiff, new String[]{XMPConst.NS_TIFF});
        namespacesOfXmpTableModel.put(modelXmpCameraRawSettings, new String[]{XMPConst.NS_CAMERARAW, "http://ns.adobe.com/camera-raw-saved-settings/1.0/"});
        namespacesOfXmpTableModel.put(modelXmpXap, new String[]{XMPConst.NS_XMP, XMPConst.NS_XMP_RIGHTS});
    }

    private void postInitComponents() {
        initTablelsModelsMap();
        initNamespacesOfXmpTableModelMap();
        setModels();
        setRenderer();
        setTableTextFilters();
        setTableComparators();
        checkBoxScanForEmbeddedXmp.setSelected(isScanForEmbeddedXmp());
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent evt) {
        if (DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP.equals(evt.getKey())) {
            checkBoxScanForEmbeddedXmp.setSelected((boolean) evt.getNewValue());
        }
    }

    private void persistScanForEmbeddedXmp() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean scanForEmbeddedXmpSelected = checkBoxScanForEmbeddedXmp.isSelected();
        prefs.setBoolean(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP, scanForEmbeddedXmpSelected);
    }

    private void setModels() {
        for (JTable table : modelOfTable.keySet()) {
            table.setModel(modelOfTable.get(table));
        }
    }

    private void setRenderer() {
        for (JTable table : modelOfTable.keySet()) {
            table.setDefaultRenderer(Object.class, xmpTableCellRenderer);
        }
    }

    private void setTableTextFilters() {
        setTableTextFilter(textFieldTableXmpCameraRawSettingsFilter, tableXmpCameraRawSettings);
        setTableTextFilter(textFieldTableXmpDcFilter, tableXmpDc);
        setTableTextFilter(textFieldTableXmpExifFilter, tableXmpExif);
        setTableTextFilter(textFieldTableXmpIptcFilter, tableXmpIptc);
        setTableTextFilter(textFieldTableXmpLightroomFilter, tableXmpLightroom);
        setTableTextFilter(textFieldTableXmpPhotoshopFilter, tableXmpPhotoshop);
        setTableTextFilter(textFieldTableXmpTiffFilter, tableXmpTiff);
        setTableTextFilter(textFieldTableXmpXapFilter, tableXmpXap);
    }

    private void setTableTextFilter(JTextComponent filterTextComponent, JTable xmpTable) {
        TableRowSorter<?> rowSorter = (TableRowSorter<?>) xmpTable.getRowSorter();
        TableStringConverter stringConverter = XmpTableCellRenderer.createTableStringConverter();
        Document document = filterTextComponent.getDocument();
        TableTextFilter tableTextFilter = new TableTextFilter(xmpTable, stringConverter);

        rowSorter.setStringConverter(stringConverter);
        document.addDocumentListener(tableTextFilter);
    }

    private void setTableComparators() {
        for (JTable table : modelOfTable.keySet()) {
            setTableComparator(table);
        }
    }

    private void setTableComparator(JTable xmpTable) {
        TableRowSorter<?> rowSorter = (TableRowSorter<?>) xmpTable.getRowSorter();
        Comparator<?> column0Comparator = XmpTableCellRenderer.createColumn0Comparator();
        Comparator<?> column1Comparator = XmpTableCellRenderer.createColumn1Comparator();

        rowSorter.setComparator(0, column0Comparator);
        rowSorter.setComparator(1, column1Comparator);
    }

    void removeAllRows() {
        for (XmpTableModel xmpTableModel : modelOfTable.values()) {
            xmpTableModel.removeAllRows();
        }
    }

    private void resizeTables() {
        for (JTable table : modelOfTable.keySet()) {
            TableUtil.resizeColumnWidthsToFit(table);
            ComponentUtil.forceRepaint(table);
        }
    }

    void setFile(File file) {
        setXmpModels(file);
        resizeTables();
    }

    private void setXmpModels(File file) {
        List<XMPPropertyInfo> allInfos = null;
        File sidecarFile = xmpSidecarFileResolver.getXmpSidecarFileOrNullIfNotExists(file);

        try {
            allInfos = (sidecarFile != null)
                    ? XmpMetadata.getPropertyInfosOfSidecarFile(sidecarFile)
                    : isScanForEmbeddedXmp()
                    ? EmbeddedXmpCache.INSTANCE.getXmpPropertyInfos(file)
                    : null;
        } catch (Throwable throwable) {
            Logger.getLogger(XmpPanel.class.getName()).log(Level.SEVERE, null, throwable);
        }

        if (allInfos != null) {
            for (XmpTableModel xmpTableModel : modelOfTable.values()) {
                setPropertyInfosToXmpTableModel(file, xmpTableModel, allInfos, namespacesOfXmpTableModel.get(xmpTableModel));
            }
        }
    }

    private boolean isScanForEmbeddedXmp() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                : false;
    }

    private void setPropertyInfosToXmpTableModel(File imageFile, XmpTableModel model, List<XMPPropertyInfo> allInfos, String[] namespaces) {
        List<XMPPropertyInfo> infos = new ArrayList<>();
        for (String namespace : namespaces) {
            infos.addAll(XmpMetadata.filterPropertyInfosOfNamespace(allInfos, namespace));
        }

        model.setPropertyInfosOfFile(imageFile, infos);
    }

    public static class XmpTableCellRenderer implements TableCellRenderer {

        private static final String DELIMITER_PATH = "/";
        private static final String DELIMITER_NAMESPACE = ":";
        private static final Translation TRANSLATION_XMP = new Translation(XmpTableCellRenderer.class, "XmpPropertyTranslations");
        private final TableLookAndFeel lookAndFeel = Lookup.getDefault().lookup(TableLookAndFeel.class);
        private final JLabel cellLabel = UiFactory.label();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (lookAndFeel == null || !(value instanceof XMPPropertyInfo)) {
                return UiFactory.label(StringUtil.toStringNullToEmptyString(value));
            }
            XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) value;
            lookAndFeel.setTableCellColor(cellLabel, isSelected);
            boolean isRowHeader = column == 0;
            int maxChars = isRowHeader ? lookAndFeel.getRowHeaderMaxChars() : lookAndFeel.getCellMaxChars();
            String css = isRowHeader ? lookAndFeel.getRowHeaderCss() : lookAndFeel.getCellCss();
            if (column == 0) {
                lookAndFeel.setTableRowHeaderFont(cellLabel);
                String xmpPath = xmpPropertyInfo.getPath();
                String text = translate(xmpPath, xmpPath);
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel, text, maxChars, css);
            } else {
                lookAndFeel.setTableCellFont(cellLabel);
                String text = xmpPropertyInfo.getValue().toString();
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel, text, maxChars, css);
            }

            return cellLabel;
        }

        private static String translate(String path, String alternate) {
            StringBuilder newPath = new StringBuilder();
            List<String> pathComponents = getPathComponents(path);
            int pathComponentsCount = pathComponents.size();

            for (int i = 0; i < pathComponentsCount; i++) {
                String pathComponent = pathComponents.get(i);
                String withoutIndex = getWithoutIndex(pathComponent);
                String translated = TRANSLATION_XMP.translate(withoutIndex, alternate);

                newPath.append(getWithoutNamespace(translated)).append(getIndexString(pathComponent)).append(
                        ((pathComponentsCount > 1) && (i < pathComponentsCount - 1))
                        ? DELIMITER_PATH
                        : "");
            }

            return newPath.toString();
        }

        private static List<String> getPathComponents(String path) {
            List<String> components = new ArrayList<>();
            StringTokenizer tokenizer = new StringTokenizer(path, DELIMITER_PATH);

            while (tokenizer.hasMoreTokens()) {
                components.add(tokenizer.nextToken());
            }

            return components;
        }

        private static String getWithoutIndex(String string) {
            if (hasIndex(string)) {
                int startIndex = string.lastIndexOf('[');

                return string.substring(0, startIndex);
            }

            return string;
        }

        private static String getIndexString(String string) {
            if (hasIndex(string)) {
                int startIndex = string.lastIndexOf('[');

                return string.substring(startIndex);
            }

            return "";
        }

        private static boolean hasIndex(String string) {
            return string.matches("..*\\[[0-9]+\\]$");
        }

        private static String getWithoutNamespace(String string) {
            if (hasNamespace(string)) {
                int indexDelim = string.indexOf(DELIMITER_NAMESPACE);

                return string.substring(indexDelim + 1);
            }

            return string;
        }

        private static boolean hasNamespace(String string) {
            int indexDelim = string.indexOf(DELIMITER_NAMESPACE);

            if (indexDelim > 0) {
                return XmpMetadata.isKnownNamespace(string.substring(0, indexDelim));
            }

            return false;
        }

        private static boolean isExifNamespace(String string) {
            return string.startsWith("exif:");
        }

        public static Comparator<?> createColumn0Comparator() {
            return new Column0Comparator();
        }

        public static Comparator<?> createColumn1Comparator() {
            return new Column1Comparator();
        }

        private static class Column0Comparator implements Comparator<Object> {

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof XMPPropertyInfo && o2 instanceof XMPPropertyInfo) {
                    XMPPropertyInfo xmpPropertyInfo1 = (XMPPropertyInfo) o1;
                    XMPPropertyInfo xmpPropertyInfo2 = (XMPPropertyInfo) o2;
                    String xmpPath1 = xmpPropertyInfo1.getPath();
                    String xmpPath2 = xmpPropertyInfo2.getPath();
                    String o1String = translate(xmpPath1, xmpPath1);
                    String o2String = translate(xmpPath2, xmpPath2);

                    return o1String.compareToIgnoreCase(o2String);
                } else {
                    return 0;
                }
            }
        }

        private static class Column1Comparator implements Comparator<Object> {

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof XMPPropertyInfo && o2 instanceof XMPPropertyInfo) {
                    XMPPropertyInfo xmpPropertyInfo1 = (XMPPropertyInfo) o1;
                    XMPPropertyInfo xmpPropertyInfo2 = (XMPPropertyInfo) o2;
                    String o1String = xmpPropertyInfo1.getValue().toString();
                    String o2String = xmpPropertyInfo2.getValue().toString();

                    return o1String.compareToIgnoreCase(o2String);
                } else {
                    return 0;
                }
            }
        }

        public static TableStringConverter createTableStringConverter() {
            return new XmpTableStringConverter();
        }

        private static class XmpTableStringConverter extends TableStringConverter {

            @Override
            public String toString(TableModel model, int row, int column) {
                Object value = model.getValueAt(row, column);

                if (value instanceof XMPPropertyInfo) {
                    XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) value;
                    String xmpPath = xmpPropertyInfo.getPath();

                    return column == 0
                            ? translate(xmpPath, xmpPath)
                            : xmpPropertyInfo.getValue().toString();
                } else {
                    return StringUtil.toStringNullToEmptyString(value);
                }
            }
        }
    }

    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        tabbedPaneXmp = UiFactory.tabbedPane();
        panelTableXmpTiff = UiFactory.panel();
        panelTableXmpTiffFilter = UiFactory.panel();
        labelTableXmpTiffFilter = UiFactory.label();
        textFieldTableXmpTiffFilter = UiFactory.textField();
        scrollPaneXmpTiff = UiFactory.scrollPane();
        tableXmpTiff = UiFactory.table();
        panelTableXmpExif = UiFactory.panel();
        panelTableXmpExifFilter = UiFactory.panel();
        labelTableXmpExifFilter = UiFactory.label();
        textFieldTableXmpExifFilter = UiFactory.textField();
        scrollPaneXmpExif = UiFactory.scrollPane();
        tableXmpExif = UiFactory.table();
        panelTableXmpDc = UiFactory.panel();
        panelTableXmpDcFilter = UiFactory.panel();
        labelTableXmpDcFilter = UiFactory.label();
        textFieldTableXmpDcFilter = UiFactory.textField();
        scrollPaneXmpDc = UiFactory.scrollPane();
        tableXmpDc = UiFactory.table();
        panelTableXmpIptc = UiFactory.panel();
        panelTableXmpIptcFilter = UiFactory.panel();
        labelTableXmpIptcFilter = UiFactory.label();
        textFieldTableXmpIptcFilter = UiFactory.textField();
        scrollPaneXmpIptc = UiFactory.scrollPane();
        tableXmpIptc = UiFactory.table();
        panelTableXmpPhotoshop = UiFactory.panel();
        panelTableXmpPhotoshopFilter = UiFactory.panel();
        labelTableXmpPhotoshopFilter = UiFactory.label();
        textFieldTableXmpPhotoshopFilter = UiFactory.textField();
        scrollPaneXmpPhotoshop = UiFactory.scrollPane();
        tableXmpPhotoshop = UiFactory.table();
        panelTableXmpXap = UiFactory.panel();
        panelTableXmpXapFilter = UiFactory.panel();
        labelTableXmpXapFilter = UiFactory.label();
        textFieldTableXmpXapFilter = UiFactory.textField();
        scrollPaneXmpXap = UiFactory.scrollPane();
        tableXmpXap = UiFactory.table();
        panelTableXmpLightroom = UiFactory.panel();
        panelTableXmpLightroomFilter = UiFactory.panel();
        labelTableXmpLightroomFilter = UiFactory.label();
        textFieldTableXmpLightroomFilter = UiFactory.textField();
        scrollPaneXmpLightroom = UiFactory.scrollPane();
        tableXmpLightroom = UiFactory.table();
        panelTableXmpCameraRawSettings = UiFactory.panel();
        panelTableXmpCameraRawSettingsFilter = UiFactory.panel();
        labelTableXmpCameraRawSettingsFilter = UiFactory.label();
        textFieldTableXmpCameraRawSettingsFilter = UiFactory.textField();
        scrollPaneXmpCameraRawSettings = UiFactory.scrollPane();
        tableXmpCameraRawSettings = UiFactory.table();
        checkBoxScanForEmbeddedXmp = UiFactory.checkBox();

        
        setLayout(new GridBagLayout());

        tabbedPaneXmp.setName("tabbedPaneXmp"); // NOI18N
        tabbedPaneXmp.setOpaque(true);

        panelTableXmpTiff.setName("panelTableXmpTiff"); // NOI18N
        panelTableXmpTiff.setLayout(new GridBagLayout());

        panelTableXmpTiffFilter.setName("panelTableXmpTiffFilter"); // NOI18N
        panelTableXmpTiffFilter.setLayout(new GridBagLayout());

        labelTableXmpTiffFilter.setText(Bundle.getString(getClass(), "XmpPanel.labelTableXmpTiffFilter.text")); // NOI18N
        labelTableXmpTiffFilter.setName("labelTableXmpTiffFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panelTableXmpTiffFilter.add(labelTableXmpTiffFilter, gridBagConstraints);

        textFieldTableXmpTiffFilter.setName("textFieldTableXmpTiffFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTableXmpTiffFilter.add(textFieldTableXmpTiffFilter, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelTableXmpTiff.add(panelTableXmpTiffFilter, gridBagConstraints);

        scrollPaneXmpTiff.setName("scrollPaneXmpTiff"); // NOI18N

        tableXmpTiff.setAutoCreateRowSorter(true);
        tableXmpTiff.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableXmpTiff.setName("tableXmpTiff"); // NOI18N
        scrollPaneXmpTiff.setViewportView(tableXmpTiff);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTableXmpTiff.add(scrollPaneXmpTiff, gridBagConstraints);

        tabbedPaneXmp.addTab(Bundle.getString(getClass(), "XmpPanel.panelTableXmpTiff.TabConstraints.tabTitle"), panelTableXmpTiff); // NOI18N

        panelTableXmpExif.setName("panelTableXmpExif"); // NOI18N
        panelTableXmpExif.setLayout(new GridBagLayout());

        panelTableXmpExifFilter.setName("panelTableXmpExifFilter"); // NOI18N
        panelTableXmpExifFilter.setLayout(new GridBagLayout());

        labelTableXmpExifFilter.setText(Bundle.getString(getClass(), "XmpPanel.labelTableXmpExifFilter.text")); // NOI18N
        labelTableXmpExifFilter.setName("labelTableXmpExifFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panelTableXmpExifFilter.add(labelTableXmpExifFilter, gridBagConstraints);

        textFieldTableXmpExifFilter.setName("textFieldTableXmpExifFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTableXmpExifFilter.add(textFieldTableXmpExifFilter, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelTableXmpExif.add(panelTableXmpExifFilter, gridBagConstraints);

        scrollPaneXmpExif.setName("scrollPaneXmpExif"); // NOI18N

        tableXmpExif.setAutoCreateRowSorter(true);
        tableXmpExif.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableXmpExif.setName("tableXmpExif"); // NOI18N
        scrollPaneXmpExif.setViewportView(tableXmpExif);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTableXmpExif.add(scrollPaneXmpExif, gridBagConstraints);

        tabbedPaneXmp.addTab(Bundle.getString(getClass(), "XmpPanel.panelTableXmpExif.TabConstraints.tabTitle"), panelTableXmpExif); // NOI18N

        panelTableXmpDc.setName("panelTableXmpDc"); // NOI18N
        panelTableXmpDc.setLayout(new GridBagLayout());

        panelTableXmpDcFilter.setName("panelTableXmpDcFilter"); // NOI18N
        panelTableXmpDcFilter.setLayout(new GridBagLayout());

        labelTableXmpDcFilter.setText(Bundle.getString(getClass(), "XmpPanel.labelTableXmpDcFilter.text")); // NOI18N
        labelTableXmpDcFilter.setName("labelTableXmpDcFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panelTableXmpDcFilter.add(labelTableXmpDcFilter, gridBagConstraints);

        textFieldTableXmpDcFilter.setName("textFieldTableXmpDcFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTableXmpDcFilter.add(textFieldTableXmpDcFilter, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelTableXmpDc.add(panelTableXmpDcFilter, gridBagConstraints);

        scrollPaneXmpDc.setName("scrollPaneXmpDc"); // NOI18N

        tableXmpDc.setAutoCreateRowSorter(true);
        tableXmpDc.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableXmpDc.setName("tableXmpDc"); // NOI18N
        scrollPaneXmpDc.setViewportView(tableXmpDc);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTableXmpDc.add(scrollPaneXmpDc, gridBagConstraints);

        tabbedPaneXmp.addTab(Bundle.getString(getClass(), "XmpPanel.panelTableXmpDc.TabConstraints.tabTitle"), panelTableXmpDc); // NOI18N

        panelTableXmpIptc.setName("panelTableXmpIptc"); // NOI18N
        panelTableXmpIptc.setLayout(new GridBagLayout());

        panelTableXmpIptcFilter.setName("panelTableXmpIptcFilter"); // NOI18N
        panelTableXmpIptcFilter.setLayout(new GridBagLayout());

        labelTableXmpIptcFilter.setText(Bundle.getString(getClass(), "XmpPanel.labelTableXmpIptcFilter.text")); // NOI18N
        labelTableXmpIptcFilter.setName("labelTableXmpIptcFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panelTableXmpIptcFilter.add(labelTableXmpIptcFilter, gridBagConstraints);

        textFieldTableXmpIptcFilter.setName("textFieldTableXmpIptcFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTableXmpIptcFilter.add(textFieldTableXmpIptcFilter, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelTableXmpIptc.add(panelTableXmpIptcFilter, gridBagConstraints);

        scrollPaneXmpIptc.setName("scrollPaneXmpIptc"); // NOI18N

        tableXmpIptc.setAutoCreateRowSorter(true);
        tableXmpIptc.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableXmpIptc.setName("tableXmpIptc"); // NOI18N
        scrollPaneXmpIptc.setViewportView(tableXmpIptc);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTableXmpIptc.add(scrollPaneXmpIptc, gridBagConstraints);

        tabbedPaneXmp.addTab(Bundle.getString(getClass(), "XmpPanel.panelTableXmpIptc.TabConstraints.tabTitle"), panelTableXmpIptc); // NOI18N

        panelTableXmpPhotoshop.setName("panelTableXmpPhotoshop"); // NOI18N
        panelTableXmpPhotoshop.setLayout(new GridBagLayout());

        panelTableXmpPhotoshopFilter.setName("panelTableXmpPhotoshopFilter"); // NOI18N
        panelTableXmpPhotoshopFilter.setLayout(new GridBagLayout());

        labelTableXmpPhotoshopFilter.setText(Bundle.getString(getClass(), "XmpPanel.labelTableXmpPhotoshopFilter.text")); // NOI18N
        labelTableXmpPhotoshopFilter.setName("labelTableXmpPhotoshopFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panelTableXmpPhotoshopFilter.add(labelTableXmpPhotoshopFilter, gridBagConstraints);

        textFieldTableXmpPhotoshopFilter.setName("textFieldTableXmpPhotoshopFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTableXmpPhotoshopFilter.add(textFieldTableXmpPhotoshopFilter, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelTableXmpPhotoshop.add(panelTableXmpPhotoshopFilter, gridBagConstraints);

        scrollPaneXmpPhotoshop.setName("scrollPaneXmpPhotoshop"); // NOI18N

        tableXmpPhotoshop.setAutoCreateRowSorter(true);
        tableXmpPhotoshop.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableXmpPhotoshop.setName("tableXmpPhotoshop"); // NOI18N
        scrollPaneXmpPhotoshop.setViewportView(tableXmpPhotoshop);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTableXmpPhotoshop.add(scrollPaneXmpPhotoshop, gridBagConstraints);

        tabbedPaneXmp.addTab(Bundle.getString(getClass(), "XmpPanel.panelTableXmpPhotoshop.TabConstraints.tabTitle"), panelTableXmpPhotoshop); // NOI18N

        panelTableXmpXap.setName("panelTableXmpXap"); // NOI18N
        panelTableXmpXap.setLayout(new GridBagLayout());

        panelTableXmpXapFilter.setName("panelTableXmpXapFilter"); // NOI18N
        panelTableXmpXapFilter.setLayout(new GridBagLayout());

        labelTableXmpXapFilter.setText(Bundle.getString(getClass(), "XmpPanel.labelTableXmpXapFilter.text")); // NOI18N
        labelTableXmpXapFilter.setName("labelTableXmpXapFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panelTableXmpXapFilter.add(labelTableXmpXapFilter, gridBagConstraints);

        textFieldTableXmpXapFilter.setName("textFieldTableXmpXapFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTableXmpXapFilter.add(textFieldTableXmpXapFilter, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelTableXmpXap.add(panelTableXmpXapFilter, gridBagConstraints);

        scrollPaneXmpXap.setName("scrollPaneXmpXap"); // NOI18N

        tableXmpXap.setAutoCreateRowSorter(true);
        tableXmpXap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableXmpXap.setName("tableXmpXap"); // NOI18N
        scrollPaneXmpXap.setViewportView(tableXmpXap);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTableXmpXap.add(scrollPaneXmpXap, gridBagConstraints);

        tabbedPaneXmp.addTab(Bundle.getString(getClass(), "XmpPanel.panelTableXmpXap.TabConstraints.tabTitle"), panelTableXmpXap); // NOI18N

        panelTableXmpLightroom.setName("panelTableXmpLightroom"); // NOI18N
        panelTableXmpLightroom.setLayout(new GridBagLayout());

        panelTableXmpLightroomFilter.setName("panelTableXmpLightroomFilter"); // NOI18N
        panelTableXmpLightroomFilter.setLayout(new GridBagLayout());

        labelTableXmpLightroomFilter.setText(Bundle.getString(getClass(), "XmpPanel.labelTableXmpLightroomFilter.text")); // NOI18N
        labelTableXmpLightroomFilter.setName("labelTableXmpLightroomFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panelTableXmpLightroomFilter.add(labelTableXmpLightroomFilter, gridBagConstraints);

        textFieldTableXmpLightroomFilter.setName("textFieldTableXmpLightroomFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTableXmpLightroomFilter.add(textFieldTableXmpLightroomFilter, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelTableXmpLightroom.add(panelTableXmpLightroomFilter, gridBagConstraints);

        scrollPaneXmpLightroom.setName("scrollPaneXmpLightroom"); // NOI18N

        tableXmpLightroom.setAutoCreateRowSorter(true);
        tableXmpLightroom.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableXmpLightroom.setName("tableXmpLightroom"); // NOI18N
        scrollPaneXmpLightroom.setViewportView(tableXmpLightroom);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTableXmpLightroom.add(scrollPaneXmpLightroom, gridBagConstraints);

        tabbedPaneXmp.addTab(Bundle.getString(getClass(), "XmpPanel.panelTableXmpLightroom.TabConstraints.tabTitle"), panelTableXmpLightroom); // NOI18N

        panelTableXmpCameraRawSettings.setName("panelTableXmpCameraRawSettings"); // NOI18N
        panelTableXmpCameraRawSettings.setLayout(new GridBagLayout());

        panelTableXmpCameraRawSettingsFilter.setName("panelTableXmpCameraRawSettingsFilter"); // NOI18N
        panelTableXmpCameraRawSettingsFilter.setLayout(new GridBagLayout());

        labelTableXmpCameraRawSettingsFilter.setText(Bundle.getString(getClass(), "XmpPanel.labelTableXmpCameraRawSettingsFilter.text")); // NOI18N
        labelTableXmpCameraRawSettingsFilter.setName("labelTableXmpCameraRawSettingsFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panelTableXmpCameraRawSettingsFilter.add(labelTableXmpCameraRawSettingsFilter, gridBagConstraints);

        textFieldTableXmpCameraRawSettingsFilter.setName("textFieldTableXmpCameraRawSettingsFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelTableXmpCameraRawSettingsFilter.add(textFieldTableXmpCameraRawSettingsFilter, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelTableXmpCameraRawSettings.add(panelTableXmpCameraRawSettingsFilter, gridBagConstraints);

        scrollPaneXmpCameraRawSettings.setName("scrollPaneXmpCameraRawSettings"); // NOI18N

        tableXmpCameraRawSettings.setAutoCreateRowSorter(true);
        tableXmpCameraRawSettings.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableXmpCameraRawSettings.setName("tableXmpCameraRawSettings"); // NOI18N
        scrollPaneXmpCameraRawSettings.setViewportView(tableXmpCameraRawSettings);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTableXmpCameraRawSettings.add(scrollPaneXmpCameraRawSettings, gridBagConstraints);

        tabbedPaneXmp.addTab(Bundle.getString(getClass(), "XmpPanel.panelTableXmpCameraRawSettings.TabConstraints.tabTitle"), panelTableXmpCameraRawSettings); // NOI18N

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabbedPaneXmp, gridBagConstraints);

        checkBoxScanForEmbeddedXmp.setText(Bundle.getString(XmpPanel.class, "XmpPanel.checkBoxScanForEmbeddedXmp.text")); // NOI18N
        checkBoxScanForEmbeddedXmp.setName("checkBoxScanForEmbeddedXmp"); // NOI18N
        checkBoxScanForEmbeddedXmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                checkBoxScanForEmbeddedXmpActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        add(checkBoxScanForEmbeddedXmp, gridBagConstraints);
    }

    private void checkBoxScanForEmbeddedXmpActionPerformed(ActionEvent evt) {
        persistScanForEmbeddedXmp();
    }

    private JCheckBox checkBoxScanForEmbeddedXmp;
    private JLabel labelTableXmpCameraRawSettingsFilter;
    private JLabel labelTableXmpDcFilter;
    private JLabel labelTableXmpExifFilter;
    private JLabel labelTableXmpIptcFilter;
    private JLabel labelTableXmpLightroomFilter;
    private JLabel labelTableXmpPhotoshopFilter;
    private JLabel labelTableXmpTiffFilter;
    private JLabel labelTableXmpXapFilter;
    private JPanel panelTableXmpCameraRawSettings;
    private JPanel panelTableXmpCameraRawSettingsFilter;
    private JPanel panelTableXmpDc;
    private JPanel panelTableXmpDcFilter;
    private JPanel panelTableXmpExif;
    private JPanel panelTableXmpExifFilter;
    private JPanel panelTableXmpIptc;
    private JPanel panelTableXmpIptcFilter;
    private JPanel panelTableXmpLightroom;
    private JPanel panelTableXmpLightroomFilter;
    private JPanel panelTableXmpPhotoshop;
    private JPanel panelTableXmpPhotoshopFilter;
    private JPanel panelTableXmpTiff;
    private JPanel panelTableXmpTiffFilter;
    private JPanel panelTableXmpXap;
    private JPanel panelTableXmpXapFilter;
    private JScrollPane scrollPaneXmpCameraRawSettings;
    private JScrollPane scrollPaneXmpDc;
    private JScrollPane scrollPaneXmpExif;
    private JScrollPane scrollPaneXmpIptc;
    private JScrollPane scrollPaneXmpLightroom;
    private JScrollPane scrollPaneXmpPhotoshop;
    private JScrollPane scrollPaneXmpTiff;
    private JScrollPane scrollPaneXmpXap;
    private JTabbedPane tabbedPaneXmp;
    private JTable tableXmpCameraRawSettings;
    private JTable tableXmpDc;
    private JTable tableXmpExif;
    private JTable tableXmpIptc;
    private JTable tableXmpLightroom;
    private JTable tableXmpPhotoshop;
    private JTable tableXmpTiff;
    private JTable tableXmpXap;
    private JTextField textFieldTableXmpCameraRawSettingsFilter;
    private JTextField textFieldTableXmpDcFilter;
    private JTextField textFieldTableXmpExifFilter;
    private JTextField textFieldTableXmpIptcFilter;
    private JTextField textFieldTableXmpLightroomFilter;
    private JTextField textFieldTableXmpPhotoshopFilter;
    private JTextField textFieldTableXmpTiffFilter;
    private JTextField textFieldTableXmpXapFilter;
}
