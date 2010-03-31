/*
 * @(#)LogfileDialog.java    Created on 2008-10-05
 *
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

package org.jphototagger.lib.dialog;

import java.awt.HeadlessException;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.model.TableModelLogfiles;
import org.jphototagger.lib.renderer.TableCellRendererLogfileDialog;
import org.jphototagger.lib.resource.JslBundle;
import org.jphototagger.lib.util.logging.ExceptionLogfileRecord;
import org.jphototagger.lib.util.logging.FrameLogfileRecord;
import org.jphototagger.lib.util.logging.LogfileParser;
import org.jphototagger.lib.util.logging.LogfileRecord;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Frame;
import java.awt.Point;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.text.html.HTMLDocument;

/**
 * Nichtmodaler Dialog zum Anzeigen einer Logdatei geschrieben von einem
 * <code>java.util.logging.Logger</code>-Objekt. Das XML-Format muss validieren
 * gegen die <code>logger.dtd</code>.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann
 */
public final class LogfileDialog extends Dialog
        implements ListSelectionListener, ActionListener {
    private static final long           serialVersionUID  = 1L;
    public static final long            DEFAULT_MAX_BYTES = 10 * 1024 * 1024;
    private long                        maxBytes          = DEFAULT_MAX_BYTES;
    private final Map<JCheckBox, Level> levelOfCheckBox   =
        new HashMap<JCheckBox, Level>();
    private final Map<Class<?>, Integer> paneIndexOfFormatterClass =
        new HashMap<Class<?>, Integer>();
    private final List<Level>   visibleLevels = new ArrayList<Level>();
    private String              filterString;
    private List<LogfileRecord> logfileRecords;
    private Class<?>            formatterClass;
    private String              logfilename;

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
    public LogfileDialog(Frame parent, String logfilename,
                         Class<?> formatterClass) {
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

    private void postInitComponents() {
        initTextPaneDetails();
        initTableLogfileRecords();
        initLevelOfCheckbox();
        listenToCheckboxes();
    }

    private void initTextPaneDetails() {
        textPaneDetails.setStyledDocument(new HTMLDocument());
        textPaneDetails.setContentType("text/html");
    }

    private void initTableLogfileRecords() {
        tableLogfileRecords.getSelectionModel().addListSelectionListener(this);
        tableLogfileRecords.getColumnModel().getSelectionModel()
            .addListSelectionListener(this);
        tableLogfileRecords.setDefaultRenderer(Object.class,
                new TableCellRendererLogfileDialog());
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
        boolean simple =
            tabbedPane.getSelectedIndex()
            == paneIndexOfFormatterClass.get(SimpleFormatter.class);

        if (simple) {
            textAreaSimple.setText(
                FileUtil.getFileContentAsString(
                    new File(logfilename), "UTF-8"));
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
        JOptionPane.showMessageDialog(this, JslBundle.INSTANCE.getString(
                "LogfileDialog.Error.LogfileIsEmpty"),
                    JslBundle.INSTANCE.getString(
                        "LogfileDialog.Error.LogfileIsEmpty.Title"),
                 JOptionPane.ERROR_MESSAGE);
    }

    private void errorMessageMaxBytes(long logfileBytes) {
        JOptionPane.showMessageDialog(this, JslBundle.INSTANCE.getString(
                "LogfileDialog.Error.MaximumSizeExceeded",
                    Math.round((float) logfileBytes / (float) maxBytes)),
                JslBundle.INSTANCE.getString(
                    "LogfileDialog.Error.MaximumSizeExceeded.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    public long getMaxBytes() {
        return maxBytes;
    }

    /**
     * Sets the maximum bytes of the logfile size, which can be displayed.
     * <p>
     * Otherwise the dialog does not open the log file.
     *
     * @param maxBytes maximum amount of bytes.
     *                 Default: {@link #DEFAULT_MAX_BYTES}.
     */
    public void setMaxBytes(long maxBytes) {
        this.maxBytes = maxBytes;
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
        int width        = getWidth() - 20;
        int widthColumn0 = 20;
        int widthColumn1 = 150;
        int widthColumn2 = width - widthColumn0 - widthColumn1;

        if (widthColumn2 > 0) {
            tableLogfileRecords.getColumnModel().getColumn(0).setPreferredWidth(
                widthColumn0);
            tableLogfileRecords.getColumnModel().getColumn(1).setPreferredWidth(
                widthColumn1);
            tableLogfileRecords.getColumnModel().getColumn(2).setPreferredWidth(
                widthColumn2);
        }
    }

    private void showDetails() {
        TableModelLogfiles model =
            (TableModelLogfiles) tableLogfileRecords.getModel();
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
            JslBundle.INSTANCE.getString("LogfileDialog.Info.Loglevel"),
            logfileRecord.getLevel().getLocalizedName());
        addDetailTableRow(
            details,
            JslBundle.INSTANCE.getString("LogfileDialog.Info.Message"),
            logfileRecord.getMessage());
        addDetailTableRow(
            details,
            JslBundle.INSTANCE.getString("LogfileDialog.Info.LoggerClass"),
            logfileRecord.getLogger());
        addDetailTableRow(
            details, JslBundle.INSTANCE.getString("LogfileDialog.Info.Class"),
            logfileRecord.getClassname());
        addDetailTableRow(
            details, JslBundle.INSTANCE.getString("LogfileDialog.Info.Method"),
            logfileRecord.getMethodname());
        addDetailTableRow(
            details, JslBundle.INSTANCE.getString("LogfileDialog.Info.Thread"),
            logfileRecord.getThread());
        details.append("\n</table>");
        addDetailException(details, logfileRecord.getException());
        details.append("\n</html>");
        textPaneDetails.setText(details.toString());
    }

    private void addDetailTableRow(StringBuffer stringBuffer, String rowHeader,
                                   String rowData) {
        if (rowData != null) {
            stringBuffer.append("\n\t<tr>");
            stringBuffer.append("\n\t\t<td>");
            stringBuffer.append("<strong>" + rowHeader + "</strong>");
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
                stringBuffer.append("\n" + frame.getClassName() + ":");
                stringBuffer.append(" " + frame.getMethodName());
                stringBuffer.append(
                    JslBundle.INSTANCE.getString(
                        "LogfileDialog.Info.StartLineNumber") + frame.getLine()
                            + ")");
            }

            stringBuffer.append("\n</pre>");
        }
    }

    private void addDetailExceptionMessage(ExceptionLogfileRecord exception,
            StringBuffer stringBuffer) {
        String message = exception.getMessage();

        if (message != null) {
            stringBuffer.append("\n<br /><font color=\"ff0000\">" + message
                                + "</font>");
        }
    }

    private void setTable() {
        if ((logfilename != null) &&!logfilename.isEmpty()) {
            TableModelLogfiles model = new TableModelLogfiles(filterString,
                                           visibleLevels);

            tableLogfileRecords.setModel(model);
            model.setRecords(logfileRecords);
            setColumnWidths();
            scrollPaneTableLogfileRecords.getViewport().setViewPosition(
                new Point(0, 0));
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
        if (visible && checkFileSize()) {
            boolean simpleFormatter = formatterClass.equals(SimpleFormatter.class);
            boolean xmlFormatter    = formatterClass.equals(XMLFormatter.class);

            if (xmlFormatter) {
                readXml();
            } else if (simpleFormatter) {
                readSimple();
            } else {
                errorMessageNotSupportedFormat();
                readSimple();
            }
            panelSearch.setEnabled(simpleFormatter);
        }
        super.setVisible(visible);
    }

    private void errorMessageNotSupportedFormat() {
        JOptionPane
            .showMessageDialog(this, JslBundle.INSTANCE
                .getString("LogfileDialog.Error.UnknownLogfileFormat"), JslBundle
                .INSTANCE
                .getString("LogfileDialog.Error.UnknownLogfileFormat.Title"), JOptionPane
                .ERROR_MESSAGE);
    }

    private void readSimple() {
        selectPane();
        textAreaSimple.setText(
            FileUtil.getFileContentAsString(new File(logfilename), "UTF-8"));
    }

    private void readXml() {
        selectPane();
        filterString = textFieldSearch.getText();
        readLogfileRecords();
        resetVisibeLevels();
        textFieldSearch.requestFocus();
    }

    private void selectPane() {
        Set<Class<?>> classes = paneIndexOfFormatterClass.keySet();

        for (Class<?> c : classes) {
            int     panelIndex         = paneIndexOfFormatterClass.get(c);
            boolean isCurrentFormatter = c.equals(formatterClass);

            if (isCurrentFormatter) {
                tabbedPane.setSelectedIndex(panelIndex);
            }

            tabbedPane.setEnabledAt(panelIndex, isCurrentFormatter);
        }
    }

    /**
     * This method is called from within the constructor to
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
        panelSearch = new javax.swing.JPanel();
        labelSearch = new javax.swing.JLabel();
        textFieldSearch = new javax.swing.JTextField();
        scrollPaneTableLogfileRecords = new javax.swing.JScrollPane();
        tableLogfileRecords = new javax.swing.JTable();
        scrollPaneTextPaneDetails = new javax.swing.JScrollPane();
        textPaneDetails = new javax.swing.JTextPane();
        panelSimple = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaSimple = new javax.swing.JTextArea();
        searchPanel = new org.jphototagger.lib.component.TextAreaSearchPanel();
        searchPanel.setTextArea(textAreaSimple);
        buttonReload = new javax.swing.JButton();
        buttonExit = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/lib/resource/properties/Bundle"); // NOI18N
        setTitle(bundle.getString("LogfileDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("LogfileDialog.panelFilter.border.title"))); // NOI18N

        labelIconSevere.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_severe.png"))); // NOI18N

        checkBoxSevere.setSelected(true);
        checkBoxSevere.setText(Level.SEVERE.getLocalizedName());

        labelIconWarning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_warning.png"))); // NOI18N

        checkBoxWarning.setSelected(true);
        checkBoxWarning.setText(Level.WARNING.getLocalizedName());

        labelIconInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_info.png"))); // NOI18N

        checkBoxInfo.setSelected(true);
        checkBoxInfo.setText(Level.INFO.getLocalizedName());

        labelIconConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_config.png"))); // NOI18N

        checkBoxConfig.setSelected(true);
        checkBoxConfig.setText(Level.CONFIG.getLocalizedName());

        labelIconFine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_fine.png"))); // NOI18N

        checkBoxFine.setSelected(true);
        checkBoxFine.setText(Level.FINE.getLocalizedName());

        labelIconFiner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_finer.png"))); // NOI18N

        checkBoxFiner.setSelected(true);
        checkBoxFiner.setText(Level.FINER.getLocalizedName());

        labelIconFinest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_logfiledialog_finest.png"))); // NOI18N

        checkBoxFinest.setSelected(true);
        checkBoxFinest.setText(Level.FINEST.getLocalizedName());

        javax.swing.GroupLayout panelFilterCheckBoxesLayout = new javax.swing.GroupLayout(panelFilterCheckBoxes);
        panelFilterCheckBoxes.setLayout(panelFilterCheckBoxesLayout);
        panelFilterCheckBoxesLayout.setHorizontalGroup(
            panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelIconSevere)
                    .addComponent(labelIconFine))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxSevere)
                    .addComponent(checkBoxFine))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelIconWarning)
                    .addComponent(labelIconFiner))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxWarning)
                    .addComponent(checkBoxFiner))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelIconInfo)
                    .addComponent(labelIconFinest))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxInfo)
                    .addComponent(checkBoxFinest))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelIconConfig)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxConfig))
        );

        panelFilterCheckBoxesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {labelIconConfig, labelIconFine, labelIconFiner, labelIconFinest, labelIconInfo, labelIconSevere, labelIconWarning});

        panelFilterCheckBoxesLayout.setVerticalGroup(
            panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(checkBoxConfig)
                    .addComponent(labelIconConfig)
                    .addComponent(checkBoxInfo)
                    .addComponent(labelIconInfo)
                    .addComponent(checkBoxWarning)
                    .addComponent(labelIconWarning)
                    .addComponent(checkBoxSevere)
                    .addComponent(labelIconSevere))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFilterCheckBoxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(checkBoxFinest)
                    .addComponent(labelIconFinest)
                    .addComponent(checkBoxFiner)
                    .addComponent(labelIconFiner)
                    .addComponent(checkBoxFine)))
            .addGroup(panelFilterCheckBoxesLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(labelIconFine))
        );

        panelFilterCheckBoxesLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelIconConfig, labelIconFine, labelIconFiner, labelIconFinest, labelIconInfo, labelIconSevere, labelIconWarning});

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
                .addComponent(textFieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE))
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
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelSearch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelFilterCheckBoxes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addComponent(panelFilterCheckBoxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tableLogfileRecords.setAutoCreateRowSorter(true);
        tableLogfileRecords.setModel(new TableModelLogfiles("", Arrays.asList(Level.ALL)));
        tableLogfileRecords.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableLogfileRecords.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPaneTableLogfileRecords.setViewportView(tableLogfileRecords);

        textPaneDetails.setEditable(false);
        scrollPaneTextPaneDetails.setViewportView(textPaneDetails);

        javax.swing.GroupLayout panelXmlLayout = new javax.swing.GroupLayout(panelXml);
        panelXml.setLayout(panelXmlLayout);
        panelXmlLayout.setHorizontalGroup(
            panelXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelXmlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneTableLogfileRecords, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                    .addComponent(panelFilter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPaneTextPaneDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelXmlLayout.setVerticalGroup(
            panelXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelXmlLayout.createSequentialGroup()
                .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneTableLogfileRecords, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneTextPaneDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("LogfileDialog.panelXml.TabConstraints.tabTitle"), panelXml); // NOI18N

        textAreaSimple.setColumns(20);
        textAreaSimple.setRows(5);
        jScrollPane1.setViewportView(textAreaSimple);

        javax.swing.GroupLayout panelSimpleLayout = new javax.swing.GroupLayout(panelSimple);
        panelSimple.setLayout(panelSimpleLayout);
        panelSimpleLayout.setHorizontalGroup(
            panelSimpleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSimpleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelSimpleLayout.setVerticalGroup(
            panelSimpleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSimpleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("LogfileDialog.panelSimple.TabConstraints.tabTitle"), panelSimple); // NOI18N

        buttonReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_refresh24.png"))); // NOI18N
        buttonReload.setToolTipText(bundle.getString("LogfileDialog.buttonReload.toolTipText")); // NOI18N
        buttonReload.setBorder(null);
        buttonReload.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonReload.setPreferredSize(new java.awt.Dimension(24, 24));
        buttonReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReloadActionPerformed(evt);
            }
        });

        buttonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_exit24.png"))); // NOI18N
        buttonExit.setToolTipText(bundle.getString("LogfileDialog.buttonExit.toolTipText")); // NOI18N
        buttonExit.setBorder(null);
        buttonExit.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonExit.setPreferredSize(new java.awt.Dimension(24, 24));
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
                    .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addComponent(buttonReload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonReload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JScrollPane jScrollPane1;
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
    private javax.swing.JScrollPane scrollPaneTableLogfileRecords;
    private javax.swing.JScrollPane scrollPaneTextPaneDetails;
    private org.jphototagger.lib.component.TextAreaSearchPanel searchPanel;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tableLogfileRecords;
    private javax.swing.JTextArea textAreaSimple;
    private javax.swing.JTextField textFieldSearch;
    private javax.swing.JTextPane textPaneDetails;
    // End of variables declaration//GEN-END:variables
}
