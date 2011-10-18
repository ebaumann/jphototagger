package org.jphototagger.exifmodule;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Comparator;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;
import javax.swing.text.Document;

import org.openide.util.Lookup;

import org.jphototagger.api.branding.TableLookAndFeel;
import org.jphototagger.domain.metadata.exif.ExifTag;
import org.jphototagger.lib.swing.TableButtonMouseListener;
import org.jphototagger.lib.swing.TableTextFilter;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swing.util.TableUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public class ExifPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private final ExifTableModel exifTableModel = new ExifTableModel();
    private final ExifTableCellRenderer exifTableCellRenderer = new ExifTableCellRenderer();

    public ExifPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        tableExif.setDefaultRenderer(Object.class, exifTableCellRenderer);
        setExifTableTextFilter();
        setExifTableComparator();
        addMouseListener(new TableButtonMouseListener(tableExif));
        MnemonicUtil.setMnemonics(this);
    }

    private void setExifTableTextFilter() {
        TableRowSorter<?> rowSorter = (TableRowSorter<?>) tableExif.getRowSorter();
        TableStringConverter stringConverter = ExifTableCellRenderer.createTableStringConverter();
        Document document = textFieldTableExifFilter.getDocument();
        TableTextFilter tableTextFilter = new TableTextFilter(tableExif, stringConverter);
        rowSorter.setStringConverter(stringConverter);
        document.addDocumentListener(tableTextFilter);
    }

    private void setExifTableComparator() {
        TableRowSorter<?> rowSorter = (TableRowSorter<?>) tableExif.getRowSorter();
        Comparator<?> column0Comparator = ExifTableCellRenderer.createColumn0Comparator();
        Comparator<?> column1Comparator = ExifTableCellRenderer.createColumn1Comparator();

        rowSorter.setComparator(0, column0Comparator);
        rowSorter.setComparator(1, column1Comparator);
    }

    void removeAllRows() {
        exifTableModel.removeAllRows();
    }

    void setFile(File file) {
        exifTableModel.setFile(file);
        resizeTable();
    }

    private void resizeTable() {
        TableUtil.resizeColumnWidthsToFit(tableExif);
        ComponentUtil.forceRepaint(tableExif);
    }

    private static class ExifTableCellRenderer implements TableCellRenderer {

        private final TableLookAndFeel lookAndFeel = Lookup.getDefault().lookup(TableLookAndFeel.class);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (lookAndFeel == null) {
                return new JLabel(StringUtil.toStringNullToEmptyString(value));
            }

            JLabel cellLabel = new JLabel();
            lookAndFeel.setTableCellColor(cellLabel, isSelected);
            if (column == 0) {
                lookAndFeel.setTableRowHeaderFont(cellLabel);
            } else {
                lookAndFeel.setTableCellFont(cellLabel);
            }

            boolean isRowHeader = column == 0;
            int maxChars = isRowHeader ? lookAndFeel.getRowHeaderMaxChars() : lookAndFeel.getCellMaxChars();
            String css = isRowHeader ? lookAndFeel.getRowHeaderCss() : lookAndFeel.getCellCss();
            if (value instanceof ExifTag) {
                ExifTag exifTag = (ExifTag) value;

                if (column == 0) {
                    String displayName = exifTag.getDisplayName();
                    TableUtil.embedTableCellTextInHtml(table, row, cellLabel, displayName, maxChars, css);
                } else {
                    String displayValue = exifTag.getDisplayValue();
                    TableUtil.embedTableCellTextInHtml(table, row, cellLabel, displayValue, maxChars, css);
                }
            } else if (value instanceof Component) {
                return (Component) value;
            }

            return cellLabel;
        }

        public static TableStringConverter createTableStringConverter() {
            return new ExifTableStringConverter();
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
                if (o1 instanceof ExifTag && o2 instanceof ExifTag) {
                    ExifTag exifTag1 = (ExifTag) o1;
                    ExifTag exifTag2 = (ExifTag) o2;
                    String displayName1 = exifTag1.getDisplayName();
                    String displayName2 = exifTag2.getDisplayName();

                    return displayName1.compareToIgnoreCase(displayName2);
                } else {
                    return 0;
                }
            }
        }

        private static class Column1Comparator implements Comparator<Object> {

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof ExifTag && o2 instanceof ExifTag) {
                    ExifTag exifTag1 = (ExifTag) o1;
                    ExifTag exifTag2 = (ExifTag) o2;
                    String displayValue1 = exifTag1.getDisplayValue();
                    String displayValue2 = exifTag2.getDisplayValue();

                    return displayValue1.compareToIgnoreCase(displayValue2);
                } else {
                    return 0;
                }
            }
        }

        private static class ExifTableStringConverter extends TableStringConverter {

            @Override
            public String toString(TableModel model, int row, int column) {
                Object value = model.getValueAt(row, column);

                if (value instanceof ExifTag) {
                    ExifTag exifTag = (ExifTag) value;

                    return column == 0
                            ? exifTag.getDisplayName()
                            : exifTag.getDisplayValue();
                } else {
                    return StringUtil.toStringNullToEmptyString(value);
                }
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        GridBagConstraints gridBagConstraints;

        panelTableExifFilter = new JPanel();
        labelTableExifFilter = new JLabel();
        textFieldTableExifFilter = new JTextField();
        scrollPaneExif = new JScrollPane();
        tableExif = new JTable();
        buttonExifToXmp = new JButton();

        setName("Form"); // NOI18N
        setLayout(new GridBagLayout());

        panelTableExifFilter.setName("panelTableExifFilter"); // NOI18N
        panelTableExifFilter.setLayout(new GridBagLayout());

        ResourceBundle bundle = ResourceBundle.getBundle("org/jphototagger/exifmodule/Bundle"); // NOI18N
        labelTableExifFilter.setText(bundle.getString("ExifPanel.labelTableExifFilter.text")); // NOI18N
        labelTableExifFilter.setName("labelTableExifFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panelTableExifFilter.add(labelTableExifFilter, gridBagConstraints);

        textFieldTableExifFilter.setName("textFieldTableExifFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panelTableExifFilter.add(textFieldTableExifFilter, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(panelTableExifFilter, gridBagConstraints);

        scrollPaneExif.setName("scrollPaneExif"); // NOI18N

        tableExif.setAutoCreateRowSorter(true);
        tableExif.setModel(exifTableModel);
        tableExif.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableExif.setName("tableExif"); // NOI18N
        scrollPaneExif.setViewportView(tableExif);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPaneExif, gridBagConstraints);

        buttonExifToXmp.setAction(new SetExifToXmpAction());
        buttonExifToXmp.setIcon(new ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp.png"))); // NOI18N
        buttonExifToXmp.setText(bundle.getString("ExifPanel.buttonExifToXmp.text")); // NOI18N
        buttonExifToXmp.setToolTipText(bundle.getString("ExifPanel.buttonExifToXmp.toolTipText")); // NOI18N
        buttonExifToXmp.setMargin(new Insets(2, 2, 2, 2));
        buttonExifToXmp.setName("buttonExifToXmp"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 2);
        add(buttonExifToXmp, gridBagConstraints);
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton buttonExifToXmp;
    private JLabel labelTableExifFilter;
    private JPanel panelTableExifFilter;
    private JScrollPane scrollPaneExif;
    private JTable tableExif;
    private JTextField textFieldTableExifFilter;
    // End of variables declaration//GEN-END:variables
}
