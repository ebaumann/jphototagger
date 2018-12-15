package org.jphototagger.lib.util.logging;

import java.awt.CardLayout;
import java.awt.Frame;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.text.html.HTMLDocument;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * Non modal dialog to display a logfile written by a
 * <code>java.util.logging.Logger</code>.
 * The XML format has to validate against <code>logger.dtd</code>.
 *
 * @author Elmar Baumann
 */
public final class LogfileDialog extends DialogExt implements ListSelectionListener, ActionListener {

    private static final long serialVersionUID  = 1L;
    public static final long  DEFAULT_MAX_BYTES = 10 * 1024 * 1024;
    private static final float MEGABYTE_IN_BYTES = 1024 * 1024;
    private long maxBytes = DEFAULT_MAX_BYTES;
    private final Map<JCheckBox, Level> levelOfCheckBox = new HashMap<>();
    private final Map<JCheckBox, JLabel> iconLabelOfCheckBox = new HashMap<>();
    private final Map<Class<?>, Integer> paneIndexOfFormatterClass = new HashMap<>();
    private final List<Level> visibleLevels = new ArrayList<>();
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
        this.logfilename = logfilename;
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
        AnnotationProcessor.process(this);
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
        File logfile = new File(logfilename);
        long logfileBytes = logfile.length();
        if (logfileBytes <= 0) {
            errorMessageEmpty();
            return false;
        } else if (logfileBytes >= maxBytes) {
            errorMessageMaxBytes();
            return false;
        }
        return true;
    }

    private void errorMessageEmpty() {
        String message = Bundle.getString(LogfileDialog.class, "LogfileDialog.Error.LogfileIsEmpty", logfilename);
        String title = Bundle.getString(LogfileDialog.class, "LogfileDialog.Error.LogfileIsEmpty.Title");
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void errorMessageMaxBytes() {
        File logfile = new File(logfilename);
        long logfileBytes = logfile.length();
        int logfileSizeInMegabytes = Math.round((float) logfileBytes / MEGABYTE_IN_BYTES);
        int maxSizeInMeagbytes = Math.round((float) maxBytes / MEGABYTE_IN_BYTES);
        String message = Bundle.getString(LogfileDialog.class, "LogfileDialog.Error.MaximumSizeExceeded",
                logfileSizeInMegabytes, maxSizeInMeagbytes);
        if (MessageDisplayer.confirmYesNo(this, message)) {
            DesktopUtil.open(logfile, "LogfileDialog.OpenLogfile");
        }
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
        StringBuilder sb = new StringBuilder(1000);
        sb.append("<html>")
          .append("\n<table>");
        addDetailTableRow(
            sb,
            Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.Loglevel"),
            logfileRecord.getLevel().getLocalizedName());
        addDetailTableRow(
            sb,
            Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.Message"),
            logfileRecord.getMessage());
        addDetailTableRow(
            sb,
            Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.LoggerClass"),
            logfileRecord.getLogger());
        addDetailTableRow(
            sb, Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.Class"),
            logfileRecord.getClassname());
        addDetailTableRow(
            sb, Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.Method"),
            logfileRecord.getMethodname());
        addDetailTableRow(
            sb, Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.Thread"),
            logfileRecord.getThread());
        sb.append("\n</table>");
        addDetailException(sb, logfileRecord.getException());
        sb.append("\n</html>");
        textPaneDetails.setText(sb.toString());
        textPaneDetails.moveCaretPosition(0);
    }

    private void addDetailTableRow(StringBuilder sb, String rowHeader, String rowData) {
        if (rowData != null) {
            sb.append("\n\t<tr>")
              .append("\n\t\t<td>")
              .append("<strong>")
              .append(rowHeader)
              .append("</strong>")
              .append("</td>")
              .append("<td><font color=\"#5555aa\">")
              .append(rowData)
              .append("</font></td>")
              .append("\n\t</tr>");
        }
    }

    private void addDetailException(StringBuilder sb, ExceptionLogfileRecord ex) {
        if (ex != null) {
            addDetailExceptionMessage(ex, sb);
            sb.append("\n<pre>");
            List<FrameLogfileRecord> frames = ex.getFrames();
            for (FrameLogfileRecord frame : frames) {
                sb
                  .append("\n")
                  .append(frame.getClassName())
                  .append(":")
                  .append(" ")
                  .append(frame.getMethodName())
                  .append(Bundle.getString(LogfileDialog.class, "LogfileDialog.Info.StartLineNumber"))
                  .append(frame.getLine())
                  .append(")");
            }
            sb.append("\n</pre>");
        }
    }

    private void addDetailExceptionMessage(ExceptionLogfileRecord exception, StringBuilder sb) {
        String message = exception.getMessage();
        if (message != null) {
            sb
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
        if (visible && !checkFileSize()) {
            return;
        }
        if (visible) {
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

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = org.jphototagger.resources.UiFactory.panel();
        labelLogfileName = org.jphototagger.resources.UiFactory.label();
        panelButtons = org.jphototagger.resources.UiFactory.panel();
        buttonReload = org.jphototagger.resources.UiFactory.button();
        buttonExit = org.jphototagger.resources.UiFactory.button();
        panelCards = org.jphototagger.resources.UiFactory.panel();
        panelXml = org.jphototagger.resources.UiFactory.panel();
        panelFilter = org.jphototagger.resources.UiFactory.panel();
        panelFilterCheckBoxes = org.jphototagger.resources.UiFactory.panel();
        labelIconSevere = org.jphototagger.resources.UiFactory.label();
        checkBoxSevere = org.jphototagger.resources.UiFactory.checkBox();
        labelIconWarning = org.jphototagger.resources.UiFactory.label();
        checkBoxWarning = org.jphototagger.resources.UiFactory.checkBox();
        labelIconInfo = org.jphototagger.resources.UiFactory.label();
        checkBoxInfo = org.jphototagger.resources.UiFactory.checkBox();
        labelIconConfig = org.jphototagger.resources.UiFactory.label();
        checkBoxConfig = org.jphototagger.resources.UiFactory.checkBox();
        labelIconFine = org.jphototagger.resources.UiFactory.label();
        checkBoxFine = org.jphototagger.resources.UiFactory.checkBox();
        labelIconFiner = org.jphototagger.resources.UiFactory.label();
        checkBoxFiner = org.jphototagger.resources.UiFactory.checkBox();
        labelIconFinest = org.jphototagger.resources.UiFactory.label();
        checkBoxFinest = org.jphototagger.resources.UiFactory.checkBox();
        panelSearchXml = org.jphototagger.resources.UiFactory.panel();
        labelSearch = org.jphototagger.resources.UiFactory.label();
        textFieldSearch = org.jphototagger.resources.UiFactory.textField();
        scrollPaneTableLogfileRecords = org.jphototagger.resources.UiFactory.scrollPane();
        tableLogfileRecords = org.jphototagger.resources.UiFactory.table();
        scrollPaneTextPaneDetails = org.jphototagger.resources.UiFactory.scrollPane();
        textPaneDetails = org.jphototagger.resources.UiFactory.textPane();
        panelSimple = org.jphototagger.resources.UiFactory.panel();
        scrollPanePanelSimple = org.jphototagger.resources.UiFactory.scrollPane();
        textAreaSimple = org.jphototagger.resources.UiFactory.textArea();
        panelSearchSimple = new org.jphototagger.lib.swing.TextComponentSearchPanel();
        panelSearchSimple.setSearchableTextComponent(textAreaSimple);

        setTitle(Bundle.getString(getClass(), "LogfileDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        labelLogfileName.setText(Bundle.getString(getClass(), "LogfileDialog.labelLogfileName.text")); // NOI18N
        labelLogfileName.setName("labelLogfileName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(labelLogfileName, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridLayout(1, 0, UiFactory.scale(10), 0));

        buttonReload.setIcon(org.jphototagger.resources.Icons.getIcon("icon_refresh.png")); // NOI18N
        buttonReload.setToolTipText(Bundle.getString(getClass(), "LogfileDialog.buttonReload.toolTipText")); // NOI18N
        buttonReload.setBorder(null);
        buttonReload.setName("buttonReload"); // NOI18N
        buttonReload.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReloadActionPerformed(evt);
            }
        });
        panelButtons.add(buttonReload);

        buttonExit.setIcon(org.jphototagger.resources.Icons.getIcon("icon_exit.png")); // NOI18N
        buttonExit.setToolTipText(Bundle.getString(getClass(), "LogfileDialog.buttonExit.toolTipText")); // NOI18N
        buttonExit.setBorder(null);
        buttonExit.setName("buttonExit"); // NOI18N
        buttonExit.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExitActionPerformed(evt);
            }
        });
        panelButtons.add(buttonExit);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 10, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        panelCards.setName("panelCards"); // NOI18N
        panelCards.setLayout(new java.awt.CardLayout());

        panelXml.setName("panelXml"); // NOI18N
        panelXml.setLayout(new java.awt.GridBagLayout());

        panelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "LogfileDialog.panelFilter.border.title"))); // NOI18N
        panelFilter.setName("panelFilter"); // NOI18N
        panelFilter.setLayout(new java.awt.GridBagLayout());

        panelFilterCheckBoxes.setName("panelFilterCheckBoxes"); // NOI18N
        panelFilterCheckBoxes.setLayout(new java.awt.GridBagLayout());

        labelIconSevere.setIcon(org.jphototagger.resources.Icons.getIcon("icon_logfiledialog_severe.png")); // NOI18N
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

        labelIconWarning.setIcon(org.jphototagger.resources.Icons.getIcon("icon_logfiledialog_warning.png")); // NOI18N
        labelIconWarning.setName("labelIconWarning"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 3, 0, 0);
        panelFilterCheckBoxes.add(labelIconWarning, gridBagConstraints);

        checkBoxWarning.setSelected(true);
        checkBoxWarning.setText(Level.WARNING.getLocalizedName());
        checkBoxWarning.setName("checkBoxWarning"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelFilterCheckBoxes.add(checkBoxWarning, gridBagConstraints);

        labelIconInfo.setIcon(org.jphototagger.resources.Icons.getIcon("icon_logfiledialog_info.png")); // NOI18N
        labelIconInfo.setName("labelIconInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 3, 0, 0);
        panelFilterCheckBoxes.add(labelIconInfo, gridBagConstraints);

        checkBoxInfo.setSelected(true);
        checkBoxInfo.setText(Level.INFO.getLocalizedName());
        checkBoxInfo.setName("checkBoxInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelFilterCheckBoxes.add(checkBoxInfo, gridBagConstraints);

        labelIconConfig.setIcon(org.jphototagger.resources.Icons.getIcon("icon_logfiledialog_config.png")); // NOI18N
        labelIconConfig.setName("labelIconConfig"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 3, 0, 0);
        panelFilterCheckBoxes.add(labelIconConfig, gridBagConstraints);

        checkBoxConfig.setSelected(true);
        checkBoxConfig.setText(Level.CONFIG.getLocalizedName());
        checkBoxConfig.setName("checkBoxConfig"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelFilterCheckBoxes.add(checkBoxConfig, gridBagConstraints);

        labelIconFine.setIcon(org.jphototagger.resources.Icons.getIcon("icon_logfiledialog_fine.png")); // NOI18N
        labelIconFine.setName("labelIconFine"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
        panelFilterCheckBoxes.add(labelIconFine, gridBagConstraints);

        checkBoxFine.setSelected(true);
        checkBoxFine.setText(Level.FINE.getLocalizedName());
        checkBoxFine.setName("checkBoxFine"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
        panelFilterCheckBoxes.add(checkBoxFine, gridBagConstraints);

        labelIconFiner.setIcon(org.jphototagger.resources.Icons.getIcon("icon_logfiledialog_finer.png")); // NOI18N
        labelIconFiner.setName("labelIconFiner"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 3, 0, 0);
        panelFilterCheckBoxes.add(labelIconFiner, gridBagConstraints);

        checkBoxFiner.setSelected(true);
        checkBoxFiner.setText(Level.FINER.getLocalizedName());
        checkBoxFiner.setName("checkBoxFiner"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
        panelFilterCheckBoxes.add(checkBoxFiner, gridBagConstraints);

        labelIconFinest.setIcon(org.jphototagger.resources.Icons.getIcon("icon_logfiledialog_finest.png")); // NOI18N
        labelIconFinest.setName("labelIconFinest"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 3, 0, 0);
        panelFilterCheckBoxes.add(labelIconFinest, gridBagConstraints);

        checkBoxFinest.setSelected(true);
        checkBoxFinest.setText(Level.FINEST.getLocalizedName());
        checkBoxFinest.setName("checkBoxFinest"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
        panelFilterCheckBoxes.add(checkBoxFinest, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelFilter.add(panelFilterCheckBoxes, gridBagConstraints);

        panelSearchXml.setName("panelSearchXml"); // NOI18N
        panelSearchXml.setLayout(new java.awt.GridBagLayout());

        labelSearch.setText(Bundle.getString(getClass(), "LogfileDialog.labelSearch.text")); // NOI18N
        labelSearch.setName("labelSearch"); // NOI18N
        panelSearchXml.add(labelSearch, new java.awt.GridBagConstraints());

        textFieldSearch.setName("textFieldSearch"); // NOI18N
        textFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldSearchKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 3, 0, 0);
        panelSearchXml.add(textFieldSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 5, 5);
        panelFilter.add(panelSearchXml, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelXml.add(panelFilter, gridBagConstraints);

        scrollPaneTableLogfileRecords.setName("scrollPaneTableLogfileRecords"); // NOI18N
        scrollPaneTableLogfileRecords.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(50, 50));
        scrollPaneTableLogfileRecords.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
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
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
        panelXml.add(scrollPaneTableLogfileRecords, gridBagConstraints);

        scrollPaneTextPaneDetails.setName("scrollPaneTextPaneDetails"); // NOI18N
        scrollPaneTextPaneDetails.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(50, 50));

        textPaneDetails.setEditable(false);
        textPaneDetails.setName("textPaneDetails"); // NOI18N
        scrollPaneTextPaneDetails.setViewportView(textPaneDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
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
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelSimple.add(panelSearchSimple, gridBagConstraints);

        panelCards.add(panelSimple, "panelSimple");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(panelCards, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void textFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldSearchKeyReleased
        filterTable();
    }

    private void buttonReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReloadActionPerformed
        reload();
    }

    private void buttonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExitActionPerformed
        setVisible(false);
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
    }

    private void scrollPaneTableLogfileRecordsComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_scrollPaneTableLogfileRecordsComponentResized
        setColumnWidths();
    }

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
}
