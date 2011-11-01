package org.jphototagger.lib.util.logging;

import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.text.html.HTMLDocument;

import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * Non modal dialog to display a logfile written by a
 * <code>java.util.logging.Logger</code>.
 * The XML format has to validate against <code>logger.dtd</code>.
 *
 * @author Elmar Baumann
 */
public final class LogfileDialog extends Dialog implements ListSelectionListener, ActionListener {

    private static final long serialVersionUID  = 1L;
    public static final long  DEFAULT_MAX_BYTES = 10 * 1024 * 1024;
    private long maxBytes = DEFAULT_MAX_BYTES;
    private final Map<JCheckBox, Level> levelOfCheckBox = new HashMap<JCheckBox, Level>();
    private final Map<JCheckBox, JLabel> iconLabelOfCheckBox = new HashMap<JCheckBox, JLabel>();
    private final Map<Class<?>, Integer> paneIndexOfFormatterClass = new HashMap<Class<?>, Integer>();
    private final List<Level> visibleLevels = new ArrayList<Level>();
    private String filterString;
    private List<LogfileRecord> logfileRecords;
    private Class<?> formatterClass;
    private String logfilename;

    public LogfileDialog(Frame parent, String logfilename, Class<?> formatterClass) {
        super(parent, false);

        if (logfilename == null) {
            throw new NullPointerException("logfilename == null");
        }

        if (formatterClass == null) {
            throw new NullPointerException("formatterClass == null");
        }

        this.logfilename    = logfilename;
        this.formatterClass = formatterClass;
        initPaneIndexOfLogfileType();
        initComponents();
        postInitComponents();
    }

    private void initPaneIndexOfLogfileType() {
        paneIndexOfFormatterClass.put(XMLFormatter.class, 0);
        paneIndexOfFormatterClass.put(SimpleFormatter.class, 1);
    }

    private void postInitComponents() {
        setLogfileNameLabelText();
        initTextPaneDetails();
        initTableLogfileRecords();
        initLevelOfCheckbox();
        listenToCheckboxes();
    }

    private void setLogfileNameLabelText() {
        String pattern = labelLogfileName.getText();

        labelLogfileName.setText(MessageFormat.format(pattern, logfilename));
    }

    private void initTextPaneDetails() {
        textPaneDetails.setStyledDocument(new HTMLDocument());
        textPaneDetails.setContentType("text/html");
    }

    private void initTableLogfileRecords() {
        tableLogfileRecords.getSelectionModel().addListSelectionListener(this);
        tableLogfileRecords.getColumnModel().getSelectionModel().addListSelectionListener(this);
        tableLogfileRecords.setDefaultRenderer(Object.class, new LogfileDialogTableCellRenderer());
    }

    private void initLevelOfCheckbox() {
        levelOfCheckBox.put(checkBoxConfig, Level.CONFIG);
        levelOfCheckBox.put(checkBoxFine, Level.FINE);
        levelOfCheckBox.put(checkBoxFiner, Level.FINER);
        levelOfCheckBox.put(checkBoxFinest, Level.FINEST);
        levelOfCheckBox.put(checkBoxInfo, Level.INFO);
        levelOfCheckBox.put(checkBoxSevere, Level.SEVERE);
        levelOfCheckBox.put(checkBoxWarning, Level.WARNING);
        iconLabelOfCheckBox.put(checkBoxConfig, labelIconConfig);
        iconLabelOfCheckBox.put(checkBoxFine, labelIconFine);
        iconLabelOfCheckBox.put(checkBoxFiner, labelIconFiner);
        iconLabelOfCheckBox.put(checkBoxFinest, labelIconFinest);
        iconLabelOfCheckBox.put(checkBoxInfo, labelIconInfo);
        iconLabelOfCheckBox.put(checkBoxSevere, labelIconSevere);
        iconLabelOfCheckBox.put(checkBoxWarning, labelIconWarning);
    }

    private void listenToCheckboxes() {
        Set<JCheckBox> checkBoxes = levelOfCheckBox.keySet();

        for (JCheckBox checkBox : checkBoxes) {
            checkBox.addActionListener(this);
        }
    }

    private void reload() {
        boolean simple = SimpleFormatter.class.equals(formatterClass);

        if (simple) {
            try {
                textAreaSimple.setText(FileUtil.getContentAsString(new File(logfilename), "UTF-8"));
            } catch (IOException ex) {
                Logger.getLogger(LogfileDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            textPaneDetails.setText("");
        }
    }

    private boolean checkFileSize() {
        long logfileBytes = new File(logfilename).length();

        if (logfileBytes <= 0) {
            errorMessageEmpty();
            return false;
        } else if (logfileBytes >= maxBytes) {
            errorMessageMaxBytes(logfileBytes);
            return false;
        }

        return true;
    }

    private void errorMessageEmpty() throws HeadlessException {
        String message = Bundle.getString(LogfileDialog.class, "LogfileDialog.Error.LogfileIsEmpty");
        String title = Bundle.getString(LogfileDialog.class, "LogfileDialog.Error.LogfileIsEmpty.Title");

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void errorMessageMaxBytes(long logfileBytes) {
        int maxSizeInMeagbytes = Math.round((float) logfileBytes / (float) maxBytes);
        String message = Bundle.getString(LogfileDialog.class, "LogfileDialog.Error.MaximumSizeExceeded", maxSizeInMeagbytes);
        String title = Bundle.getString(LogfileDialog.class, "LogfileDialog.Error.MaximumSizeExceeded.Title");

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public long getMaxBytes() {
        return maxBytes;
    }

    /**
     * Sets the maximum bytes of the logfile size, which can be displayed.
     * <p>
     * Otherwise the dialog does not open the log file.
     *
     * @param maxBytes maximum amount of bytes. Default: {@code #DEFAULT_MAX_BYTES}.
     */
    public void setMaxBytes(long maxBytes) {
        this.maxBytes = maxBytes;
    }

    public void setFilterableMinIntValue(int intValue) {
        for (JCheckBox levelCheckBox : levelOfCheckBox.keySet()) {
            Level level = levelOfCheckBox.get(levelCheckBox);
            int levelIntValue = level.intValue();

            if (levelIntValue < intValue) {
                JLabel iconLabel = iconLabelOfCheckBox.get(levelCheckBox);

                panelFilterCheckBoxes.remove(iconLabel);
                panelFilterCheckBoxes.remove(levelCheckBox);
            }
        }
    }

    private void readLogfileRecords() {
        flushLoggerHandlers();
        logfileRecords = LogfileParser.parseLogfile(logfilename);
    }

    private void filterTable() {
        filterString = textFieldSearch.getText();
        setTable();
    }

    private void setColumnWidths() {
        Rectangle bounds = scrollPaneTableLogfileRecords.getViewportBorderBounds();
        int width = bounds.width;
        int widthColumn0 = 25;
        int widthColumn1 = 150;
        int widthColumn2 = width - widthColumn0 - widthColumn1;

        if (widthColumn2 > 0) {
            TableColumnModel columnModel = tableLogfileRecords.getColumnModel();

            columnModel.getColumn(0).setPreferredWidth(widthColumn0);
            columnModel.getColumn(1).setPreferredWidth(widthColumn1);
            columnModel.getColumn(2).setPreferredWidth(widthColumn2);
        }
    }

    private void showDetails() {
        LogfilesTableModel model = (LogfilesTableModel) tableLogfileRecords.getModel();
        int selectedRowIndex = tableLogfileRecords.getSelectedRow();

        if (selectedRowIndex >= 0) {
            showDetails(model.getLogfileRecord(selectedRowIndex));
        }
    }

    private void showDetails(LogfileRecord logfileRecord) {
        StringBuffer details = new StringBuffer(1000);

        details.append("<html>");
        details.append("\n<table>");
        addDetailTableRow(
            details,
            Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.Loglevel"),
            logfileRecord.getLevel().getLocalizedName());
        addDetailTableRow(
            details,
            Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.Message"),
            logfileRecord.getMessage());
        addDetailTableRow(
            details,
            Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.LoggerClass"),
            logfileRecord.getLogger());
        addDetailTableRow(
            details, Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.Class"),
            logfileRecord.getClassname());
        addDetailTableRow(
            details, Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.Method"),
            logfileRecord.getMethodname());
        addDetailTableRow(
            details, Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.Thread"),
            logfileRecord.getThread());
        details.append("\n</table>");
        addDetailException(details, logfileRecord.getException());
        details.append("\n</html>");
        textPaneDetails.setText(details.toString());
        textPaneDetails.moveCaretPosition(0);
    }

    private void addDetailTableRow(StringBuffer stringBuffer, String rowHeader,
                                   String rowData) {
        if (rowData != null) {
            stringBuffer.append("\n\t<tr>");
            stringBuffer.append("\n\t\t<td>");
            stringBuffer.append("<strong>").append(rowHeader).append("</strong>");
            stringBuffer.append("</td>");
            stringBuffer.append("<td><font color=\"#5555aa\">");
            stringBuffer.append(rowData);
            stringBuffer.append("</font></td>");
            stringBuffer.append("\n\t</tr>");
        }
    }

    private void addDetailException(StringBuffer stringBuffer,
                                    ExceptionLogfileRecord ex) {
        if (ex != null) {
            addDetailExceptionMessage(ex, stringBuffer);
            stringBuffer.append("\n<pre>");

            List<FrameLogfileRecord> frames = ex.getFrames();

            for (FrameLogfileRecord frame : frames) {
                stringBuffer
                        .append("\n")
                        .append(frame.getClassName())
                        .append(":");
                stringBuffer
                        .append(" ")
                        .append(frame.getMethodName());
                stringBuffer
                        .append(Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.StartLineNumber"))
                        .append(frame.getLine())
                        .append(")");
            }

            stringBuffer.append("\n</pre>");
        }
    }

    private void addDetailExceptionMessage(ExceptionLogfileRecord exception,
            StringBuffer stringBuffer) {
        String message = exception.getMessage();

        if (message != null) {
            stringBuffer
                    .append("\n<br /><font color=\"ff0000\">")
                    .append(message)
                    .append("</font>");
        }
    }

    private void setTable() {
        if ((logfilename != null) &&!logfilename.isEmpty()) {
            LogfilesTableModel model = new LogfilesTableModel(filterString, visibleLevels);

            tableLogfileRecords.setModel(model);
            Collections.sort(logfileRecords, LogfileRecordComparatorDescendingByTime.INSTANCE);
            model.setRecords(logfileRecords);
            scrollPaneTableLogfileRecords.getViewport().setViewPosition(new Point(0, 0));
            setColumnWidths();
        }
    }

    private void flushLoggerHandlers() {
        Handler[] handlers = Logger.getLogger("").getHandlers();

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
    public void valueChanged(ListSelectionEvent evt) {
        showDetails();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (isCheckbox(evt.getSource())) {
            resetVisibeLevels();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && checkFileSize()) {
            boolean simpleFormatter = formatterClass.equals(SimpleFormatter.class);
            boolean xmlFormatter = formatterClass.equals(XMLFormatter.class);

            panelSearchSimple.setEnabled(simpleFormatter);

            if (xmlFormatter) {
                readXml();
                panelSearchXml.requestFocusInWindow();
                remove(panelSearchSimple);
            } else if (simpleFormatter) {
                readSimple();
                panelSearchSimple.requestFocusToSearchTextField();
            } else {
                errorMessageNotSupportedFormat();
                readSimple();
            }
        }
        super.setVisible(visible);
    }

    private void errorMessageNotSupportedFormat() {
        String message = Bundle.getString(LogfileDialog.class, "LogfileDialog.Error.UnknownLogfileFormat");
        String title = Bundle.getString(LogfileDialog.class, "LogfileDialog.Error.UnknownLogfileFormat.Title");

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void readSimple() {
        showCard("panelSimple");
        try {
            textAreaSimple.setText(FileUtil.getContentAsString(new File(logfilename), "UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(LogfileDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readXml() {
        showCard("panelXml");
        filterString = textFieldSearch.getText();
        readLogfileRecords();
        resetVisibeLevels();
        textFieldSearch.requestFocus();
    }

    private void showCard(String cardName) {
        CardLayout cardLayout = (CardLayout) panelCards.getLayout();

        cardLayout.show(panelCards, cardName);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = new javax.swing.JPanel();
        labelLogfileName = new javax.swing.JLabel();
        panelButtons = new javax.swing.JPanel();
        buttonReload = new javax.swing.JButton();
        buttonExit = new javax.swing.JButton();
        panelCards = new javax.swing.JPanel();
        panelXml = new javax.swing.JPanel();
        panelFilter = new javax.swing.JPanel();
        panelFilterCheckBoxes = new javax.swing.JPanel();
        labelIconSevere = new javax.swing.JLabel();
        checkBoxSevere = new javax.swing.JCheckBox();
        labelIconWarning = new javax.swing.JLabel();
        checkBoxWarning = new javax.swing.JCheckBox();
        labelIconInfo = new javax.swing.JLabel();
        checkBoxInfo = new javax.swing.JCheckBox();
        labelIconConfig = new javax.swing.JLabel();
        checkBoxConfig = new javax.swing.JCheckBox();
        labelIconFine = new javax.swing.JLabel();
        checkBoxFine = new javax.swing.JCheckBox();
        labelIconFiner = new javax.swing.JLabel();
        checkBoxFiner = new javax.swing.JCheckBox();
        labelIconFinest = new javax.swing.JLabel();
        checkBoxFinest = new javax.swing.JCheckBox();
        panelSearchXml = new javax.swing.JPanel();
        labelSearch = new javax.swing.JLabel();
        textFieldSearch = new javax.swing.JTextField();
        scrollPaneTableLogfileRecords = new javax.swing.JScrollPane();
        tableLogfileRecords = new javax.swing.JTable();
        scrollPaneTextPaneDetails = new javax.swing.JScrollPane();
        textPaneDetails = new javax.swing.JTextPane();
        panelSimple = new javax.swing.JPanel();
        scrollPanePanelSimple = new javax.swing.JScrollPane();
        textAreaSimple = new javax.swing.JTextArea();
        panelSearchSimple = new org.jphototagger.lib.swing.TextComponentSearchPanel();
        panelSearchSimple.setSearchableTextComponent(textAreaSimple);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/lib/util/logging/Bundle"); // NOI18N
        setTitle(bundle.getString("LogfileDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        labelLogfileName.setText(bundle.getString("LogfileDialog.labelLogfileName.text")); // NOI18N
        labelLogfileName.setName("labelLogfileName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(labelLogfileName, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        buttonReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_refresh24.png"))); // NOI18N
        buttonReload.setToolTipText(bundle.getString("LogfileDialog.buttonReload.toolTipText")); // NOI18N
        buttonReload.setBorder(null);
        buttonReload.setName("buttonReload"); // NOI18N
        buttonReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReloadActionPerformed(evt);
            }
        });
        panelButtons.add(buttonReload);

        buttonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_exit24.png"))); // NOI18N
        buttonExit.setToolTipText(bundle.getString("LogfileDialog.buttonExit.toolTipText")); // NOI18N
        buttonExit.setBorder(null);
        buttonExit.setName("buttonExit"); // NOI18N
        buttonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExitActionPerformed(evt);
            }
        });
        panelButtons.add(buttonExit);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        panelCards.setName("panelCards"); // NOI18N
        panelCards.setLayout(new java.awt.CardLayout());

        panelXml.setName("panelXml"); // NOI18N
        panelXml.setLayout(new java.awt.GridBagLayout());

        panelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("LogfileDialog.panelFilter.border.title"))); // NOI18N
        panelFilter.setName("panelFilter"); // NOI18N
        panelFilter.setLayout(new java.awt.GridBagLayout());

        panelFilterCheckBoxes.setName("panelFilterCheckBoxes"); // NOI18N
        panelFilterCheckBoxes.setLayout(new java.awt.GridBagLayout());

        labelIconSevere.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_severe.png"))); // NOI18N
        labelIconSevere.setName("labelIconSevere"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelFilterCheckBoxes.add(labelIconSevere, gridBagConstraints);

        checkBoxSevere.setSelected(true);
        checkBoxSevere.setText(Level.SEVERE.getLocalizedName());
        checkBoxSevere.setName("checkBoxSevere"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelFilterCheckBoxes.add(checkBoxSevere, gridBagConstraints);

        labelIconWarning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_warning.png"))); // NOI18N
        labelIconWarning.setName("labelIconWarning"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        panelFilterCheckBoxes.add(labelIconWarning, gridBagConstraints);

        checkBoxWarning.setSelected(true);
        checkBoxWarning.setText(Level.WARNING.getLocalizedName());
        checkBoxWarning.setName("checkBoxWarning"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelFilterCheckBoxes.add(checkBoxWarning, gridBagConstraints);

        labelIconInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_info.png"))); // NOI18N
        labelIconInfo.setName("labelIconInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        panelFilterCheckBoxes.add(labelIconInfo, gridBagConstraints);

        checkBoxInfo.setSelected(true);
        checkBoxInfo.setText(Level.INFO.getLocalizedName());
        checkBoxInfo.setName("checkBoxInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelFilterCheckBoxes.add(checkBoxInfo, gridBagConstraints);

        labelIconConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_config.png"))); // NOI18N
        labelIconConfig.setName("labelIconConfig"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        panelFilterCheckBoxes.add(labelIconConfig, gridBagConstraints);

        checkBoxConfig.setSelected(true);
        checkBoxConfig.setText(Level.CONFIG.getLocalizedName());
        checkBoxConfig.setName("checkBoxConfig"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelFilterCheckBoxes.add(checkBoxConfig, gridBagConstraints);

        labelIconFine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_fine.png"))); // NOI18N
        labelIconFine.setName("labelIconFine"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelFilterCheckBoxes.add(labelIconFine, gridBagConstraints);

        checkBoxFine.setSelected(true);
        checkBoxFine.setText(Level.FINE.getLocalizedName());
        checkBoxFine.setName("checkBoxFine"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelFilterCheckBoxes.add(checkBoxFine, gridBagConstraints);

        labelIconFiner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_finer.png"))); // NOI18N
        labelIconFiner.setName("labelIconFiner"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        panelFilterCheckBoxes.add(labelIconFiner, gridBagConstraints);

        checkBoxFiner.setSelected(true);
        checkBoxFiner.setText(Level.FINER.getLocalizedName());
        checkBoxFiner.setName("checkBoxFiner"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelFilterCheckBoxes.add(checkBoxFiner, gridBagConstraints);

        labelIconFinest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_finest.png"))); // NOI18N
        labelIconFinest.setName("labelIconFinest"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        panelFilterCheckBoxes.add(labelIconFinest, gridBagConstraints);

        checkBoxFinest.setSelected(true);
        checkBoxFinest.setText(Level.FINEST.getLocalizedName());
        checkBoxFinest.setName("checkBoxFinest"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelFilterCheckBoxes.add(checkBoxFinest, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelFilter.add(panelFilterCheckBoxes, gridBagConstraints);

        panelSearchXml.setName("panelSearchXml"); // NOI18N
        panelSearchXml.setLayout(new java.awt.GridBagLayout());

        labelSearch.setText(bundle.getString("LogfileDialog.labelSearch.text")); // NOI18N
        labelSearch.setName("labelSearch"); // NOI18N
        panelSearchXml.add(labelSearch, new java.awt.GridBagConstraints());

        textFieldSearch.setName("textFieldSearch"); // NOI18N
        textFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldSearchKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        panelSearchXml.add(textFieldSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelFilter.add(panelSearchXml, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelXml.add(panelFilter, gridBagConstraints);

        scrollPaneTableLogfileRecords.setName("scrollPaneTableLogfileRecords"); // NOI18N
        scrollPaneTableLogfileRecords.setPreferredSize(new java.awt.Dimension(50, 50));
        scrollPaneTableLogfileRecords.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                scrollPaneTableLogfileRecordsComponentResized(evt);
            }
        });

        tableLogfileRecords.setAutoCreateRowSorter(true);
        tableLogfileRecords.setModel(new org.jphototagger.lib.util.logging.LogfilesTableModel("", Arrays.asList(Level.ALL)));
        tableLogfileRecords.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableLogfileRecords.setName("tableLogfileRecords"); // NOI18N
        tableLogfileRecords.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPaneTableLogfileRecords.setViewportView(tableLogfileRecords);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelXml.add(scrollPaneTableLogfileRecords, gridBagConstraints);

        scrollPaneTextPaneDetails.setName("scrollPaneTextPaneDetails"); // NOI18N
        scrollPaneTextPaneDetails.setPreferredSize(new java.awt.Dimension(50, 50));

        textPaneDetails.setEditable(false);
        textPaneDetails.setName("textPaneDetails"); // NOI18N
        scrollPaneTextPaneDetails.setViewportView(textPaneDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelXml.add(scrollPaneTextPaneDetails, gridBagConstraints);

        panelCards.add(panelXml, "panelXml");

        panelSimple.setName("panelSimple"); // NOI18N
        panelSimple.setLayout(new java.awt.GridBagLayout());

        scrollPanePanelSimple.setName("scrollPanePanelSimple"); // NOI18N

        textAreaSimple.setColumns(20);
        textAreaSimple.setRows(5);
        textAreaSimple.setName("textAreaSimple"); // NOI18N
        scrollPanePanelSimple.setViewportView(textAreaSimple);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelSimple.add(scrollPanePanelSimple, gridBagConstraints);

        panelSearchSimple.setName("panelSearchSimple"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelSimple.add(panelSearchSimple, gridBagConstraints);

        panelCards.add(panelSimple, "panelSimple");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelContent.add(panelCards, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void textFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldSearchKeyReleased
        filterTable();
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

    private void scrollPaneTableLogfileRecordsComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_scrollPaneTableLogfileRecordsComponentResized
        setColumnWidths();
    }//GEN-LAST:event_scrollPaneTableLogfileRecordsComponentResized

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                LogfileDialog dialog =
                        new LogfileDialog(new javax.swing.JFrame(), "",
                        XMLFormatter.class);

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
    private javax.swing.JLabel labelIconConfig;
    private javax.swing.JLabel labelIconFine;
    private javax.swing.JLabel labelIconFiner;
    private javax.swing.JLabel labelIconFinest;
    private javax.swing.JLabel labelIconInfo;
    private javax.swing.JLabel labelIconSevere;
    private javax.swing.JLabel labelIconWarning;
    private javax.swing.JLabel labelLogfileName;
    private javax.swing.JLabel labelSearch;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelCards;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JPanel panelFilterCheckBoxes;
    private org.jphototagger.lib.swing.TextComponentSearchPanel panelSearchSimple;
    private javax.swing.JPanel panelSearchXml;
    private javax.swing.JPanel panelSimple;
    private javax.swing.JPanel panelXml;
    private javax.swing.JScrollPane scrollPanePanelSimple;
    private javax.swing.JScrollPane scrollPaneTableLogfileRecords;
    private javax.swing.JScrollPane scrollPaneTextPaneDetails;
    private javax.swing.JTable tableLogfileRecords;
    private javax.swing.JTextArea textAreaSimple;
    private javax.swing.JTextField textFieldSearch;
    private javax.swing.JTextPane textPaneDetails;
    // End of variables declaration//GEN-END:variables
}
