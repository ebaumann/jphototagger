package de.elmar_baumann.lib.dialog;

import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.util.logging.LogfileParser;
import de.elmar_baumann.lib.util.logging.LogfileRecord;
import de.elmar_baumann.lib.util.logging.LogfileRecordException;
import de.elmar_baumann.lib.util.logging.LogfileRecordFrame;
import de.elmar_baumann.lib.model.TableModelLogfiles;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.persistence.PersistentSettingsHints;
import de.elmar_baumann.lib.resource.LogLevelIcons;
import de.elmar_baumann.lib.renderer.TableCellRendererLogfileDialog;
import de.elmar_baumann.lib.resource.Bundle;
import de.elmar_baumann.lib.resource.Settings;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLDocument;

/**
 * Nichtmodaler Dialog zum Anzeigen einer Logdatei geschrieben von einem
 * <code>java.util.logging.Logger</code>-Objekt. Das XML-Format muss validieren
 * gegen die <code>logger.dtd</code>.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class LogfileDialog extends javax.swing.JDialog implements
    ListSelectionListener, ActionListener {

    private static final long criticalLogfileSizeInBytes = 10 * 1024 * 1024;
    private final Map<JCheckBox, Level> levelOfCheckBox = new HashMap<JCheckBox, Level>();
    private final Map<Class, Integer> paneIndexOfFormatterClass = new HashMap<Class, Integer>();
    private final List<Level> visibleLevels = new ArrayList<Level>();
    private String filterString;
    private List<LogfileRecord> logfileRecords;
    private Class formatterClass;
    private String logfilename;

    private void initPaneIndexOfLogfileType() {
        paneIndexOfFormatterClass.put(XMLFormatter.class, 0);
        paneIndexOfFormatterClass.put(SimpleFormatter.class, 1);
    }

    /**
     * Konstruktor.
     * 
     * @param parent          Elternframe
     * @param logfilename     Name der anzuzeigenden Logdatei
     * @param formatterClass  Formatierer der Logdatei
     */
    public LogfileDialog(Frame parent, String logfilename, Class formatterClass) {
        super(parent, false);
        this.logfilename = logfilename;
        this.formatterClass = formatterClass;
        initPaneIndexOfLogfileType();
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setIcons();
        setLabelIcons();
        initTextPaneDetails(); // NOI18N
        initTableLogfileRecords();
        initLevelOfCheckbox();
        listenToCheckboxes();
    }

    private void setIcons() {
        if (Settings.INSTANCE.hasIconImages()) {
            setIconImages(IconUtil.getIconImages(
                Settings.INSTANCE.getIconImagesPaths()));
        }
    }

    private void setLabelIcons() {
        labelIconConfig.setIcon(LogLevelIcons.getIcon(Level.CONFIG));
        labelIconFine.setIcon(LogLevelIcons.getIcon(Level.FINE));
        labelIconFiner.setIcon(LogLevelIcons.getIcon(Level.FINER));
        labelIconFinest.setIcon(LogLevelIcons.getIcon(Level.FINEST));
        labelIconInfo.setIcon(LogLevelIcons.getIcon(Level.INFO));
        labelIconSevere.setIcon(LogLevelIcons.getIcon(Level.SEVERE));
        labelIconWarning.setIcon(LogLevelIcons.getIcon(Level.WARNING));
    }

    private void initTextPaneDetails() {
        textPaneDetails.setStyledDocument(new HTMLDocument());
        textPaneDetails.setContentType("text/html"); // NOI18N
    }

    private void initTableLogfileRecords() {
        tableLogfileRecords.getSelectionModel().addListSelectionListener(this);
        tableLogfileRecords.getColumnModel().getSelectionModel().addListSelectionListener(this);
        tableLogfileRecords.setDefaultRenderer(Object.class, new TableCellRendererLogfileDialog());
    }

    private void initLevelOfCheckbox() {
        levelOfCheckBox.put(checkBoxConfig, Level.CONFIG);
        levelOfCheckBox.put(checkBoxFine, Level.FINE);
        levelOfCheckBox.put(checkBoxFiner, Level.FINER);
        levelOfCheckBox.put(checkBoxFinest, Level.FINEST);
        levelOfCheckBox.put(checkBoxInfo, Level.INFO);
        levelOfCheckBox.put(checkBoxSevere, Level.SEVERE);
        levelOfCheckBox.put(checkBoxWarning, Level.WARNING);
    }

    private void listenToCheckboxes() {
        Set<JCheckBox> checkBoxes = levelOfCheckBox.keySet();
        for (JCheckBox checkBox : checkBoxes) {
            checkBox.addActionListener(this);
        }
    }

    private void reload() {
        boolean simple = tabbedPane.getSelectedIndex() ==
            paneIndexOfFormatterClass.get(SimpleFormatter.class);
        if (simple) {
            editorPaneSimple.setText(FileUtil.getFileAsString(logfilename));
        } else {
            readLogfileRecords();
            setTable();
        }
    }

    private void resetVisibeLevels() {
        visibleLevels.clear();
        Set<JCheckBox> checkBoxes = levelOfCheckBox.keySet();
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                visibleLevels.add(levelOfCheckBox.get(checkBox));
            }
        }
        setTable();
        if (tableLogfileRecords.getSelectedRow() < 0) {
            textPaneDetails.setText(""); // NOI18N
        }
    }

    private boolean checkSize() {
        long logfileBytes = new File(logfilename).length();
        if (logfileBytes <= 0) {
            JOptionPane.showMessageDialog(
                this,
                Bundle.getString("LogfileDialog.ErrorMessage.LogfileIsEmpty"),
                Bundle.getString("LogfileDialog.ErrorMessage.LogfileIsEmpty.Title"),
                JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (logfileBytes >= criticalLogfileSizeInBytes) {
            MessageFormat msg = new MessageFormat(Bundle.getString("LogfileDialog.ErrorMessage.MaximumSizeExceeded"));
            Object[] params = {new Integer(Math.round(logfileBytes / criticalLogfileSizeInBytes))};
            JOptionPane.showMessageDialog(
                this,
                msg.format(params),
                Bundle.getString("LogfileDialog.ErrorMessage.MaximumSizeExceeded.Title"),
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void readLogfileRecords() {
        flushLoggerHandlers();
        logfileRecords = LogfileParser.parseLogfile(logfilename);
    }

    private void search() {
        filterString = textFieldSearch.getText();
        setTable();
    }

    private void setColumnWidths() {
        int width = getWidth() - 20;
        int widthColumn0 = 20;
        int widthColumn1 = 150;
        int widthColumn2 = width - widthColumn0 - widthColumn1;
        if (widthColumn2 > 0) {
            tableLogfileRecords.getColumnModel().getColumn(0).setPreferredWidth(widthColumn0);
            tableLogfileRecords.getColumnModel().getColumn(1).setPreferredWidth(widthColumn1);
            tableLogfileRecords.getColumnModel().getColumn(2).setPreferredWidth(widthColumn2);
        }
    }

    private void showDetails() {
        TableModelLogfiles model = (TableModelLogfiles) tableLogfileRecords.getModel();
        int selectedRowIndex = tableLogfileRecords.getSelectedRow();
        if (selectedRowIndex >= 0) {
            showDetails(model.getLogfileRecord(selectedRowIndex));
        }
    }

    private void showDetails(LogfileRecord logfileRecord) {
        StringBuffer details = new StringBuffer(1000);

        details.append("<html>"); // NOI18N
        details.append("\n<table>"); // NOI18N
        addDetailTableRow(details, Bundle.getString("LogfileDialog.Information.Loglevel"),
            logfileRecord.getLevel().getLocalizedName());
        addDetailTableRow(details, Bundle.getString("LogfileDialog.Information.Message"),
            logfileRecord.getMessage());
        addDetailTableRow(details, Bundle.getString("LogfileDialog.Information.LoggerClass"),
            logfileRecord.getLogger());
        addDetailTableRow(details, Bundle.getString("LogfileDialog.Information.Class"),
            logfileRecord.getClassname());
        addDetailTableRow(details, Bundle.getString("LogfileDialog.Information.Method"),
            logfileRecord.getMethodname());
        addDetailTableRow(details, Bundle.getString("LogfileDialog.Information.Thread"),
            logfileRecord.getThread());
        details.append("\n</table>"); // NOI18N
        addDetailException(details, logfileRecord.getException());
        details.append("\n</html>"); // NOI18N

        textPaneDetails.setText(details.toString());
    }

    private void addDetailTableRow(StringBuffer stringBuffer, String rowHeader,
        String rowData) {
        if (rowData != null) {
            stringBuffer.append("\n\t<tr>"); // NOI18N
            stringBuffer.append("\n\t\t<td>"); // NOI18N
            stringBuffer.append("<strong>" + rowHeader + "</strong>"); // NOI18N
            stringBuffer.append("</td>"); // NOI18N
            stringBuffer.append("<td><font color=\"#5555aa\">"); // NOI18N
            stringBuffer.append(rowData);
            stringBuffer.append("</font></td>"); // NOI18N
            stringBuffer.append("\n\t</tr>"); // NOI18N
        }
    }

    private void addDetailException(StringBuffer stringBuffer,
        LogfileRecordException ex) {
        if (ex != null) {
            addDetailExceptionMessage(ex, stringBuffer);
            stringBuffer.append("\n<pre>"); // NOI18N
            List<LogfileRecordFrame> frames = ex.getFrames();
            for (LogfileRecordFrame frame : frames) {
                stringBuffer.append("\n" + frame.getClassName() + ":"); // NOI18N
                stringBuffer.append(" " + frame.getMethodName()); // NOI18N
                stringBuffer.append(
                    Bundle.getString("LogfileDialog.Information.StartLineNumber") + frame.getLine() + ")"); // NOI18N
            }
            stringBuffer.append("\n</pre>"); // NOI18N
        }
    }

    private void addDetailExceptionMessage(LogfileRecordException exception,
        StringBuffer stringBuffer) {
        String message = exception.getMessage();
        if (message != null) {
            stringBuffer.append("\n<br /><font color=\"ff0000\">" + message + "</font>"); // NOI18N
        }
    }

    private void setTable() {
        if (logfilename != null && !logfilename.isEmpty()) {
            TableModelLogfiles model = new TableModelLogfiles();
            model.setFilter(filterString);
            model.setVisibleLevels(visibleLevels);
            tableLogfileRecords.setModel(model);
            model.setRecords(logfileRecords);
            setColumnWidths();
            scrollPaneTableLogfileRecords.getViewport().setViewPosition(new Point(0, 0));
        }
    }

    private void flushLoggerHandlers() {
        Handler[] handlers = Logger.getLogger("").getHandlers(); // NOI18N
        for (Handler handler : handlers) {
            handler.flush();
        }
    }

    private boolean isCheckbox(Object source) {
        Set<JCheckBox> checkBoxes = levelOfCheckBox.keySet();
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox == source) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        showDetails();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isCheckbox(e.getSource())) {
            resetVisibeLevels();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && checkSize()) {
            if (getFormatterClass().equals(XMLFormatter.class)) {
                readXml();
            } else if (getFormatterClass().equals(SimpleFormatter.class)) {
                readSimple();
            } else {
                errorMessageNotSupportedFormat();
                readSimple();
            }
            PersistentAppSizes.getSizeAndLocation(this);
            PersistentSettings.getInstance().getComponent(this, new PersistentSettingsHints());
            super.setVisible(true);
        } else {
            PersistentAppSizes.setSizeAndLocation(this);
            PersistentSettings.getInstance().setComponent(this, new PersistentSettingsHints());
            super.setVisible(false);
        }
    }

    private void errorMessageNotSupportedFormat() {
        JOptionPane.showMessageDialog(
            this,
            Bundle.getString("LogfileDialog.ErrorMessage.UnknownLogfileFormat"),
            Bundle.getString("LogfileDialog.ErrorMessage.UnknownLogfileFormat.Title"),
            JOptionPane.ERROR_MESSAGE);
    }

    private void readSimple() {
        selectPane();
        editorPaneSimple.setText(FileUtil.getFileAsString(logfilename));
    }

    private void readXml() {
        selectPane();
        filterString = textFieldSearch.getText();
        readLogfileRecords();
        resetVisibeLevels();
        textFieldSearch.requestFocus();
    }

    private void selectPane() {
        Class fClass = getFormatterClass();
        Set<Class> classes = paneIndexOfFormatterClass.keySet();
        for (Class c : classes) {
            int panelIndex = paneIndexOfFormatterClass.get(c);
            boolean isCurrentFormatter = c.equals(fClass);
            if (isCurrentFormatter) {
                tabbedPane.setSelectedIndex(panelIndex);
            }
            tabbedPane.setEnabledAt(panelIndex, isCurrentFormatter);
        }
    }

    /**
     * Liefert, ob ein Logdateiformat unterstützt wird.
     * 
     * @param  f  Format
     * @return true, wenn das Format unterstützt wird
     */
    public boolean isSupportedFormat(Formatter f) {
        return paneIndexOfFormatterClass.containsKey(f);
    }

    /**
     * Setzt die Formatiererklasse der Logdatei.
     * 
     * @param formatterClass Klasse
     */
    public void setFormatter(Class formatterClass) {
        this.formatterClass = formatterClass;
    }

    /**
     * Liefert die Formatiererklasse der Logdatei.
     * 
     * @return Typ
     */
    public Class getFormatterClass() {
        return formatterClass;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        panelXml = new javax.swing.JPanel();
        panelFilter = new javax.swing.JPanel();
        panelFilterCheckBoxes = new javax.swing.JPanel();
        labelIconSevere = new javax.swing.JLabel();
        checkBoxSevere = new javax.swing.JCheckBox();
        labelIconInfo = new javax.swing.JLabel();
        checkBoxInfo = new javax.swing.JCheckBox();
        labelIconFine = new javax.swing.JLabel();
        checkBoxFine = new javax.swing.JCheckBox();
        labelIconFinest = new javax.swing.JLabel();
        checkBoxFinest = new javax.swing.JCheckBox();
        labelIconWarning = new javax.swing.JLabel();
        checkBoxWarning = new javax.swing.JCheckBox();
        labelIconConfig = new javax.swing.JLabel();
        checkBoxConfig = new javax.swing.JCheckBox();
        labelIconFiner = new javax.swing.JLabel();
        checkBoxFiner = new javax.swing.JCheckBox();
        panelSearch = new javax.swing.JPanel();
        labelSearch = new javax.swing.JLabel();
        textFieldSearch = new javax.swing.JTextField();
        scrollPaneTextPaneDetails = new javax.swing.JScrollPane();
        textPaneDetails = new javax.swing.JTextPane();
        scrollPaneTableLogfileRecords = new javax.swing.JScrollPane();
        tableLogfileRecords = new javax.swing.JTable();
        panelSimple = new javax.swing.JPanel();
        scrollPaneSimple = new javax.swing.JScrollPane();
        editorPaneSimple = new javax.swing.JEditorPane();
        buttonReload = new javax.swing.JButton();
        buttonExit = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/lib/resource/Bundle"); // NOI18N
        setTitle(bundle.getString("LogfileDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("LogfileDialog.panelFilter.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelIconSevere.setPreferredSize(new java.awt.Dimension(16, 16));

        checkBoxSevere.setSelected(true);
        checkBoxSevere.setText(Level.SEVERE.getLocalizedName());

        labelIconInfo.setPreferredSize(new java.awt.Dimension(16, 16));

        checkBoxInfo.setSelected(true);
        checkBoxInfo.setText(Level.INFO.getLocalizedName());

        labelIconFine.setPreferredSize(new java.awt.Dimension(16, 16));

        checkBoxFine.setSelected(true);
        checkBoxFine.setText(Level.FINE.getLocalizedName());

        labelIconFinest.setPreferredSize(new java.awt.Dimension(16, 16));

        checkBoxFinest.setSelected(true);
        checkBoxFinest.setText(Level.FINEST.getLocalizedName());

        labelIconWarning.setPreferredSize(new java.awt.Dimension(16, 16));

        checkBoxWarning.setSelected(true);
        checkBoxWarning.setText(Level.WARNING.getLocalizedName());

        labelIconConfig.setPreferredSize(new java.awt.Dimension(16, 16));

        checkBoxConfig.setSelected(true);
        checkBoxConfig.setText(Level.CONFIG.getLocalizedName());

        labelIconFiner.setPreferredSize(new java.awt.Dimension(16, 16));

        checkBoxFiner.setSelected(true);
        checkBoxFiner.setText(Level.FINER.getLocalizedName());

        javax.swing.GroupLayout panelFilterCheckBoxesLayout = new javax.swing.GroupLayout(panelFilterCheckBoxes);
        panelFilterCheckBoxes.setLayout(panelFilterCheckBoxesLayout);
        panelFilterCheckBoxesLayout.setHorizontalGroup(
            panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelIconWarning, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(labelIconSevere, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxWarning, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkBoxSevere, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelIconConfig, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(labelIconInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkBoxConfig, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelIconFiner, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(labelIconFine, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                        .addComponent(checkBoxFine, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelIconFinest, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                        .addGap(14, 14, 14)
                        .addComponent(checkBoxFinest, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))
                    .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                        .addComponent(checkBoxFiner, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                        .addGap(121, 121, 121)))
                .addContainerGap())
        );
        panelFilterCheckBoxesLayout.setVerticalGroup(
            panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(checkBoxSevere, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(labelIconInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(checkBoxInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(labelIconFine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(checkBoxFine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(labelIconFinest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(checkBoxFinest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(labelIconSevere, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(checkBoxFiner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(labelIconFiner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(checkBoxConfig, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(labelIconConfig, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(checkBoxWarning, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(labelIconWarning, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        labelSearch.setText(bundle.getString("LogfileDialog.labelSearch.text")); // NOI18N

        textFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldSearchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panelSearchLayout = new javax.swing.GroupLayout(panelSearch);
        panelSearch.setLayout(panelSearchLayout);
        panelSearchLayout.setHorizontalGroup(
            panelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSearchLayout.createSequentialGroup()
                .addComponent(labelSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE))
        );
        panelSearchLayout.setVerticalGroup(
            panelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(labelSearch)
                .addComponent(textFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter);
        panelFilter.setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelFilterLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(panelSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panelFilterCheckBoxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFilterLayout.createSequentialGroup()
                .addComponent(panelFilterCheckBoxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        textPaneDetails.setEditable(false);
        scrollPaneTextPaneDetails.setViewportView(textPaneDetails);

        tableLogfileRecords.setAutoCreateRowSorter(true);
        tableLogfileRecords.setModel(new de.elmar_baumann.lib.model.TableModelLogfiles());
        tableLogfileRecords.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableLogfileRecords.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPaneTableLogfileRecords.setViewportView(tableLogfileRecords);

        javax.swing.GroupLayout panelXmlLayout = new javax.swing.GroupLayout(panelXml);
        panelXml.setLayout(panelXmlLayout);
        panelXmlLayout.setHorizontalGroup(
            panelXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelXmlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneTableLogfileRecords, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addComponent(panelFilter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addComponent(scrollPaneTextPaneDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelXmlLayout.setVerticalGroup(
            panelXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelXmlLayout.createSequentialGroup()
                .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneTableLogfileRecords, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneTextPaneDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("LogfileDialog.panelXml.TabConstraints.tabTitle"), panelXml); // NOI18N

        editorPaneSimple.setEditable(false);
        scrollPaneSimple.setViewportView(editorPaneSimple);

        javax.swing.GroupLayout panelSimpleLayout = new javax.swing.GroupLayout(panelSimple);
        panelSimple.setLayout(panelSimpleLayout);
        panelSimpleLayout.setHorizontalGroup(
            panelSimpleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSimpleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneSimple)
                .addContainerGap())
        );
        panelSimpleLayout.setVerticalGroup(
            panelSimpleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSimpleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneSimple, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("LogfileDialog.panelSimple.TabConstraints.tabTitle"), panelSimple); // NOI18N

        buttonReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/lib/resource/icon_reload.png"))); // NOI18N
        buttonReload.setToolTipText(bundle.getString("LogfileDialog.buttonReload.toolTipText")); // NOI18N
        buttonReload.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonReload.setPreferredSize(new java.awt.Dimension(40, 40));
        buttonReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReloadActionPerformed(evt);
            }
        });

        buttonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/lib/resource/icon_exit.png"))); // NOI18N
        buttonExit.setToolTipText(bundle.getString("LogfileDialog.buttonExit.toolTipText")); // NOI18N
        buttonExit.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonExit.setPreferredSize(new java.awt.Dimension(40, 40));
        buttonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonReload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonReload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void textFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldSearchKeyReleased
    search();
}//GEN-LAST:event_textFieldSearchKeyReleased

private void buttonReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReloadActionPerformed
    reload();
}//GEN-LAST:event_buttonReloadActionPerformed

private void buttonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExitActionPerformed
    setVisible(false);
}//GEN-LAST:event_buttonExitActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    setVisible(false);
}//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                LogfileDialog dialog = new LogfileDialog(new javax.swing.JFrame(), "", XMLFormatter.class); // NOI18N
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonExit;
    private javax.swing.JButton buttonReload;
    private javax.swing.JCheckBox checkBoxConfig;
    private javax.swing.JCheckBox checkBoxFine;
    private javax.swing.JCheckBox checkBoxFiner;
    private javax.swing.JCheckBox checkBoxFinest;
    private javax.swing.JCheckBox checkBoxInfo;
    private javax.swing.JCheckBox checkBoxSevere;
    private javax.swing.JCheckBox checkBoxWarning;
    private javax.swing.JEditorPane editorPaneSimple;
    private javax.swing.JLabel labelIconConfig;
    private javax.swing.JLabel labelIconFine;
    private javax.swing.JLabel labelIconFiner;
    private javax.swing.JLabel labelIconFinest;
    private javax.swing.JLabel labelIconInfo;
    private javax.swing.JLabel labelIconSevere;
    private javax.swing.JLabel labelIconWarning;
    private javax.swing.JLabel labelSearch;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JPanel panelFilterCheckBoxes;
    private javax.swing.JPanel panelSearch;
    private javax.swing.JPanel panelSimple;
    private javax.swing.JPanel panelXml;
    private javax.swing.JScrollPane scrollPaneSimple;
    private javax.swing.JScrollPane scrollPaneTableLogfileRecords;
    private javax.swing.JScrollPane scrollPaneTextPaneDetails;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tableLogfileRecords;
    private javax.swing.JTextField textFieldSearch;
    private javax.swing.JTextPane textPaneDetails;
    // End of variables declaration//GEN-END:variables
}
