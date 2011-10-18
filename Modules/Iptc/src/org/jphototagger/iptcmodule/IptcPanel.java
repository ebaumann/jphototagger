package org.jphototagger.iptcmodule;

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
import org.jphototagger.iptc.IptcEntry;
import org.jphototagger.lib.swing.TableTextFilter;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swing.util.TableUtil;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.lib.util.Translation;

/**
 * @author Elmar Baumann
 */
public class IptcPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private final IptcTableModel iptcTableModel = new IptcTableModel();
    private final IptcTableCellRenderer iptcTableCellRenderer = new IptcTableCellRenderer();

    public IptcPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        tableIptc.setDefaultRenderer(Object.class, iptcTableCellRenderer);
        setIptcTableTextFilter();
        setIptcTableComparator();
        MnemonicUtil.setMnemonics(this);
    }

    private void setIptcTableTextFilter() {
        TableRowSorter<?> rowSorter = (TableRowSorter<?>) tableIptc.getRowSorter();
        TableStringConverter stringConverter = iptcTableCellRenderer.createTableStringConverter();
        Document document = textFieldTableIptcFilter.getDocument();
        TableTextFilter tableTextFilter = new TableTextFilter(tableIptc, stringConverter);

        rowSorter.setStringConverter(stringConverter);
        document.addDocumentListener(tableTextFilter);
    }

    private void setIptcTableComparator() {
        TableRowSorter<?> rowSorter = (TableRowSorter<?>) tableIptc.getRowSorter();
        Comparator<?> column0Comparator = iptcTableCellRenderer.createColumn0Comparator();
        Comparator<?> column1Comparator = iptcTableCellRenderer.createColumn1Comparator();
        Comparator<?> column2Comparator = iptcTableCellRenderer.createColumn2Comparator();

        rowSorter.setComparator(0, column0Comparator);
        rowSorter.setComparator(1, column1Comparator);
        rowSorter.setComparator(2, column2Comparator);
    }

    private void resizeTable() {
        TableUtil.resizeColumnWidthsToFit(tableIptc);
        ComponentUtil.forceRepaint(tableIptc);
    }

    void removeAllRows() {
        iptcTableModel.removeAllRows();
    }

    void setFile(File file) {
        iptcTableModel.setFile(file);
        resizeTable();
    }

    private final class IptcTableCellRenderer implements TableCellRenderer {

        private final Translation TRANSLATION = new Translation(IptcPanel.class, "IptcRecordDataSetNumberTranslations");
        private final TableLookAndFeel lookAndFeel = Lookup.getDefault().lookup(TableLookAndFeel.class);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (lookAndFeel == null) {
                return new JLabel(StringUtil.toStringNullToEmptyString(value));
            }

            JLabel cellLabel = new JLabel();
            lookAndFeel.setTableCellColor(cellLabel, isSelected);
            IptcEntry iptcEntry = (IptcEntry) value;
            String entryNumber = getIptcEntryNumber(iptcEntry);
            boolean isRowHeader = column == 0;
            int maxChars = isRowHeader ? lookAndFeel.getRowHeaderMaxChars() : lookAndFeel.getCellMaxChars();
            String css = isRowHeader ? lookAndFeel.getRowHeaderCss() : lookAndFeel.getCellCss();
            if (column == 0) {
                lookAndFeel.setTableCellFont(cellLabel);
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel, entryNumber, maxChars, css);
            } else if (column == 1) {
                lookAndFeel.setTableRowHeaderFont(cellLabel);
                String text = TRANSLATION.translate(entryNumber, entryNumber);
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel, text, maxChars, css);
            } else {
                lookAndFeel.setTableCellFont(cellLabel);
                TableUtil.embedTableCellTextInHtml(table, row, cellLabel, iptcEntry.getData(), maxChars, css);
            }

            return cellLabel;
        }

        private String getIptcEntryNumber(IptcEntry iptcEntry) {
            if (iptcEntry == null) {
                return "";
            }

            int recordNumber = iptcEntry.getRecordNumber();
            int dataSetNumber = iptcEntry.getDataSetNumber();

            return Integer.toString(recordNumber) + ":" + Integer.toString(dataSetNumber);
        }

        private Comparator<?> createColumn0Comparator() {
            return new Column0Comparator();
        }

        private Comparator<?> createColumn1Comparator() {
            return new Column1Comparator();
        }

        private Comparator<?> createColumn2Comparator() {
            return new Column2Comparator();
        }

        private class Column0Comparator implements Comparator<Object> {

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof IptcEntry && o2 instanceof IptcEntry) {
                    IptcEntry iptcEntry1 = (IptcEntry) o1;
                    IptcEntry iptcEntry2 = (IptcEntry) o2;
                    String o1String = getIptcEntryNumber(iptcEntry1);
                    String o2String = getIptcEntryNumber(iptcEntry2);

                    return o1String.compareToIgnoreCase(o2String);
                } else {
                    return 0;
                }
            }
        }

        private class Column1Comparator implements Comparator<Object> {

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof IptcEntry && o2 instanceof IptcEntry) {
                    IptcEntry iptcEntry1 = (IptcEntry) o1;
                    IptcEntry iptcEntry2 = (IptcEntry) o2;
                    String entryNumber1 = getIptcEntryNumber(iptcEntry1);
                    String entryNumber2 = getIptcEntryNumber(iptcEntry2);
                    String o1String = TRANSLATION.translate(entryNumber1, entryNumber1);
                    String o2String = TRANSLATION.translate(entryNumber2, entryNumber2);

                    return o1String.compareToIgnoreCase(o2String);
                } else {
                    return 0;
                }
            }
        }

        private class Column2Comparator implements Comparator<Object> {

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof IptcEntry && o2 instanceof IptcEntry) {
                    IptcEntry iptcEntry1 = (IptcEntry) o1;
                    IptcEntry iptcEntry2 = (IptcEntry) o2;
                    String o1String = iptcEntry1.getData();
                    String o2String = iptcEntry2.getData();

                    return o1String.compareToIgnoreCase(o2String);
                } else {
                    return 0;
                }
            }
        }

        public TableStringConverter createTableStringConverter() {
            return new IptcTableStringConverter();
        }

        private class IptcTableStringConverter extends TableStringConverter {

            @Override
            public String toString(TableModel model, int row, int column) {
                Object value = model.getValueAt(row, column);

                if (value instanceof IptcEntry) {
                    IptcEntry iptcEntry = (IptcEntry) value;
                    String entryNumber = getIptcEntryNumber(iptcEntry);

                    return column == 0
                            ? entryNumber
                            : column == 1
                            ? TRANSLATION.translate(entryNumber, entryNumber)
                            : iptcEntry.getData();
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

        panelTableIptcFilter = new JPanel();
        labeTTableIptcFilter = new JLabel();
        textFieldTableIptcFilter = new JTextField();
        scrollPaneIptc = new JScrollPane();
        tableIptc = new JTable();
        buttonIptcToXmp = new JButton();

        setName("Form"); // NOI18N
        setLayout(new GridBagLayout());

        panelTableIptcFilter.setName("panelTableIptcFilter"); // NOI18N
        panelTableIptcFilter.setLayout(new GridBagLayout());

        ResourceBundle bundle = ResourceBundle.getBundle("org/jphototagger/iptcmodule/Bundle"); // NOI18N
        labeTTableIptcFilter.setText(bundle.getString("IptcPanel.labeTTableIptcFilter.text")); // NOI18N
        labeTTableIptcFilter.setName("labeTTableIptcFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panelTableIptcFilter.add(labeTTableIptcFilter, gridBagConstraints);

        textFieldTableIptcFilter.setName("textFieldTableIptcFilter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panelTableIptcFilter.add(textFieldTableIptcFilter, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        add(panelTableIptcFilter, gridBagConstraints);

        scrollPaneIptc.setName("scrollPaneIptc"); // NOI18N

        tableIptc.setAutoCreateRowSorter(true);
        tableIptc.setModel(iptcTableModel);
        tableIptc.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableIptc.setName("tableIptc"); // NOI18N
        scrollPaneIptc.setViewportView(tableIptc);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPaneIptc, gridBagConstraints);

        buttonIptcToXmp.setAction(new ExportIptcToXmpOfSelectedFilesAction());
        buttonIptcToXmp.setIcon(new ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_xmp.png"))); // NOI18N
        buttonIptcToXmp.setText(bundle.getString("IptcPanel.buttonIptcToXmp.text")); // NOI18N
        buttonIptcToXmp.setToolTipText(bundle.getString("IptcPanel.buttonIptcToXmp.toolTipText")); // NOI18N
        buttonIptcToXmp.setMargin(new Insets(2, 2, 2, 2));
        buttonIptcToXmp.setName("buttonIptcToXmp"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 2);
        add(buttonIptcToXmp, gridBagConstraints);
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton buttonIptcToXmp;
    private JLabel labeTTableIptcFilter;
    private JPanel panelTableIptcFilter;
    private JScrollPane scrollPaneIptc;
    private JTable tableIptc;
    private JTextField textFieldTableIptcFilter;
    // End of variables declaration//GEN-END:variables
}
