package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.model.ComboBoxModelAppLookAndFeel;
import de.elmar_baumann.imv.model.ComboBoxModelLogfileFormatter;
import de.elmar_baumann.imv.model.ListModelAutoscanDirectories;
import de.elmar_baumann.lib.component.CheckList;
import de.elmar_baumann.imv.model.ListModelFastSearchColumns;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.renderer.ListCellRendererLogfileFormatter;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.persistence.PersistentSettingsHints;
import de.elmar_baumann.lib.renderer.ListCellRendererFileSystem;
import de.elmar_baumann.lib.util.ArrayUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

/**
 * Modaler Dialog für Anwendungseinstellungen.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class UserSettingsDialog extends javax.swing.JDialog
    implements ActionListener {

    private Database db = Database.getInstance();
    private final String keySearchColumns = "UserSettingsDialog.SearchColumns"; // NOI18N
    private final String delimiterSearchColumns = "\t"; // NOI18N
    private final String keyImageOpenApp = "UserSettingsDialog.ImageOpenApp"; // NOI18N
    private final String keyLastSelectedAutoscanDirectory = "UserSettingsDialog.keyLastSelectedAutoscanDirectory"; // NOI18N
    private List<UserSettingsChangeListener> changeListener = new ArrayList<UserSettingsChangeListener>();
    public CheckList checkListSearchColumns = new CheckList();
    public ListModelFastSearchColumns searchColumnsListModel = new ListModelFastSearchColumns();
    private HashMap<Tab, Integer> indexOfTab = new HashMap<Tab, Integer>();
    private HashMap<Integer, Tab> tabOfIndex = new HashMap<Integer, Tab>();
    private DefaultListModel modelImageOpenApps = new DefaultListModel();
    private ListModelAutoscanDirectories modelAutoscanDirectories = new ListModelAutoscanDirectories();
    private String lastSelectedAutoscanDirectory = ""; // NOI18N
    private String previousDirectory = ""; // NOI18N
    private ComboBoxModelAppLookAndFeel modelLookAndFeel = new ComboBoxModelAppLookAndFeel();
    private static UserSettingsDialog instance = new UserSettingsDialog();

    private void initHashMaps() {
        // TODO PERMANENT: Bei neuen Tabs ergänzen
        indexOfTab.put(Tab.Programs, 0);
        indexOfTab.put(Tab.FastSearch, 1);
        indexOfTab.put(Tab.Thumbnails, 2);
        indexOfTab.put(Tab.Iptc, 3);
        indexOfTab.put(Tab.Tasks, 4);
        indexOfTab.put(Tab.Performance, 5);
        indexOfTab.put(Tab.FileExcludePatterns, 6);
        indexOfTab.put(Tab.Other, 7);

        for (Tab tab : indexOfTab.keySet()) {
            tabOfIndex.put(indexOfTab.get(tab), tab);
        }
    }

    private void writePersistentComoBoxSkinsIndex() {
        PersistentSettings.getInstance().setString(
            Integer.toString(comboBoxAppLookAndFeel.getSelectedIndex()),
            ComboBoxModelAppLookAndFeel.keySelectedIndex);
    }

    /**
     * Ein Tab mit bestimmten Einstellungen.
     */
    public enum Tab {

        /** Programme zum Öffnen von Bildern */
        Programs,
        /** Schnellsuche */
        FastSearch,
        /** Thumbnails */
        Thumbnails,
        /** IPTC */
        Iptc,
        /** Geplante Tasks */
        Tasks,
        /** Geschwindigkeit */
        Performance,
        /**
         * File exclude patterns
         */
        FileExcludePatterns,
        /** Sonstiges */
        Other
    };

    private void moveDownOpenImageApp() {
        if (canMoveDownOpenImageApp()) {
            int selectedIndex = listOpenImageApps.getSelectedIndex();
            int newSelectedIndex = selectedIndex + 1;
            Object element = modelImageOpenApps.get(selectedIndex);
            modelImageOpenApps.remove(selectedIndex);
            modelImageOpenApps.add(newSelectedIndex, element);
            listOpenImageApps.setSelectedIndex(newSelectedIndex);
            setEnabled();
        }
    }

    public List<String> getFileExcludePatterns() {
        return panelFileExcludePatterns.getFileExcludePatterns();
    }

    /**
     * Fügt einen Änderungsbeobachter hinzu.
     * 
     * @param listener Beobachter
     */
    public void addChangeListener(UserSettingsChangeListener listener) {
        changeListener.add(listener);
    }

    /**
     * Entfernt einen Änderungsbeobachter.
     * 
     * @param listener Beobachter
     */
    public void removeChagneListener(UserSettingsChangeListener listener) {
        changeListener.remove(listener);
    }

    private void notifyChangeListener(UserSettingsChangeEvent evt) {
        for (UserSettingsChangeListener listener : changeListener) {
            listener.applySettings(evt);
        }
    }

    private boolean canMoveDownOpenImageApp() {
        int selectedIndex = listOpenImageApps.getSelectedIndex();
        int lastIndex = modelImageOpenApps.getSize() - 1;
        return selectedIndex >= 0 && selectedIndex < lastIndex;
    }

    private void moveUpOpenImageApp() {
        if (canMoveUpOpenImageApp()) {
            int selectedIndex = listOpenImageApps.getSelectedIndex();
            int newSelectedIndex = selectedIndex - 1;
            Object element = modelImageOpenApps.get(selectedIndex);
            modelImageOpenApps.remove(selectedIndex);
            modelImageOpenApps.add(newSelectedIndex, element);
            listOpenImageApps.setSelectedIndex(newSelectedIndex);
            setEnabled();
        }
    }

    private boolean canMoveUpOpenImageApp() {
        return listOpenImageApps.getSelectedIndex() > 0;
    }

    private void setEnabled() {
        textFieldExternalThumbnailApp.setEnabled(checkBoxExternalThumbnailApp.isSelected());
        buttonOpenImageAppMoveDown.setEnabled(canMoveDownOpenImageApp());
        buttonOpenImageAppMoveUp.setEnabled(canMoveUpOpenImageApp());
        buttonRemoveOtherOpenImageApp.setEnabled(isOtherOpenImageAppSelected());
        buttonTasksAutoscanRemoveDirectories.setEnabled(listTasksAutoscanDirectories.getSelectedIndex() >= 0);
    }

    private boolean isOtherOpenImageAppSelected() {
        return listOpenImageApps.getSelectedIndex() >= 0;
    }

    public static UserSettingsDialog getInstance() {
        return instance;
    }

    private UserSettingsDialog() {
        super((java.awt.Frame) null, false);
        initHashMaps();
        setSearchColumns();
        initComponents();
        setIconImages(AppSettings.getAppIcons());
        readPersistent();
        setEnabled();
    }

    /**
     * Wählt einen Tab aus.
     * 
     * @param tab Tab
     */
    public void selectTab(Tab tab) {
        tabbedPane.setSelectedIndex(indexOfTab.get(tab));
    }

    public Tab getSelectedTab() {
        return tabOfIndex.get(tabbedPane.getSelectedIndex());
    }

    private void checkLogLevel() {
        if (comboBoxLogLevel.getSelectedIndex() < 0) {
            comboBoxLogLevel.setSelectedIndex(0);
        }
    }

    private void enableExternalAppSettings() {
        textFieldExternalThumbnailApp.setEnabled(
            checkBoxExternalThumbnailApp.isSelected());
    }

    private void enableMaxThumbnailWidth() {
        spinnerMaxThumbnailSize.setEnabled(!checkBoxUseEmbeddedThumbnails.isSelected());
    }

    private void setSearchColumns() {
        checkListSearchColumns.setModel(searchColumnsListModel);
        checkListSearchColumns.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        checkListSearchColumns.addActionListener(this);
    }

    private void readPersistent() {
        PersistentAppSizes.getSizeAndLocation(this);
        readPersistentContent();
        previousDirectory = labelImageOpenApp.getText();
        lastSelectedAutoscanDirectory = PersistentSettings.getInstance().getString(keyLastSelectedAutoscanDirectory);
    }

    private void readPersistentContent() {
        PersistentSettings settings = PersistentSettings.getInstance();
        settings.getComponent(this, getPersistentSettingsHints());
        checkListSearchColumns.setSelectedItemsWithText(getTextSelectedSearchColumns(), true);
        labelImageOpenApp.setText(settings.getString(keyImageOpenApp));
        checkLogLevel();
        enableMaxThumbnailWidth();
        enableExternalAppSettings();
    }

    private List<String> getTextSelectedSearchColumns() {
        List<String> text = new ArrayList<String>();
        List<Column> columns = getPersistentWrittenTableColumns();
        for (Column column : columns) {
            text.add(column.getDescription());
        }
        return text;
    }

    private List<Column> getPersistentWrittenTableColumns() {
        List<Column> columns = new ArrayList<Column>();
        List<String> columnKeys = ArrayUtil.stringTokenToList(PersistentSettings.getInstance().
            getString(keySearchColumns), delimiterSearchColumns);
        for (String key : columnKeys) {
            try {
                Class cl = Class.forName(key);
                @SuppressWarnings("unchecked")
                Method method = cl.getMethod("getInstance", new Class[0]); // NOI18N
                Object o = method.invoke(null, new Object[0]);
                if (o instanceof Column) {
                    columns.add((Column) o);
                }
            } catch (Exception ex) {
                Logger.getLogger(UserSettingsDialog.class.getName()).log(Level.WARNING, ex.getMessage());
            }
        }
        return columns;
    }

    private void setDefaultOpenApp() {
        File file = chooseFile(labelImageOpenApp.getText());
        if (file != null) {
            labelImageOpenApp.setText(file.getAbsolutePath());
            PersistentSettings.getInstance().setString(
                file.getAbsolutePath(), keyImageOpenApp);
            notifyChangeListener(new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Changed.defaultOpenImageApp));
        }
    }

    private void addOtherOpenImageApp() {
        File file = chooseFile(previousDirectory);
        if (file != null) {
            String filename = file.getAbsolutePath();
            if (!modelImageOpenApps.contains(filename)) {
                modelImageOpenApps.addElement(filename);
                setEnabled();
                notifyChangeListener(new UserSettingsChangeEvent(
                    UserSettingsChangeEvent.Changed.otherOpenImageApps));
            }
        }
    }

    private void removeOtherOpenImageApp() {
        int index = listOpenImageApps.getSelectedIndex();
        if (index >= 0 && askRemove(modelImageOpenApps.getElementAt(index).toString())) {
            modelImageOpenApps.remove(index);
            listOpenImageApps.setSelectedIndex(index);
            setEnabled();
            notifyChangeListener(new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Changed.otherOpenImageApps));
        }
    }

    private boolean askRemove(String otherImageOpenApp) {
        MessageFormat msg = new MessageFormat(Bundle.getString("UserSettingsDialog.ConfirmMessage.RemoveImageOpenApp"));
        return JOptionPane.showConfirmDialog(
            this,
            msg.format(new Object[]{otherImageOpenApp}),
            Bundle.getString("UserSettingsDialog.ConfirmMessage.RemoveImageOpenApp.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private File chooseFile(String startDirectory) {
        JFileChooser fileChooser = new JFileChooser(new File(startDirectory));
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.isDirectory()) {
                return file;
            }
        }
        return null;
    }

    private void writePersistent() {
        PersistentSettings settings = PersistentSettings.getInstance();
        settings.setString(getSearchColumnKeys(), keySearchColumns);
        settings.setComponent(this, getPersistentSettingsHints());
        settings.setString(lastSelectedAutoscanDirectory, keyLastSelectedAutoscanDirectory);
        PersistentAppSizes.setSizeAndLocation(this);
    }

    private PersistentSettingsHints getPersistentSettingsHints() {
        PersistentSettingsHints hints = new PersistentSettingsHints();
        hints.setListContent(true);
        hints.addExcludedMember(getClass().getName() + ".listAutoscanDirectories"); // NOI18N
        hints.addExcludedMember("de.elmar_baumann.imv.view.panels.FileExcludePatternsPanel.listPattern"); // NOI18N
        hints.addExcludedMember("de.elmar_baumann.imv.view.panels.FileExcludePatternsPanel.textFieldInputPattern"); // NOI18N
        return hints;
    }

    private String getSearchColumnKeys() {
        StringBuffer tableColumns = new StringBuffer();
        List<Integer> indices =
            checkListSearchColumns.getSelectedItemIndices();
        for (Integer index : indices) {
            tableColumns.append(searchColumnsListModel.getTableColumnAtIndex(
                index).getKey() + delimiterSearchColumns);
        }
        return tableColumns.toString();
    }

    private void addAutoscanDirectories() {
        DirectoryChooser dialog = new DirectoryChooser(null, UserSettings.getInstance().isAcceptHiddenDirectories());
        dialog.setStartDirectory(new File(lastSelectedAutoscanDirectory));
        dialog.setMultiSelection(true);
        dialog.setVisible(true);
        if (dialog.accepted()) {
            List<File> directories = dialog.getSelectedDirectories();
            for (File directory : directories) {
                if (!modelAutoscanDirectories.contains(directory)) {
                    String directoryName = directory.getAbsolutePath();
                    lastSelectedAutoscanDirectory = directoryName;
                    if (!db.existsAutoscanDirectory(directoryName)) {
                        if (db.insertAutoscanDirectory(directoryName)) {
                            modelAutoscanDirectories.addElement(directory);
                        } else {
                            messageErrorInsertAutoscanDirectory(directoryName);
                        }
                    }
                }
            }
        }
        setEnabled();
    }

    private void removeSelectedAutoscanDirectories() {
        Object[] values = listTasksAutoscanDirectories.getSelectedValues();
        for (int i = 0; i < values.length; i++) {
            File directory = (File) values[i];
            String directoryName = (directory).getAbsolutePath();
            if (db.existsAutoscanDirectory(directoryName)) {
                if (db.deleteAutoscanDirectory(directoryName)) {
                    modelAutoscanDirectories.removeElement(directory);
                } else {
                    messageErrorDeleteAutoscanDirectory(directoryName);
                }
            }
        }
        setEnabled();
    }

    private void messageErrorInsertAutoscanDirectory(String directoryName) {
        MessageFormat msg = new MessageFormat(Bundle.getString("UserSettingsDialog.ErrorMessage.InsertAutoscanDirectory"));
        Object[] params = {directoryName};
        JOptionPane.showMessageDialog(
            this,
            msg.format(params),
            Bundle.getString("UserSettingsDialog.ErrorMessage.InsertAutoscanDirectory.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    private void messageErrorDeleteAutoscanDirectory(String directoryName) {
        MessageFormat msg = new MessageFormat(Bundle.getString("UserSettingsDialog.ErrorMessage.DeleteAutoscanDirectory"));
        Object[] params = {directoryName};
        JOptionPane.showMessageDialog(
            this,
            msg.format(params),
            Bundle.getString("UserSettingsDialog.ErrorMessage.DeleteAutoscanDirectory.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkListSearchColumns) {
            boolean selected = checkListSearchColumns.getSelectionCount() > 0;
            notifyChangeListener(new UserSettingsChangeEvent(selected
                ? UserSettingsChangeEvent.Changed.fastSearchColumnDefined
                : UserSettingsChangeEvent.Changed.noFastSearchColumns));
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            modelAutoscanDirectories = new ListModelAutoscanDirectories();
            listTasksAutoscanDirectories.setModel(modelAutoscanDirectories);
        }
        super.setVisible(visible);
    }

    private void setEnabledButtonRemoveAutoscanDirectory() {
        buttonTasksAutoscanRemoveDirectories.setEnabled(
            listTasksAutoscanDirectories.getSelectedIndices().length > 0);
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
        panelOpen = new javax.swing.JPanel();
        panelImageOpenApps = new javax.swing.JPanel();
        buttonImageOpenApp = new javax.swing.JButton();
        labelInfoOtherOpenImageApps = new javax.swing.JLabel();
        scrollPaneListOtherOpenImageApps = new javax.swing.JScrollPane();
        listOpenImageApps = new javax.swing.JList();
        buttonRemoveOtherOpenImageApp = new javax.swing.JButton();
        buttonAddOtherOpenImageApp = new javax.swing.JButton();
        buttonOpenImageAppMoveDown = new javax.swing.JButton();
        buttonOpenImageAppMoveUp = new javax.swing.JButton();
        labelImageInfotextOpenApp = new javax.swing.JLabel();
        labelImageOpenApp = new javax.swing.JLabel();
        panelSearch = new javax.swing.JPanel();
        labelSearch = new javax.swing.JLabel();
        scrollPaneSearchColumns = new JScrollPane(checkListSearchColumns);
        panelThumbnails = new javax.swing.JPanel();
        panelThumbnailDimensions = new javax.swing.JPanel();
        checkBoxUseEmbeddedThumbnails = new javax.swing.JCheckBox();
        labelMaxThumbnailLength = new javax.swing.JLabel();
        spinnerMaxThumbnailSize = new javax.swing.JSpinner();
        labelThumbnailCountPerRow = new javax.swing.JLabel();
        spinnerThumbnailCountPerRow = new javax.swing.JSpinner();
        labelInfoChangeLength = new javax.swing.JLabel();
        panelExternalThumbnailApp = new javax.swing.JPanel();
        checkBoxExternalThumbnailApp = new javax.swing.JCheckBox();
        labelInfoExternalThumbnailApp = new javax.swing.JLabel();
        textFieldExternalThumbnailApp = new javax.swing.JTextField();
        panelIptc = new javax.swing.JPanel();
        labelIptcCharset = new javax.swing.JLabel();
        comboBoxIptcCharset = new javax.swing.JComboBox();
        panelTasks = new javax.swing.JPanel();
        panelTasksAutoscan = new javax.swing.JPanel();
        labelTasksAutoscanMoreInfoDirectories = new javax.swing.JLabel();
        labelTasksAutoscanInfoDirectories = new javax.swing.JLabel();
        scrollPaneTasksAutoscanListDirectories = new javax.swing.JScrollPane();
        listTasksAutoscanDirectories = new javax.swing.JList();
        checkBoxTasksAutoscanIncludeSubdirectories = new javax.swing.JCheckBox();
        buttonTasksAutoscanRemoveDirectories = new javax.swing.JButton();
        buttonTasksAutoscanAddDirectories = new javax.swing.JButton();
        panelTasksOther = new javax.swing.JPanel();
        checkBoxTasksRemoveRecordsWithNotExistingFiles = new javax.swing.JCheckBox();
        labelTasksMinutesToStartScheduledTasks = new javax.swing.JLabel();
        spinnerTasksMinutesToStartScheduledTasks = new javax.swing.JSpinner();
        panelPerformance = new javax.swing.JPanel();
        panelAccelerateStart = new javax.swing.JPanel();
        checkBoxDisableAutocomplete = new javax.swing.JCheckBox();
        labelInfoDisableAutocomplete = new javax.swing.JLabel();
        checkBoxDisableExpandDirectoriesTree = new javax.swing.JCheckBox();
        labelInfoDisableExpandDirectoriesTree = new javax.swing.JLabel();
        panelThreadPriority = new javax.swing.JPanel();
        labelThreadPriority = new javax.swing.JLabel();
        comboBoxThreadPriority = new javax.swing.JComboBox();
        labelInfoThreadPriority = new javax.swing.JLabel();
        panelFileExcludePatterns = new de.elmar_baumann.imv.view.panels.FileExcludePatternsPanel();
        panelOther = new javax.swing.JPanel();
        panelLogfile = new javax.swing.JPanel();
        labelLogLevel = new javax.swing.JLabel();
        comboBoxLogLevel = new javax.swing.JComboBox();
        labelLogFormat = new javax.swing.JLabel();
        comboBoxLogfileFormatter = new javax.swing.JComboBox();
        checkBoxAcceptHiddenDirectories = new javax.swing.JCheckBox();
        labelAppLookAndFeel = new javax.swing.JLabel();
        comboBoxAppLookAndFeel = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString("UserSettingsDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabbedPane.setFont(new java.awt.Font("Dialog", 0, 12));

        panelImageOpenApps.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelImageOpenApps.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        buttonImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonImageOpenApp.setMnemonic('a');
        buttonImageOpenApp.setText(Bundle.getString("UserSettingsDialog.buttonImageOpenApp.text")); // NOI18N
        buttonImageOpenApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonImageOpenAppActionPerformed(evt);
            }
        });

        labelInfoOtherOpenImageApps.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfoOtherOpenImageApps.setText(Bundle.getString("UserSettingsDialog.labelInfoOtherOpenImageApps.text")); // NOI18N

        listOpenImageApps.setModel(modelImageOpenApps);
        listOpenImageApps.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listOpenImageApps.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listOpenImageAppsValueChanged(evt);
            }
        });
        scrollPaneListOtherOpenImageApps.setViewportView(listOpenImageApps);

        buttonRemoveOtherOpenImageApp.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonRemoveOtherOpenImageApp.setMnemonic('e');
        buttonRemoveOtherOpenImageApp.setText(Bundle.getString("UserSettingsDialog.buttonRemoveOtherOpenImageApp.text")); // NOI18N
        buttonRemoveOtherOpenImageApp.setEnabled(false);
        buttonRemoveOtherOpenImageApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveOtherOpenImageAppActionPerformed(evt);
            }
        });

        buttonAddOtherOpenImageApp.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonAddOtherOpenImageApp.setMnemonic('w');
        buttonAddOtherOpenImageApp.setText(Bundle.getString("UserSettingsDialog.buttonAddOtherOpenImageApp.text")); // NOI18N
        buttonAddOtherOpenImageApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddOtherOpenImageAppActionPerformed(evt);
            }
        });

        buttonOpenImageAppMoveDown.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonOpenImageAppMoveDown.setMnemonic('u');
        buttonOpenImageAppMoveDown.setText(Bundle.getString("UserSettingsDialog.buttonOpenImageAppMoveDown.text")); // NOI18N
        buttonOpenImageAppMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOpenImageAppMoveDownActionPerformed(evt);
            }
        });

        buttonOpenImageAppMoveUp.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonOpenImageAppMoveUp.setMnemonic('o');
        buttonOpenImageAppMoveUp.setText(Bundle.getString("UserSettingsDialog.buttonOpenImageAppMoveUp.text")); // NOI18N
        buttonOpenImageAppMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOpenImageAppMoveUpActionPerformed(evt);
            }
        });

        labelImageInfotextOpenApp.setFont(new java.awt.Font("Dialog", 0, 12));
        labelImageInfotextOpenApp.setText(Bundle.getString("UserSettingsDialog.labelImageInfotextOpenApp.text")); // NOI18N

        labelImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12));
        labelImageOpenApp.setForeground(new java.awt.Color(0, 0, 255));
        labelImageOpenApp.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout panelImageOpenAppsLayout = new javax.swing.GroupLayout(panelImageOpenApps);
        panelImageOpenApps.setLayout(panelImageOpenAppsLayout);
        panelImageOpenAppsLayout.setHorizontalGroup(
            panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelImageOpenApp, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                    .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                        .addComponent(labelImageInfotextOpenApp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonImageOpenApp))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelImageOpenAppsLayout.createSequentialGroup()
                        .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                                .addComponent(buttonRemoveOtherOpenImageApp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonAddOtherOpenImageApp))
                            .addComponent(scrollPaneListOtherOpenImageApps, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonOpenImageAppMoveDown)
                            .addComponent(buttonOpenImageAppMoveUp)))
                    .addComponent(labelInfoOtherOpenImageApps))
                .addContainerGap())
        );

        panelImageOpenAppsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonOpenImageAppMoveDown, buttonOpenImageAppMoveUp});

        panelImageOpenAppsLayout.setVerticalGroup(
            panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelImageInfotextOpenApp)
                    .addComponent(buttonImageOpenApp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelImageOpenApp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoOtherOpenImageApps)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                        .addComponent(scrollPaneListOtherOpenImageApps, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonAddOtherOpenImageApp)
                            .addComponent(buttonRemoveOtherOpenImageApp)))
                    .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                        .addComponent(buttonOpenImageAppMoveUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonOpenImageAppMoveDown)))
                .addContainerGap())
        );

        panelImageOpenAppsLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelImageInfotextOpenApp, labelImageOpenApp});

        panelImageOpenAppsLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonOpenImageAppMoveDown, buttonOpenImageAppMoveUp});

        javax.swing.GroupLayout panelOpenLayout = new javax.swing.GroupLayout(panelOpen);
        panelOpen.setLayout(panelOpenLayout);
        panelOpenLayout.setHorizontalGroup(
            panelOpenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOpenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelImageOpenApps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelOpenLayout.setVerticalGroup(
            panelOpenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOpenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelImageOpenApps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelOpen.TabConstraints.tabTitle"), panelOpen); // NOI18N

        labelSearch.setFont(new java.awt.Font("Dialog", 0, 11));
        labelSearch.setText(Bundle.getString("UserSettingsDialog.labelSearch.text")); // NOI18N

        javax.swing.GroupLayout panelSearchLayout = new javax.swing.GroupLayout(panelSearch);
        panelSearch.setLayout(panelSearchLayout);
        panelSearchLayout.setHorizontalGroup(
            panelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSearch)
                .addContainerGap(310, Short.MAX_VALUE))
            .addGroup(panelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelSearchLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPaneSearchColumns, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        panelSearchLayout.setVerticalGroup(
            panelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSearch)
                .addContainerGap(288, Short.MAX_VALUE))
            .addGroup(panelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSearchLayout.createSequentialGroup()
                    .addGap(38, 38, 38)
                    .addComponent(scrollPaneSearchColumns, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelSearch.TabConstraints.tabTitle"), panelSearch); // NOI18N

        panelThumbnailDimensions.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelThumbnailDimensions.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        checkBoxUseEmbeddedThumbnails.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxUseEmbeddedThumbnails.setText(Bundle.getString("UserSettingsDialog.checkBoxUseEmbeddedThumbnails.text")); // NOI18N

        labelMaxThumbnailLength.setFont(new java.awt.Font("Dialog", 0, 12));
        labelMaxThumbnailLength.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelMaxThumbnailLength.setText(Bundle.getString("UserSettingsDialog.labelMaxThumbnailLength.text")); // NOI18N

        spinnerMaxThumbnailSize.setFont(new java.awt.Font("Dialog", 0, 12));
        spinnerMaxThumbnailSize.setModel(new SpinnerNumberModel(150, 50, 250, 1));

        labelThumbnailCountPerRow.setFont(new java.awt.Font("Dialog", 0, 12));
        labelThumbnailCountPerRow.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelThumbnailCountPerRow.setText(Bundle.getString("UserSettingsDialog.labelThumbnailCountPerRow.text")); // NOI18N

        spinnerThumbnailCountPerRow.setFont(new java.awt.Font("Dialog", 0, 12));
        spinnerThumbnailCountPerRow.setModel(new SpinnerNumberModel(3, 1, 20, 1));
        spinnerThumbnailCountPerRow.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerThumbnailCountPerRowStateChanged(evt);
            }
        });

        labelInfoChangeLength.setForeground(new java.awt.Color(255, 0, 0));
        labelInfoChangeLength.setText(Bundle.getString("UserSettingsDialog.labelInfoChangeLength.text")); // NOI18N

        javax.swing.GroupLayout panelThumbnailDimensionsLayout = new javax.swing.GroupLayout(panelThumbnailDimensions);
        panelThumbnailDimensions.setLayout(panelThumbnailDimensionsLayout);
        panelThumbnailDimensionsLayout.setHorizontalGroup(
            panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                        .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkBoxUseEmbeddedThumbnails)
                            .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                                .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(labelThumbnailCountPerRow)
                                    .addComponent(labelMaxThumbnailLength))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(spinnerThumbnailCountPerRow, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                                    .addComponent(spinnerMaxThumbnailSize, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))))
                        .addGap(77, 77, 77))
                    .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                        .addComponent(labelInfoChangeLength, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        panelThumbnailDimensionsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {spinnerMaxThumbnailSize, spinnerThumbnailCountPerRow});

        panelThumbnailDimensionsLayout.setVerticalGroup(
            panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                .addComponent(checkBoxUseEmbeddedThumbnails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMaxThumbnailLength)
                    .addComponent(spinnerMaxThumbnailSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelThumbnailCountPerRow)
                    .addComponent(spinnerThumbnailCountPerRow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoChangeLength, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelExternalThumbnailApp.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelExternalThumbnailApp.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        checkBoxExternalThumbnailApp.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxExternalThumbnailApp.setText(Bundle.getString("UserSettingsDialog.checkBoxExternalThumbnailApp.text")); // NOI18N
        checkBoxExternalThumbnailApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxExternalThumbnailAppActionPerformed(evt);
            }
        });

        labelInfoExternalThumbnailApp.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfoExternalThumbnailApp.setText(Bundle.getString("UserSettingsDialog.labelInfoExternalThumbnailApp.text")); // NOI18N

        textFieldExternalThumbnailApp.setEnabled(false);

        javax.swing.GroupLayout panelExternalThumbnailAppLayout = new javax.swing.GroupLayout(panelExternalThumbnailApp);
        panelExternalThumbnailApp.setLayout(panelExternalThumbnailAppLayout);
        panelExternalThumbnailAppLayout.setHorizontalGroup(
            panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxExternalThumbnailApp)
                    .addComponent(labelInfoExternalThumbnailApp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                    .addComponent(textFieldExternalThumbnailApp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelExternalThumbnailAppLayout.setVerticalGroup(
            panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                .addComponent(checkBoxExternalThumbnailApp)
                .addGap(9, 9, 9)
                .addComponent(labelInfoExternalThumbnailApp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldExternalThumbnailApp, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelThumbnailsLayout = new javax.swing.GroupLayout(panelThumbnails);
        panelThumbnails.setLayout(panelThumbnailsLayout);
        panelThumbnailsLayout.setHorizontalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelThumbnailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelExternalThumbnailApp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelThumbnailDimensions, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelThumbnailsLayout.setVerticalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThumbnailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelThumbnailDimensions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelExternalThumbnailApp, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelThumbnails.TabConstraints.tabTitle"), panelThumbnails); // NOI18N

        labelIptcCharset.setFont(new java.awt.Font("Dialog", 0, 12));
        labelIptcCharset.setText(Bundle.getString("UserSettingsDialog.labelIptcCharset.text")); // NOI18N

        comboBoxIptcCharset.setFont(new java.awt.Font("Dialog", 0, 12));
        comboBoxIptcCharset.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ISO-8859-1", "UTF-8" }));

        javax.swing.GroupLayout panelIptcLayout = new javax.swing.GroupLayout(panelIptc);
        panelIptc.setLayout(panelIptcLayout);
        panelIptcLayout.setHorizontalGroup(
            panelIptcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelIptcLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelIptcCharset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(384, Short.MAX_VALUE))
        );
        panelIptcLayout.setVerticalGroup(
            panelIptcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelIptcLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelIptcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelIptcCharset)
                    .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(278, Short.MAX_VALUE))
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelIptc.TabConstraints.tabTitle"), panelIptc); // NOI18N

        panelTasksAutoscan.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelTasksAutoscan.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelTasksAutoscanMoreInfoDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        labelTasksAutoscanMoreInfoDirectories.setText(Bundle.getString("UserSettingsDialog.labelTasksAutoscanMoreInfoDirectories.text")); // NOI18N

        labelTasksAutoscanInfoDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        labelTasksAutoscanInfoDirectories.setText(Bundle.getString("UserSettingsDialog.labelTasksAutoscanInfoDirectories.text")); // NOI18N

        listTasksAutoscanDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        listTasksAutoscanDirectories.setModel(modelAutoscanDirectories);
        listTasksAutoscanDirectories.setCellRenderer(new ListCellRendererFileSystem(true));
        listTasksAutoscanDirectories.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listTasksAutoscanDirectoriesValueChanged(evt);
            }
        });
        listTasksAutoscanDirectories.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                listTasksAutoscanDirectoriesKeyReleased(evt);
            }
        });
        scrollPaneTasksAutoscanListDirectories.setViewportView(listTasksAutoscanDirectories);

        checkBoxTasksAutoscanIncludeSubdirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxTasksAutoscanIncludeSubdirectories.setText(Bundle.getString("UserSettingsDialog.checkBoxTasksAutoscanIncludeSubdirectories.text")); // NOI18N

        buttonTasksAutoscanRemoveDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonTasksAutoscanRemoveDirectories.setMnemonic('e');
        buttonTasksAutoscanRemoveDirectories.setText(Bundle.getString("UserSettingsDialog.buttonTasksAutoscanRemoveDirectories.text")); // NOI18N
        buttonTasksAutoscanRemoveDirectories.setEnabled(false);
        buttonTasksAutoscanRemoveDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTasksAutoscanRemoveDirectoriesActionPerformed(evt);
            }
        });

        buttonTasksAutoscanAddDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonTasksAutoscanAddDirectories.setMnemonic('h');
        buttonTasksAutoscanAddDirectories.setText(Bundle.getString("UserSettingsDialog.buttonTasksAutoscanAddDirectories.text")); // NOI18N
        buttonTasksAutoscanAddDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTasksAutoscanAddDirectoriesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTasksAutoscanLayout = new javax.swing.GroupLayout(panelTasksAutoscan);
        panelTasksAutoscan.setLayout(panelTasksAutoscanLayout);
        panelTasksAutoscanLayout.setHorizontalGroup(
            panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTasksAutoscanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPaneTasksAutoscanListDirectories, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTasksAutoscanLayout.createSequentialGroup()
                        .addGroup(panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelTasksAutoscanLayout.createSequentialGroup()
                                .addComponent(checkBoxTasksAutoscanIncludeSubdirectories)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE))
                            .addGroup(panelTasksAutoscanLayout.createSequentialGroup()
                                .addComponent(buttonTasksAutoscanRemoveDirectories)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(buttonTasksAutoscanAddDirectories))
                    .addComponent(labelTasksAutoscanMoreInfoDirectories, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                    .addComponent(labelTasksAutoscanInfoDirectories, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        panelTasksAutoscanLayout.setVerticalGroup(
            panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTasksAutoscanLayout.createSequentialGroup()
                .addComponent(labelTasksAutoscanMoreInfoDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTasksAutoscanInfoDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneTasksAutoscanListDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxTasksAutoscanIncludeSubdirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonTasksAutoscanAddDirectories)
                    .addComponent(buttonTasksAutoscanRemoveDirectories))
                .addContainerGap())
        );

        panelTasksOther.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelTasksOther.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        checkBoxTasksRemoveRecordsWithNotExistingFiles.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxTasksRemoveRecordsWithNotExistingFiles.setText(Bundle.getString("UserSettingsDialog.checkBoxTasksRemoveRecordsWithNotExistingFiles.text")); // NOI18N

        javax.swing.GroupLayout panelTasksOtherLayout = new javax.swing.GroupLayout(panelTasksOther);
        panelTasksOther.setLayout(panelTasksOtherLayout);
        panelTasksOtherLayout.setHorizontalGroup(
            panelTasksOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTasksOtherLayout.createSequentialGroup()
                .addComponent(checkBoxTasksRemoveRecordsWithNotExistingFiles)
                .addContainerGap(103, Short.MAX_VALUE))
        );
        panelTasksOtherLayout.setVerticalGroup(
            panelTasksOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTasksOtherLayout.createSequentialGroup()
                .addComponent(checkBoxTasksRemoveRecordsWithNotExistingFiles)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        labelTasksMinutesToStartScheduledTasks.setFont(new java.awt.Font("Dialog", 0, 12));
        labelTasksMinutesToStartScheduledTasks.setText(Bundle.getString("UserSettingsDialog.labelTasksMinutesToStartScheduledTasks.text")); // NOI18N

        spinnerTasksMinutesToStartScheduledTasks.setModel(new SpinnerNumberModel(5, 1, 6000, 1));

        javax.swing.GroupLayout panelTasksLayout = new javax.swing.GroupLayout(panelTasks);
        panelTasks.setLayout(panelTasksLayout);
        panelTasksLayout.setHorizontalGroup(
            panelTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTasksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelTasksOther, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelTasksAutoscan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelTasksLayout.createSequentialGroup()
                        .addComponent(labelTasksMinutesToStartScheduledTasks)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerTasksMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelTasksLayout.setVerticalGroup(
            panelTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTasksLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelTasksAutoscan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTasksOther, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTasksMinutesToStartScheduledTasks)
                    .addComponent(spinnerTasksMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelTasks.TabConstraints.tabTitle"), panelTasks); // NOI18N

        panelAccelerateStart.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelAccelerateStart.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        checkBoxDisableAutocomplete.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxDisableAutocomplete.setText(Bundle.getString("UserSettingsDialog.checkBoxDisableAutocomplete.text")); // NOI18N

        labelInfoDisableAutocomplete.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfoDisableAutocomplete.setForeground(new java.awt.Color(255, 0, 0));
        labelInfoDisableAutocomplete.setText(Bundle.getString("UserSettingsDialog.labelInfoDisableAutocomplete.text")); // NOI18N

        checkBoxDisableExpandDirectoriesTree.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxDisableExpandDirectoriesTree.setText(Bundle.getString("UserSettingsDialog.checkBoxDisableExpandDirectoriesTree.text")); // NOI18N

        labelInfoDisableExpandDirectoriesTree.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfoDisableExpandDirectoriesTree.setForeground(new java.awt.Color(255, 0, 0));
        labelInfoDisableExpandDirectoriesTree.setText(Bundle.getString("UserSettingsDialog.labelInfoDisableExpandDirectoriesTree.text")); // NOI18N

        javax.swing.GroupLayout panelAccelerateStartLayout = new javax.swing.GroupLayout(panelAccelerateStart);
        panelAccelerateStart.setLayout(panelAccelerateStartLayout);
        panelAccelerateStartLayout.setHorizontalGroup(
            panelAccelerateStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAccelerateStartLayout.createSequentialGroup()
                .addGroup(panelAccelerateStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxDisableAutocomplete)
                    .addGroup(panelAccelerateStartLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelInfoDisableAutocomplete))
                    .addComponent(checkBoxDisableExpandDirectoriesTree)
                    .addGroup(panelAccelerateStartLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelInfoDisableExpandDirectoriesTree)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelAccelerateStartLayout.setVerticalGroup(
            panelAccelerateStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAccelerateStartLayout.createSequentialGroup()
                .addComponent(checkBoxDisableAutocomplete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoDisableAutocomplete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxDisableExpandDirectoriesTree)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoDisableExpandDirectoriesTree)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        panelThreadPriority.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelThreadPriority.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelThreadPriority.setFont(new java.awt.Font("Dialog", 0, 12));
        labelThreadPriority.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelThreadPriority.setText(Bundle.getString("UserSettingsDialog.labelThreadPriority.text")); // NOI18N

        comboBoxThreadPriority.setFont(new java.awt.Font("Dialog", 0, 12));
        comboBoxThreadPriority.setModel(new de.elmar_baumann.imv.model.ComboBoxModelThreadPriority());
        comboBoxThreadPriority.setEditor(null);
        comboBoxThreadPriority.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxThreadPriorityActionPerformed(evt);
            }
        });

        labelInfoThreadPriority.setForeground(new java.awt.Color(255, 0, 0));
        labelInfoThreadPriority.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        labelInfoThreadPriority.setText(Bundle.getString("UserSettingsDialog.labelInfoThreadPriority.text")); // NOI18N

        javax.swing.GroupLayout panelThreadPriorityLayout = new javax.swing.GroupLayout(panelThreadPriority);
        panelThreadPriority.setLayout(panelThreadPriorityLayout);
        panelThreadPriorityLayout.setHorizontalGroup(
            panelThreadPriorityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThreadPriorityLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelThreadPriorityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelInfoThreadPriority)
                    .addGroup(panelThreadPriorityLayout.createSequentialGroup()
                        .addComponent(labelThreadPriority)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxThreadPriority, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(173, Short.MAX_VALUE))
        );
        panelThreadPriorityLayout.setVerticalGroup(
            panelThreadPriorityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThreadPriorityLayout.createSequentialGroup()
                .addGroup(panelThreadPriorityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelThreadPriority, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxThreadPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoThreadPriority)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelPerformanceLayout = new javax.swing.GroupLayout(panelPerformance);
        panelPerformance.setLayout(panelPerformanceLayout);
        panelPerformanceLayout.setHorizontalGroup(
            panelPerformanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPerformanceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPerformanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelAccelerateStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelThreadPriority, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPerformanceLayout.setVerticalGroup(
            panelPerformanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPerformanceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelAccelerateStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelThreadPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelPerformance.TabConstraints.tabTitle"), panelPerformance); // NOI18N
        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelFileExcludePatterns.TabConstraints.tabTitle"), panelFileExcludePatterns); // NOI18N

        panelLogfile.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelLogfile.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelLogLevel.setFont(new java.awt.Font("Dialog", 0, 12));
        labelLogLevel.setText(Bundle.getString("UserSettingsDialog.labelLogLevel.text")); // NOI18N

        comboBoxLogLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { java.util.logging.Level.WARNING.getLocalizedName(), java.util.logging.Level.SEVERE.getLocalizedName(), java.util.logging.Level.INFO.getLocalizedName(), java.util.logging.Level.CONFIG.getLocalizedName(), java.util.logging.Level.FINE.getLocalizedName(), java.util.logging.Level.FINER.getLocalizedName(), java.util.logging.Level.FINEST.getLocalizedName() }));

        labelLogFormat.setFont(new java.awt.Font("Dialog", 0, 12));
        labelLogFormat.setText(Bundle.getString("UserSettingsDialog.labelLogFormat.text")); // NOI18N

        comboBoxLogfileFormatter.setModel(new ComboBoxModelLogfileFormatter());
        comboBoxLogfileFormatter.setRenderer(new ListCellRendererLogfileFormatter());

        javax.swing.GroupLayout panelLogfileLayout = new javax.swing.GroupLayout(panelLogfile);
        panelLogfile.setLayout(panelLogfileLayout);
        panelLogfileLayout.setHorizontalGroup(
            panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogfileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelLogLevel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelLogFormat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxLogfileFormatter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(167, Short.MAX_VALUE))
        );
        panelLogfileLayout.setVerticalGroup(
            panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogfileLayout.createSequentialGroup()
                .addGroup(panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLogLevel)
                    .addComponent(labelLogFormat)
                    .addComponent(comboBoxLogfileFormatter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        checkBoxAcceptHiddenDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxAcceptHiddenDirectories.setText(Bundle.getString("UserSettingsDialog.checkBoxAcceptHiddenDirectories.text")); // NOI18N

        labelAppLookAndFeel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        labelAppLookAndFeel.setText(Bundle.getString("UserSettingsDialog.labelAppLookAndFeel.text")); // NOI18N

        comboBoxAppLookAndFeel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        comboBoxAppLookAndFeel.setModel(modelLookAndFeel);
        comboBoxAppLookAndFeel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxAppLookAndFeelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelOtherLayout = new javax.swing.GroupLayout(panelOther);
        panelOther.setLayout(panelOtherLayout);
        panelOtherLayout.setHorizontalGroup(
            panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxAcceptHiddenDirectories)
                    .addComponent(panelLogfile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelOtherLayout.createSequentialGroup()
                        .addComponent(labelAppLookAndFeel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxAppLookAndFeel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelOtherLayout.setVerticalGroup(
            panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelLogfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxAcceptHiddenDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelAppLookAndFeel)
                    .addComponent(comboBoxAppLookAndFeel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(185, Short.MAX_VALUE))
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelOther.TabConstraints.tabTitle"), panelOther); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonImageOpenAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonImageOpenAppActionPerformed
    setDefaultOpenApp();
}//GEN-LAST:event_buttonImageOpenAppActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    writePersistent();
}//GEN-LAST:event_formWindowClosing

private void checkBoxExternalThumbnailAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxExternalThumbnailAppActionPerformed
    enableExternalAppSettings();
}//GEN-LAST:event_checkBoxExternalThumbnailAppActionPerformed

private void buttonAddOtherOpenImageAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddOtherOpenImageAppActionPerformed
    addOtherOpenImageApp();
}//GEN-LAST:event_buttonAddOtherOpenImageAppActionPerformed

private void buttonOpenImageAppMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenImageAppMoveUpActionPerformed
    moveUpOpenImageApp();
}//GEN-LAST:event_buttonOpenImageAppMoveUpActionPerformed

private void buttonOpenImageAppMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenImageAppMoveDownActionPerformed
    moveDownOpenImageApp();
}//GEN-LAST:event_buttonOpenImageAppMoveDownActionPerformed

private void buttonRemoveOtherOpenImageAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveOtherOpenImageAppActionPerformed
    removeOtherOpenImageApp();
}//GEN-LAST:event_buttonRemoveOtherOpenImageAppActionPerformed

private void listOpenImageAppsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listOpenImageAppsValueChanged
    setEnabled();
}//GEN-LAST:event_listOpenImageAppsValueChanged

private void listTasksAutoscanDirectoriesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listTasksAutoscanDirectoriesKeyReleased
    if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
        removeSelectedAutoscanDirectories();
    }
}//GEN-LAST:event_listTasksAutoscanDirectoriesKeyReleased

private void buttonTasksAutoscanAddDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTasksAutoscanAddDirectoriesActionPerformed
    addAutoscanDirectories();
}//GEN-LAST:event_buttonTasksAutoscanAddDirectoriesActionPerformed

private void buttonTasksAutoscanRemoveDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTasksAutoscanRemoveDirectoriesActionPerformed
    removeSelectedAutoscanDirectories();
}//GEN-LAST:event_buttonTasksAutoscanRemoveDirectoriesActionPerformed

private void comboBoxThreadPriorityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxThreadPriorityActionPerformed
    notifyChangeListener(new UserSettingsChangeEvent(
        UserSettingsChangeEvent.Changed.loglevel));
}//GEN-LAST:event_comboBoxThreadPriorityActionPerformed

private void spinnerThumbnailCountPerRowStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerThumbnailCountPerRowStateChanged
    notifyChangeListener(new UserSettingsChangeEvent(
        UserSettingsChangeEvent.Changed.thumbnailsPanelColumnsCount));
}//GEN-LAST:event_spinnerThumbnailCountPerRowStateChanged

private void listTasksAutoscanDirectoriesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listTasksAutoscanDirectoriesValueChanged
    setEnabledButtonRemoveAutoscanDirectory();
}//GEN-LAST:event_listTasksAutoscanDirectoriesValueChanged

private void comboBoxAppLookAndFeelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxAppLookAndFeelActionPerformed
    writePersistentComoBoxSkinsIndex();
}//GEN-LAST:event_comboBoxAppLookAndFeelActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                UserSettingsDialog dialog = UserSettingsDialog.getInstance();
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
    private javax.swing.JButton buttonAddOtherOpenImageApp;
    private javax.swing.JButton buttonImageOpenApp;
    private javax.swing.JButton buttonOpenImageAppMoveDown;
    private javax.swing.JButton buttonOpenImageAppMoveUp;
    private javax.swing.JButton buttonRemoveOtherOpenImageApp;
    private javax.swing.JButton buttonTasksAutoscanAddDirectories;
    private javax.swing.JButton buttonTasksAutoscanRemoveDirectories;
    public javax.swing.JCheckBox checkBoxAcceptHiddenDirectories;
    public javax.swing.JCheckBox checkBoxDisableAutocomplete;
    public javax.swing.JCheckBox checkBoxDisableExpandDirectoriesTree;
    public javax.swing.JCheckBox checkBoxExternalThumbnailApp;
    public javax.swing.JCheckBox checkBoxTasksAutoscanIncludeSubdirectories;
    public javax.swing.JCheckBox checkBoxTasksRemoveRecordsWithNotExistingFiles;
    public javax.swing.JCheckBox checkBoxUseEmbeddedThumbnails;
    private javax.swing.JComboBox comboBoxAppLookAndFeel;
    public javax.swing.JComboBox comboBoxIptcCharset;
    public javax.swing.JComboBox comboBoxLogLevel;
    public javax.swing.JComboBox comboBoxLogfileFormatter;
    public javax.swing.JComboBox comboBoxThreadPriority;
    private javax.swing.JLabel labelAppLookAndFeel;
    private javax.swing.JLabel labelImageInfotextOpenApp;
    public javax.swing.JLabel labelImageOpenApp;
    private javax.swing.JLabel labelInfoChangeLength;
    private javax.swing.JLabel labelInfoDisableAutocomplete;
    private javax.swing.JLabel labelInfoDisableExpandDirectoriesTree;
    private javax.swing.JLabel labelInfoExternalThumbnailApp;
    private javax.swing.JLabel labelInfoOtherOpenImageApps;
    private javax.swing.JLabel labelInfoThreadPriority;
    private javax.swing.JLabel labelIptcCharset;
    private javax.swing.JLabel labelLogFormat;
    private javax.swing.JLabel labelLogLevel;
    private javax.swing.JLabel labelMaxThumbnailLength;
    private javax.swing.JLabel labelSearch;
    private javax.swing.JLabel labelTasksAutoscanInfoDirectories;
    private javax.swing.JLabel labelTasksAutoscanMoreInfoDirectories;
    private javax.swing.JLabel labelTasksMinutesToStartScheduledTasks;
    private javax.swing.JLabel labelThreadPriority;
    private javax.swing.JLabel labelThumbnailCountPerRow;
    public javax.swing.JList listOpenImageApps;
    private javax.swing.JList listTasksAutoscanDirectories;
    private javax.swing.JPanel panelAccelerateStart;
    private javax.swing.JPanel panelExternalThumbnailApp;
    private de.elmar_baumann.imv.view.panels.FileExcludePatternsPanel panelFileExcludePatterns;
    private javax.swing.JPanel panelImageOpenApps;
    private javax.swing.JPanel panelIptc;
    private javax.swing.JPanel panelLogfile;
    private javax.swing.JPanel panelOpen;
    public javax.swing.JPanel panelOther;
    private javax.swing.JPanel panelPerformance;
    private javax.swing.JPanel panelSearch;
    private javax.swing.JPanel panelTasks;
    private javax.swing.JPanel panelTasksAutoscan;
    private javax.swing.JPanel panelTasksOther;
    private javax.swing.JPanel panelThreadPriority;
    private javax.swing.JPanel panelThumbnailDimensions;
    private javax.swing.JPanel panelThumbnails;
    private javax.swing.JScrollPane scrollPaneListOtherOpenImageApps;
    private javax.swing.JScrollPane scrollPaneSearchColumns;
    private javax.swing.JScrollPane scrollPaneTasksAutoscanListDirectories;
    public javax.swing.JSpinner spinnerMaxThumbnailSize;
    public javax.swing.JSpinner spinnerTasksMinutesToStartScheduledTasks;
    public javax.swing.JSpinner spinnerThumbnailCountPerRow;
    private javax.swing.JTabbedPane tabbedPane;
    public javax.swing.JTextField textFieldExternalThumbnailApp;
    // End of variables declaration//GEN-END:variables

}
