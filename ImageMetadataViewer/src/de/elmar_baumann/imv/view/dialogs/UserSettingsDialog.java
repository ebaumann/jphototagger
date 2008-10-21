package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabaseAutoscanDirectories;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.model.ComboBoxModelLogfileFormatter;
import de.elmar_baumann.imv.model.ComboBoxModelThreadPriority;
import de.elmar_baumann.imv.model.ListModelAutoscanDirectories;
import de.elmar_baumann.imv.model.ListModelFastSearchColumns;
import de.elmar_baumann.imv.model.ListModelOtherImageOpenApps;
import de.elmar_baumann.lib.component.CheckList;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.UpdateAllThumbnails;
import de.elmar_baumann.imv.view.renderer.ListCellRendererLogfileFormatter;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.persistence.PersistentSettingsHints;
import de.elmar_baumann.lib.renderer.ListCellRendererFileSystem;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
public class UserSettingsDialog extends Dialog implements ActionListener {

    private DatabaseAutoscanDirectories db = DatabaseAutoscanDirectories.getInstance();
    private final String keyLastSelectedAutoscanDirectory = "UserSettingsDialog.keyLastSelectedAutoscanDirectory"; // NOI18N
    private final String keyTabbedPaneIndex = "UserSettingsDialog.TabbedPaneIndex"; // NOI18N
    private List<UserSettingsChangeListener> changeListeners = new LinkedList<UserSettingsChangeListener>();
    public CheckList checkListSearchColumns = new CheckList();
    public ListModelFastSearchColumns searchColumnsListModel = new ListModelFastSearchColumns();
    private Map<Tab, Integer> indexOfTab = new HashMap<Tab, Integer>();
    private Map<Integer, Tab> tabOfIndex = new HashMap<Integer, Tab>();
    private ListModelOtherImageOpenApps modelOtherImageOpenApps = new ListModelOtherImageOpenApps();
    private ListModelAutoscanDirectories modelAutoscanDirectories = new ListModelAutoscanDirectories();
    private String lastSelectedAutoscanDirectory = ""; // NOI18N
    private String lastSelectedAutocopyDirectory = ""; // NOI18N
    private String previousDirectory = ""; // NOI18N
    private UpdateAllThumbnails thumbnailsUpdater;
    private ListenerProvider listenerProvider;
    private Map<Component, String> helpUrlOfComponent = new HashMap<Component, String>();
    private static UserSettingsDialog instance = new UserSettingsDialog();

    private UserSettingsDialog() {
        super((java.awt.Frame) null, false);
        listenerProvider = ListenerProvider.getInstance();
        changeListeners = listenerProvider.getUserSettingsChangeListeners();
        setSearchColumns();
        initComponents();
        initMaps();
        setIconImages(AppSettings.getAppIcons());
        readPersistent();
        setEnabled();
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        registerKeyStrokes();
    }

    private void chooseAutocopyDirectory() {
        File file = chooseDirectory(new File(lastSelectedAutocopyDirectory));
        if (file != null) {
            String directory = file.getAbsolutePath();
            labelAutocopyDirectory.setText(directory);
            lastSelectedAutocopyDirectory = directory;
            notifyChangeListener(new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.AutocopyDirectory, this));
        }
    }

    private File chooseDirectory(File startDirectory) {
        File dir = null;
        DirectoryChooser dialog = new DirectoryChooser(null, false);

        dialog.setStartDirectory(startDirectory);
        dialog.setMultiSelection(false);
        dialog.setVisible(true);

        if (dialog.accepted()) {
            dir = dialog.getSelectedDirectories().get(0);
        }
        return dir;
    }

    private void handleActionCheckBoxIsAutoscanIncludeSubdirectories() {
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.IsAutoscanIncludeSubdirectories, this));
    }

    private void handleActionCheckBoxIsTaskRemoveRecordsWithNotExistingFiles() {
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.IsTaskRemoveRecordsWithNotExistingFiles, this));
    }

    private void handleActionCheckBoxUseEmbeddedThumbnails() {
        if (checkBoxIsUseEmbeddedThumbnails.isSelected()) {
            checkBoxIsCreateThumbnailsWithExternalApp.setSelected(false);
        }
        setExternalThumbnailAppEnabled();
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.IsUseEmbeddedThumbnails, this));
    }

    private void handleActionComboBoxIptcCharset() {
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.IptcCharset, this));
    }

    private void handleActionComboBoxThreadPriorityAction() {
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.ThreadPriority, this));
    }

    private void handleActionPerformedCheckBoxIsAcceptHiddenDirectories() {
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.IsAcceptHiddenDirectories, this));
    }

    private void handleActionPerformedCheckBoxIsAutocompleteDisabled() {
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.IsUseAutocomplete, this));
    }

    private void handleActionPerformedComboBoxLogLevel() {
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.LogLevel, this));
    }

    private void handleActionPerformedComboBoxLogfileFormatterClass() {
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.LogfileFormatterClass, this));
    }

    private void handleKeyEventListTasksAutoscanDirectories(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSelectedAutoscanDirectories();
        }
    }

    private void handleKeyEventTextFieldExternalThumbnailCreationCommand() {
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.ExternalThumbnailCreationCommand, this));
    }

    private void handleStateChangedSpinnerMinutesToStartScheduledTasks() {
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.MinutesToStartScheduledTasks, this));
    }

    private void initMaps() {
        // TODO PERMANENT: Bei neuen Tabs ergänzen
        indexOfTab.put(Tab.Programs, 0);
        indexOfTab.put(Tab.FastSearch, 1);
        indexOfTab.put(Tab.Thumbnails, 2);
        indexOfTab.put(Tab.Iptc, 3);
        indexOfTab.put(Tab.Tasks, 4);
        indexOfTab.put(Tab.Performance, 5);
        indexOfTab.put(Tab.FileExcludePatterns, 6);
        indexOfTab.put(Tab.Misc, 7);

        for (Tab tab : indexOfTab.keySet()) {
            tabOfIndex.put(indexOfTab.get(tab), tab);
        }

        helpUrlOfComponent.put(tabbedPane.getComponentAt(0), Bundle.getString("Help.Url.UserSettingsDialog.Programs"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt(1), Bundle.getString("Help.Url.UserSettingsDialog.FastSearch"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt(2), Bundle.getString("Help.Url.UserSettingsDialog.Thumbnails"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt(3), Bundle.getString("Help.Url.UserSettingsDialog.Iptc"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt(4), Bundle.getString("Help.Url.UserSettingsDialog.Tasks"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt(5), Bundle.getString("Help.Url.UserSettingsDialog.Performance"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt(6), Bundle.getString("Help.Url.UserSettingsDialog.FileExcludePattern"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt(7), Bundle.getString("Help.Url.UserSettingsDialog.Misc"));
    }

    private void handleStateChangedSpinnerMaxThumbnailWidth() {
        notifyChangeListener(new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.MaxThumbnailWidth, this));
    }

    private void updateAllThumbnails() {
        synchronized (this) {
            buttonUpdateAllThumbnails.setEnabled(false);
            thumbnailsUpdater = new UpdateAllThumbnails();
            thumbnailsUpdater.addActionListener(this);
            Thread thread = new Thread(thumbnailsUpdater);
            thread.setPriority(UserSettings.getInstance().getThreadPriority());
            thread.start();
        }
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
        Misc
    };

    private void moveDownOpenImageApp() {
        if (canMoveDownOpenImageApp()) {
            int selectedIndex = listOtherImageOpenApps.getSelectedIndex();
            int newSelectedIndex = selectedIndex + 1;
            Object element = modelOtherImageOpenApps.get(selectedIndex);
            modelOtherImageOpenApps.remove(selectedIndex);
            modelOtherImageOpenApps.add(newSelectedIndex, element);
            listOtherImageOpenApps.setSelectedIndex(newSelectedIndex);
            setEnabled();
        }
    }

    private void notifyChangeListener(UserSettingsChangeEvent evt) {
        for (UserSettingsChangeListener listener : changeListeners) {
            listener.applySettings(evt);
        }
    }

    private void handleActionCheckBoxExternalThumbnail() {
        if (checkBoxIsCreateThumbnailsWithExternalApp.isSelected()) {
            checkBoxIsUseEmbeddedThumbnails.setSelected(false);
        }
        notifyChangeListener(new UserSettingsChangeEvent(UserSettingsChangeEvent.Type.IsCreateThumbnailsWithExternalApp, this));
        setExternalThumbnailAppEnabled();
    }

    private boolean canMoveDownOpenImageApp() {
        int selectedIndex = listOtherImageOpenApps.getSelectedIndex();
        int lastIndex = modelOtherImageOpenApps.getSize() - 1;
        return selectedIndex >= 0 && selectedIndex < lastIndex;
    }

    private void moveUpOpenImageApp() {
        if (canMoveUpOpenImageApp()) {
            int selectedIndex = listOtherImageOpenApps.getSelectedIndex();
            int newSelectedIndex = selectedIndex - 1;
            Object element = modelOtherImageOpenApps.get(selectedIndex);
            modelOtherImageOpenApps.remove(selectedIndex);
            modelOtherImageOpenApps.add(newSelectedIndex, element);
            listOtherImageOpenApps.setSelectedIndex(newSelectedIndex);
            setEnabled();
        }
    }

    private boolean canMoveUpOpenImageApp() {
        return listOtherImageOpenApps.getSelectedIndex() > 0;
    }

    private void setEnabled() {
        textFieldExternalThumbnailCreationCommand.setEnabled(checkBoxIsCreateThumbnailsWithExternalApp.isSelected());
        buttonMoveDownOtherImageOpenApp.setEnabled(canMoveDownOpenImageApp());
        buttonMoveUpOtherImageOpenApp.setEnabled(canMoveUpOpenImageApp());
        buttonRemoveOtherImageOpenApp.setEnabled(isOtherOpenImageAppSelected());
        buttonRemoveAutoscanDirectories.setEnabled(listAutoscanDirectories.getSelectedIndex() >= 0);
    }

    private boolean isOtherOpenImageAppSelected() {
        return listOtherImageOpenApps.getSelectedIndex() >= 0;
    }

    public static UserSettingsDialog getInstance() {
        return instance;
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

    private void setSearchColumns() {
        checkListSearchColumns.setModel(searchColumnsListModel);
        checkListSearchColumns.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        checkListSearchColumns.addActionListener(this);
    }

    private PersistentSettingsHints getPersistentSettingsHints() {
        PersistentSettingsHints hints = new PersistentSettingsHints();
        hints.setTabbedPaneContents(false);
        return hints;
    }

    private void readPersistent() {
        PersistentAppSizes.getSizeAndLocation(this);
        PersistentSettings.getInstance().getTabbedPane(tabbedPane, keyTabbedPaneIndex, getPersistentSettingsHints());
        checkLogLevel();
        setExternalThumbnailAppEnabled();
        previousDirectory = labelDefaultImageOpenApp.getText();
        lastSelectedAutoscanDirectory = PersistentSettings.getInstance().
            getString(keyLastSelectedAutoscanDirectory);

        readPersistentContent();
    }

    private void readPersistentContent() {
        UserSettings settings = UserSettings.getInstance();

        labelDefaultImageOpenApp.setText(settings.getDefaultImageOpenApp());
        File lastAcDirectory = settings.getAutocopyDirectory();
        if (lastAcDirectory != null && lastAcDirectory.exists()) {
            String lastAcDirectoryName = lastAcDirectory.getAbsolutePath();
            labelAutocopyDirectory.setText(lastAcDirectoryName);
            lastSelectedAutocopyDirectory = lastAcDirectoryName;
        }

        comboBoxIptcCharset.getModel().setSelectedItem(settings.getIptcCharset());
        comboBoxLogLevel.setSelectedItem(settings.getLogLevel().getLocalizedName());
        ComboBoxModelLogfileFormatter modelLogfileFormatter =
            (ComboBoxModelLogfileFormatter) comboBoxLogfileFormatterClass.getModel();
        modelLogfileFormatter.setSelectedItem(settings.getLogfileFormatterClass());

        ComboBoxModelThreadPriority modelThreadPriority =
            (ComboBoxModelThreadPriority) comboBoxThreadPriority.getModel();
        modelThreadPriority.setSelectedItem(modelThreadPriority.getItemOfPriority(
            settings.getThreadPriority()));

        checkListSearchColumns.setSelectedItemsWithText(getTextSelectedSearchColumns(), true);

        spinnerMaxThumbnailWidth.setValue(settings.getMaxThumbnailWidth());
        spinnerMinutesToStartScheduledTasks.setValue(settings.getMinutesToStartScheduledTasks());

        checkBoxIsAcceptHiddenDirectories.setSelected(settings.isAcceptHiddenDirectories());
        checkBoxIsAutocompleteDisabled.setSelected(!settings.isUseAutocomplete());
        checkBoxIsAutoscanIncludeSubdirectories.setSelected(settings.isAutoscanIncludeSubdirectories());
        checkBoxIsCreateThumbnailsWithExternalApp.setSelected(settings.isCreateThumbnailsWithExternalApp());
        checkBoxIsTaskRemoveRecordsWithNotExistingFiles.setSelected(settings.isTaskRemoveRecordsWithNotExistingFiles());
        checkBoxIsUseEmbeddedThumbnails.setSelected(settings.isUseEmbeddedThumbnails());

        textFieldExternalThumbnailCreationCommand.setText(settings.getExternalThumbnailCreationCommand());
    }

    private List<String> getTextSelectedSearchColumns() {
        List<String> text = new ArrayList<String>();
        List<Column> columns = UserSettings.getInstance().getFastSearchColumns();
        for (Column column : columns) {
            text.add(column.getDescription());
        }
        return text;
    }

    private void setDefaultOpenApp() {
        File file = chooseDirectory(labelDefaultImageOpenApp.getText());
        if (file != null && file.exists()) {
            labelDefaultImageOpenApp.setText(file.getAbsolutePath());
            notifyChangeListener(new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.DefaultImageOpenApp, this));
        }
    }

    private void addOtherOpenImageApp() {
        File file = chooseDirectory(previousDirectory);
        if (file != null && modelOtherImageOpenApps.add(file)) {
            setEnabled();
            notifyChangeListener(new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.OtherImageOpenApps, this));
        }
    }

    private void removeOtherOpenImageApp() {
        int index = listOtherImageOpenApps.getSelectedIndex();
        if (index >= 0 && askRemove(modelOtherImageOpenApps.getElementAt(index).toString()) && modelOtherImageOpenApps.remove(modelOtherImageOpenApps.get(index))) {
            listOtherImageOpenApps.setSelectedIndex(index);
            setEnabled();
            notifyChangeListener(new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.OtherImageOpenApps, this));
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

    private File chooseDirectory(String startDirectory) {
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
        PersistentSettings.getInstance().setString(
            lastSelectedAutoscanDirectory, keyLastSelectedAutoscanDirectory);
        PersistentSettings.getInstance().setTabbedPane(tabbedPane, keyTabbedPaneIndex, getPersistentSettingsHints());
        PersistentAppSizes.setSizeAndLocation(this);
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
        Object[] values = listAutoscanDirectories.getSelectedValues();
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
                ? UserSettingsChangeEvent.Type.FastSearchColumnDefined
                : UserSettingsChangeEvent.Type.NoFastSearchColumns, this));
        } else if (e.getSource() == thumbnailsUpdater) {
            buttonUpdateAllThumbnails.setEnabled(true);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            modelAutoscanDirectories = new ListModelAutoscanDirectories();
            listAutoscanDirectories.setModel(modelAutoscanDirectories);
        } else {
            writePersistent();
        }
        super.setVisible(visible);
    }

    private void setEnabledButtonRemoveAutoscanDirectory() {
        buttonRemoveAutoscanDirectories.setEnabled(
            listAutoscanDirectories.getSelectedIndices().length > 0);
    }

    private void setExternalThumbnailAppEnabled() {
        textFieldExternalThumbnailCreationCommand.setEnabled(
            UserSettings.getInstance().isCreateThumbnailsWithExternalApp());
    }

    @Override
    protected void help() {
        help(helpUrlOfComponent.get(tabbedPane.getSelectedComponent()));
    }

    @Override
    protected void escape() {
        setVisible(false);
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
        labelDefaultImageOpenAppPrompt = new javax.swing.JLabel();
        buttonDefaultImageOpenApp = new javax.swing.JButton();
        labelDefaultImageOpenApp = new javax.swing.JLabel();
        labelInfoOtherOpenImageApps = new javax.swing.JLabel();
        scrollPaneListOtherImageOpenApps = new javax.swing.JScrollPane();
        listOtherImageOpenApps = new javax.swing.JList();
        buttonRemoveOtherImageOpenApp = new javax.swing.JButton();
        buttonAddOtherImageOpenApp = new javax.swing.JButton();
        buttonMoveUpOtherImageOpenApp = new javax.swing.JButton();
        buttonMoveDownOtherImageOpenApp = new javax.swing.JButton();
        panelSearch = new javax.swing.JPanel();
        labelFastSearchColumns = new javax.swing.JLabel();
        scrollPaneFastSearchColumns = new JScrollPane(checkListSearchColumns);
        panelThumbnails = new javax.swing.JPanel();
        panelThumbnailDimensions = new javax.swing.JPanel();
        labelMaxThumbnailWidth = new javax.swing.JLabel();
        spinnerMaxThumbnailWidth = new javax.swing.JSpinner();
        buttonUpdateAllThumbnails = new javax.swing.JButton();
        labelUpdateAllThumbnails = new javax.swing.JLabel();
        checkBoxIsUseEmbeddedThumbnails = new javax.swing.JCheckBox();
        panelExternalThumbnailApp = new javax.swing.JPanel();
        checkBoxIsCreateThumbnailsWithExternalApp = new javax.swing.JCheckBox();
        labelIsCreateThumbnailsWithExternalApp = new javax.swing.JLabel();
        textFieldExternalThumbnailCreationCommand = new javax.swing.JTextField();
        panelIptc = new javax.swing.JPanel();
        labelIptcCharset = new javax.swing.JLabel();
        comboBoxIptcCharset = new javax.swing.JComboBox();
        panelTasks = new javax.swing.JPanel();
        panelTasksAutoscan = new javax.swing.JPanel();
        labelAutoscanDirectoriesInfo = new javax.swing.JLabel();
        labelAutoscanDirectoriesPrompt = new javax.swing.JLabel();
        scrollPaneListAutoscanDirectories = new javax.swing.JScrollPane();
        listAutoscanDirectories = new javax.swing.JList();
        checkBoxIsAutoscanIncludeSubdirectories = new javax.swing.JCheckBox();
        buttonRemoveAutoscanDirectories = new javax.swing.JButton();
        buttonAddAutoscanDirectories = new javax.swing.JButton();
        panelTasksOther = new javax.swing.JPanel();
        checkBoxIsTaskRemoveRecordsWithNotExistingFiles = new javax.swing.JCheckBox();
        labelTasksMinutesToStartScheduledTasks = new javax.swing.JLabel();
        spinnerMinutesToStartScheduledTasks = new javax.swing.JSpinner();
        panelPerformance = new javax.swing.JPanel();
        panelAccelerateStart = new javax.swing.JPanel();
        checkBoxIsAutocompleteDisabled = new javax.swing.JCheckBox();
        labelsAutocompleteDisabled = new javax.swing.JLabel();
        panelThreadPriority = new javax.swing.JPanel();
        labelThreadPriority = new javax.swing.JLabel();
        comboBoxThreadPriority = new javax.swing.JComboBox();
        labelInfoThreadPriority = new javax.swing.JLabel();
        panelFileExcludePatterns = new de.elmar_baumann.imv.view.panels.FileExcludePatternsPanel();
        panelOther = new javax.swing.JPanel();
        panelLogfile = new javax.swing.JPanel();
        labelLogLevel = new javax.swing.JLabel();
        comboBoxLogLevel = new javax.swing.JComboBox();
        labelLogLogfileFormatterClass = new javax.swing.JLabel();
        comboBoxLogfileFormatterClass = new javax.swing.JComboBox();
        panelAutoCopyDirectory = new javax.swing.JPanel();
        labelAutocopyDirectory = new javax.swing.JLabel();
        buttonChooseAutocopyDirectory = new javax.swing.JButton();
        checkBoxIsAcceptHiddenDirectories = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString("UserSettingsDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabbedPane.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        panelImageOpenApps.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelImageOpenApps.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelDefaultImageOpenAppPrompt.setFont(new java.awt.Font("Dialog", 0, 12));
        labelDefaultImageOpenAppPrompt.setText(Bundle.getString("UserSettingsDialog.labelDefaultImageOpenAppPrompt.text")); // NOI18N

        buttonDefaultImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonDefaultImageOpenApp.setMnemonic('a');
        buttonDefaultImageOpenApp.setText(Bundle.getString("UserSettingsDialog.buttonDefaultImageOpenApp.text")); // NOI18N
        buttonDefaultImageOpenApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDefaultImageOpenAppActionPerformed(evt);
            }
        });

        labelDefaultImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12));
        labelDefaultImageOpenApp.setForeground(new java.awt.Color(0, 0, 255));
        labelDefaultImageOpenApp.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelInfoOtherOpenImageApps.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfoOtherOpenImageApps.setText(Bundle.getString("UserSettingsDialog.labelInfoOtherOpenImageApps.text")); // NOI18N

        listOtherImageOpenApps.setModel(modelOtherImageOpenApps);
        listOtherImageOpenApps.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listOtherImageOpenApps.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listOtherImageOpenAppsValueChanged(evt);
            }
        });
        scrollPaneListOtherImageOpenApps.setViewportView(listOtherImageOpenApps);

        buttonRemoveOtherImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonRemoveOtherImageOpenApp.setMnemonic('e');
        buttonRemoveOtherImageOpenApp.setText(Bundle.getString("UserSettingsDialog.buttonRemoveOtherImageOpenApp.text")); // NOI18N
        buttonRemoveOtherImageOpenApp.setEnabled(false);
        buttonRemoveOtherImageOpenApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveOtherImageOpenAppActionPerformed(evt);
            }
        });

        buttonAddOtherImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonAddOtherImageOpenApp.setMnemonic('w');
        buttonAddOtherImageOpenApp.setText(Bundle.getString("UserSettingsDialog.buttonAddOtherImageOpenApp.text")); // NOI18N
        buttonAddOtherImageOpenApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddOtherImageOpenAppActionPerformed(evt);
            }
        });

        buttonMoveUpOtherImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonMoveUpOtherImageOpenApp.setMnemonic('o');
        buttonMoveUpOtherImageOpenApp.setText(Bundle.getString("UserSettingsDialog.buttonMoveUpOtherImageOpenApp.text")); // NOI18N
        buttonMoveUpOtherImageOpenApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveUpOtherImageOpenAppActionPerformed(evt);
            }
        });

        buttonMoveDownOtherImageOpenApp.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonMoveDownOtherImageOpenApp.setMnemonic('u');
        buttonMoveDownOtherImageOpenApp.setText(Bundle.getString("UserSettingsDialog.buttonMoveDownOtherImageOpenApp.text")); // NOI18N
        buttonMoveDownOtherImageOpenApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveDownOtherImageOpenAppActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelImageOpenAppsLayout = new javax.swing.GroupLayout(panelImageOpenApps);
        panelImageOpenApps.setLayout(panelImageOpenAppsLayout);
        panelImageOpenAppsLayout.setHorizontalGroup(
            panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDefaultImageOpenApp, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                    .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                        .addComponent(labelDefaultImageOpenAppPrompt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonDefaultImageOpenApp))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelImageOpenAppsLayout.createSequentialGroup()
                        .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                                .addComponent(buttonRemoveOtherImageOpenApp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonAddOtherImageOpenApp))
                            .addComponent(scrollPaneListOtherImageOpenApps, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonMoveDownOtherImageOpenApp)
                            .addComponent(buttonMoveUpOtherImageOpenApp)))
                    .addComponent(labelInfoOtherOpenImageApps))
                .addContainerGap())
        );

        panelImageOpenAppsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonMoveDownOtherImageOpenApp, buttonMoveUpOtherImageOpenApp});

        panelImageOpenAppsLayout.setVerticalGroup(
            panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDefaultImageOpenAppPrompt)
                    .addComponent(buttonDefaultImageOpenApp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDefaultImageOpenApp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoOtherOpenImageApps)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                        .addComponent(scrollPaneListOtherImageOpenApps, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelImageOpenAppsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonAddOtherImageOpenApp)
                            .addComponent(buttonRemoveOtherImageOpenApp)))
                    .addGroup(panelImageOpenAppsLayout.createSequentialGroup()
                        .addComponent(buttonMoveUpOtherImageOpenApp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonMoveDownOtherImageOpenApp)))
                .addContainerGap())
        );

        panelImageOpenAppsLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelDefaultImageOpenApp, labelDefaultImageOpenAppPrompt});

        panelImageOpenAppsLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonMoveDownOtherImageOpenApp, buttonMoveUpOtherImageOpenApp});

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

        labelFastSearchColumns.setFont(new java.awt.Font("Dialog", 0, 11));
        labelFastSearchColumns.setText(Bundle.getString("UserSettingsDialog.labelFastSearchColumns.text")); // NOI18N

        javax.swing.GroupLayout panelSearchLayout = new javax.swing.GroupLayout(panelSearch);
        panelSearch.setLayout(panelSearchLayout);
        panelSearchLayout.setHorizontalGroup(
            panelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelFastSearchColumns)
                .addContainerGap(310, Short.MAX_VALUE))
            .addGroup(panelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelSearchLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPaneFastSearchColumns, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        panelSearchLayout.setVerticalGroup(
            panelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelFastSearchColumns)
                .addContainerGap(291, Short.MAX_VALUE))
            .addGroup(panelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSearchLayout.createSequentialGroup()
                    .addGap(38, 38, 38)
                    .addComponent(scrollPaneFastSearchColumns, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelSearch.TabConstraints.tabTitle"), panelSearch); // NOI18N

        panelThumbnailDimensions.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelThumbnailDimensions.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelMaxThumbnailWidth.setFont(new java.awt.Font("Dialog", 0, 12));
        labelMaxThumbnailWidth.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelMaxThumbnailWidth.setText(Bundle.getString("UserSettingsDialog.labelMaxThumbnailWidth.text")); // NOI18N

        spinnerMaxThumbnailWidth.setFont(new java.awt.Font("Dialog", 0, 12));
        spinnerMaxThumbnailWidth.setModel(new SpinnerNumberModel(150, 50, 250, 1));
        spinnerMaxThumbnailWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMaxThumbnailWidthStateChanged(evt);
            }
        });

        buttonUpdateAllThumbnails.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonUpdateAllThumbnails.setMnemonic('n');
        buttonUpdateAllThumbnails.setText(Bundle.getString("UserSettingsDialog.buttonUpdateAllThumbnails.text")); // NOI18N
        buttonUpdateAllThumbnails.setToolTipText(Bundle.getString("UserSettingsDialog.buttonUpdateAllThumbnails.toolTipText")); // NOI18N
        buttonUpdateAllThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateAllThumbnailsActionPerformed(evt);
            }
        });

        labelUpdateAllThumbnails.setFont(new java.awt.Font("Dialog", 0, 12));
        labelUpdateAllThumbnails.setForeground(new java.awt.Color(255, 0, 0));
        labelUpdateAllThumbnails.setText(Bundle.getString("UserSettingsDialog.labelUpdateAllThumbnails.text")); // NOI18N

        javax.swing.GroupLayout panelThumbnailDimensionsLayout = new javax.swing.GroupLayout(panelThumbnailDimensions);
        panelThumbnailDimensions.setLayout(panelThumbnailDimensionsLayout);
        panelThumbnailDimensionsLayout.setHorizontalGroup(
            panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                        .addComponent(labelMaxThumbnailWidth)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerMaxThumbnailWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonUpdateAllThumbnails))
                    .addComponent(labelUpdateAllThumbnails, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelThumbnailDimensionsLayout.setVerticalGroup(
            panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMaxThumbnailWidth)
                    .addComponent(spinnerMaxThumbnailWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonUpdateAllThumbnails))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelUpdateAllThumbnails)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        checkBoxIsUseEmbeddedThumbnails.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxIsUseEmbeddedThumbnails.setText(Bundle.getString("UserSettingsDialog.checkBoxIsUseEmbeddedThumbnails.text")); // NOI18N
        checkBoxIsUseEmbeddedThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsUseEmbeddedThumbnailsActionPerformed(evt);
            }
        });

        panelExternalThumbnailApp.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelExternalThumbnailApp.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        checkBoxIsCreateThumbnailsWithExternalApp.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxIsCreateThumbnailsWithExternalApp.setText(Bundle.getString("UserSettingsDialog.checkBoxIsCreateThumbnailsWithExternalApp.text")); // NOI18N
        checkBoxIsCreateThumbnailsWithExternalApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsCreateThumbnailsWithExternalAppActionPerformed(evt);
            }
        });

        labelIsCreateThumbnailsWithExternalApp.setFont(new java.awt.Font("Dialog", 0, 12));
        labelIsCreateThumbnailsWithExternalApp.setText(Bundle.getString("UserSettingsDialog.labelIsCreateThumbnailsWithExternalApp.text")); // NOI18N

        textFieldExternalThumbnailCreationCommand.setEnabled(false);
        textFieldExternalThumbnailCreationCommand.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldExternalThumbnailCreationCommandKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panelExternalThumbnailAppLayout = new javax.swing.GroupLayout(panelExternalThumbnailApp);
        panelExternalThumbnailApp.setLayout(panelExternalThumbnailAppLayout);
        panelExternalThumbnailAppLayout.setHorizontalGroup(
            panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxIsCreateThumbnailsWithExternalApp)
                    .addComponent(labelIsCreateThumbnailsWithExternalApp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                    .addComponent(textFieldExternalThumbnailCreationCommand, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelExternalThumbnailAppLayout.setVerticalGroup(
            panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                .addComponent(checkBoxIsCreateThumbnailsWithExternalApp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelIsCreateThumbnailsWithExternalApp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldExternalThumbnailCreationCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(checkBoxIsUseEmbeddedThumbnails, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelThumbnailDimensions, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelThumbnailsLayout.setVerticalGroup(
            panelThumbnailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThumbnailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelThumbnailDimensions, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxIsUseEmbeddedThumbnails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelExternalThumbnailApp, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                .addGap(73, 73, 73))
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelThumbnails.TabConstraints.tabTitle"), panelThumbnails); // NOI18N

        labelIptcCharset.setFont(new java.awt.Font("Dialog", 0, 12));
        labelIptcCharset.setText(Bundle.getString("UserSettingsDialog.labelIptcCharset.text")); // NOI18N

        comboBoxIptcCharset.setFont(new java.awt.Font("Dialog", 0, 12));
        comboBoxIptcCharset.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ISO-8859-1", "UTF-8" }));
        comboBoxIptcCharset.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                comboBoxIptcCharsetPropertyChange(evt);
            }
        });

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
                .addContainerGap(281, Short.MAX_VALUE))
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelIptc.TabConstraints.tabTitle"), panelIptc); // NOI18N

        panelTasksAutoscan.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelTasksAutoscan.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelAutoscanDirectoriesInfo.setFont(new java.awt.Font("Dialog", 0, 12));
        labelAutoscanDirectoriesInfo.setText(Bundle.getString("UserSettingsDialog.labelAutoscanDirectoriesInfo.text")); // NOI18N

        labelAutoscanDirectoriesPrompt.setFont(new java.awt.Font("Dialog", 0, 12));
        labelAutoscanDirectoriesPrompt.setText(Bundle.getString("UserSettingsDialog.labelAutoscanDirectoriesPrompt.text")); // NOI18N

        listAutoscanDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        listAutoscanDirectories.setModel(modelAutoscanDirectories);
        listAutoscanDirectories.setCellRenderer(new ListCellRendererFileSystem(true));
        listAutoscanDirectories.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listAutoscanDirectoriesValueChanged(evt);
            }
        });
        listAutoscanDirectories.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                listAutoscanDirectoriesKeyReleased(evt);
            }
        });
        scrollPaneListAutoscanDirectories.setViewportView(listAutoscanDirectories);

        checkBoxIsAutoscanIncludeSubdirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxIsAutoscanIncludeSubdirectories.setText(Bundle.getString("UserSettingsDialog.checkBoxIsAutoscanIncludeSubdirectories.text")); // NOI18N
        checkBoxIsAutoscanIncludeSubdirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed(evt);
            }
        });

        buttonRemoveAutoscanDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonRemoveAutoscanDirectories.setMnemonic('e');
        buttonRemoveAutoscanDirectories.setText(Bundle.getString("UserSettingsDialog.buttonRemoveAutoscanDirectories.text")); // NOI18N
        buttonRemoveAutoscanDirectories.setEnabled(false);
        buttonRemoveAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveAutoscanDirectoriesActionPerformed(evt);
            }
        });

        buttonAddAutoscanDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonAddAutoscanDirectories.setMnemonic('h');
        buttonAddAutoscanDirectories.setText(Bundle.getString("UserSettingsDialog.buttonAddAutoscanDirectories.text")); // NOI18N
        buttonAddAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddAutoscanDirectoriesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTasksAutoscanLayout = new javax.swing.GroupLayout(panelTasksAutoscan);
        panelTasksAutoscan.setLayout(panelTasksAutoscanLayout);
        panelTasksAutoscanLayout.setHorizontalGroup(
            panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTasksAutoscanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPaneListAutoscanDirectories, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTasksAutoscanLayout.createSequentialGroup()
                        .addGroup(panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelTasksAutoscanLayout.createSequentialGroup()
                                .addComponent(checkBoxIsAutoscanIncludeSubdirectories)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE))
                            .addGroup(panelTasksAutoscanLayout.createSequentialGroup()
                                .addComponent(buttonRemoveAutoscanDirectories)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(buttonAddAutoscanDirectories))
                    .addComponent(labelAutoscanDirectoriesInfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                    .addComponent(labelAutoscanDirectoriesPrompt, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        panelTasksAutoscanLayout.setVerticalGroup(
            panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTasksAutoscanLayout.createSequentialGroup()
                .addComponent(labelAutoscanDirectoriesInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelAutoscanDirectoriesPrompt, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneListAutoscanDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxIsAutoscanIncludeSubdirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAddAutoscanDirectories)
                    .addComponent(buttonRemoveAutoscanDirectories))
                .addContainerGap())
        );

        panelTasksOther.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelTasksOther.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        checkBoxIsTaskRemoveRecordsWithNotExistingFiles.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxIsTaskRemoveRecordsWithNotExistingFiles.setText(Bundle.getString("UserSettingsDialog.checkBoxIsTaskRemoveRecordsWithNotExistingFiles.text")); // NOI18N
        checkBoxIsTaskRemoveRecordsWithNotExistingFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsTaskRemoveRecordsWithNotExistingFilesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTasksOtherLayout = new javax.swing.GroupLayout(panelTasksOther);
        panelTasksOther.setLayout(panelTasksOtherLayout);
        panelTasksOtherLayout.setHorizontalGroup(
            panelTasksOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTasksOtherLayout.createSequentialGroup()
                .addComponent(checkBoxIsTaskRemoveRecordsWithNotExistingFiles)
                .addContainerGap(103, Short.MAX_VALUE))
        );
        panelTasksOtherLayout.setVerticalGroup(
            panelTasksOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTasksOtherLayout.createSequentialGroup()
                .addComponent(checkBoxIsTaskRemoveRecordsWithNotExistingFiles)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        labelTasksMinutesToStartScheduledTasks.setFont(new java.awt.Font("Dialog", 0, 12));
        labelTasksMinutesToStartScheduledTasks.setText(Bundle.getString("UserSettingsDialog.labelTasksMinutesToStartScheduledTasks.text")); // NOI18N

        spinnerMinutesToStartScheduledTasks.setModel(new SpinnerNumberModel(5, 1, 6000, 1));
        spinnerMinutesToStartScheduledTasks.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMinutesToStartScheduledTasksStateChanged(evt);
            }
        });

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
                        .addComponent(spinnerMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(spinnerMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelTasks.TabConstraints.tabTitle"), panelTasks); // NOI18N

        panelAccelerateStart.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelAccelerateStart.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        checkBoxIsAutocompleteDisabled.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxIsAutocompleteDisabled.setText(Bundle.getString("UserSettingsDialog.checkBoxIsAutocompleteDisabled.text")); // NOI18N
        checkBoxIsAutocompleteDisabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAutocompleteDisabledActionPerformed(evt);
            }
        });

        labelsAutocompleteDisabled.setFont(new java.awt.Font("Dialog", 0, 12));
        labelsAutocompleteDisabled.setForeground(new java.awt.Color(255, 0, 0));
        labelsAutocompleteDisabled.setText(Bundle.getString("UserSettingsDialog.labelsAutocompleteDisabled.text")); // NOI18N

        javax.swing.GroupLayout panelAccelerateStartLayout = new javax.swing.GroupLayout(panelAccelerateStart);
        panelAccelerateStart.setLayout(panelAccelerateStartLayout);
        panelAccelerateStartLayout.setHorizontalGroup(
            panelAccelerateStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAccelerateStartLayout.createSequentialGroup()
                .addGroup(panelAccelerateStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxIsAutocompleteDisabled)
                    .addGroup(panelAccelerateStartLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelsAutocompleteDisabled)))
                .addContainerGap(132, Short.MAX_VALUE))
        );
        panelAccelerateStartLayout.setVerticalGroup(
            panelAccelerateStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAccelerateStartLayout.createSequentialGroup()
                .addComponent(checkBoxIsAutocompleteDisabled)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelsAutocompleteDisabled)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        labelInfoThreadPriority.setFont(new java.awt.Font("Dialog", 0, 12));
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
                .addContainerGap(190, Short.MAX_VALUE))
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
                    .addComponent(panelAccelerateStart, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelThreadPriority, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPerformanceLayout.setVerticalGroup(
            panelPerformanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPerformanceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelAccelerateStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelThreadPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(142, Short.MAX_VALUE))
        );

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelPerformance.TabConstraints.tabTitle"), panelPerformance); // NOI18N
        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelFileExcludePatterns.TabConstraints.tabTitle"), panelFileExcludePatterns); // NOI18N

        panelLogfile.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelLogfile.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelLogLevel.setFont(new java.awt.Font("Dialog", 0, 12));
        labelLogLevel.setText(Bundle.getString("UserSettingsDialog.labelLogLevel.text")); // NOI18N

        comboBoxLogLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { java.util.logging.Level.WARNING.getLocalizedName(), java.util.logging.Level.SEVERE.getLocalizedName(), java.util.logging.Level.INFO.getLocalizedName(), java.util.logging.Level.CONFIG.getLocalizedName(), java.util.logging.Level.FINE.getLocalizedName(), java.util.logging.Level.FINER.getLocalizedName(), java.util.logging.Level.FINEST.getLocalizedName() }));
        comboBoxLogLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxLogLevelActionPerformed(evt);
            }
        });

        labelLogLogfileFormatterClass.setFont(new java.awt.Font("Dialog", 0, 12));
        labelLogLogfileFormatterClass.setText(Bundle.getString("UserSettingsDialog.labelLogLogfileFormatterClass.text")); // NOI18N

        comboBoxLogfileFormatterClass.setModel(new ComboBoxModelLogfileFormatter());
        comboBoxLogfileFormatterClass.setRenderer(new ListCellRendererLogfileFormatter());
        comboBoxLogfileFormatterClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxLogfileFormatterClassActionPerformed(evt);
            }
        });

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
                .addComponent(labelLogLogfileFormatterClass)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxLogfileFormatterClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(167, Short.MAX_VALUE))
        );
        panelLogfileLayout.setVerticalGroup(
            panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogfileLayout.createSequentialGroup()
                .addGroup(panelLogfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLogLevel)
                    .addComponent(labelLogLogfileFormatterClass)
                    .addComponent(comboBoxLogfileFormatterClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelAutoCopyDirectory.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("UserSettingsDialog.panelAutoCopyDirectory.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelAutocopyDirectory.setFont(new java.awt.Font("Dialog", 0, 10));
        labelAutocopyDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelAutocopyDirectory.setText(Bundle.getString("UserSettingsDialog.labelAutocopyDirectory.text")); // NOI18N
        labelAutocopyDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseAutocopyDirectory.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonChooseAutocopyDirectory.setMnemonic('a');
        buttonChooseAutocopyDirectory.setText(Bundle.getString("UserSettingsDialog.buttonChooseAutocopyDirectory.text")); // NOI18N
        buttonChooseAutocopyDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseAutocopyDirectoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelAutoCopyDirectoryLayout = new javax.swing.GroupLayout(panelAutoCopyDirectory);
        panelAutoCopyDirectory.setLayout(panelAutoCopyDirectoryLayout);
        panelAutoCopyDirectoryLayout.setHorizontalGroup(
            panelAutoCopyDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAutoCopyDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAutoCopyDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAutocopyDirectory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                    .addComponent(buttonChooseAutocopyDirectory, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        panelAutoCopyDirectoryLayout.setVerticalGroup(
            panelAutoCopyDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAutoCopyDirectoryLayout.createSequentialGroup()
                .addComponent(labelAutocopyDirectory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonChooseAutocopyDirectory)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        checkBoxIsAcceptHiddenDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxIsAcceptHiddenDirectories.setText(Bundle.getString("UserSettingsDialog.checkBoxIsAcceptHiddenDirectories.text")); // NOI18N
        checkBoxIsAcceptHiddenDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAcceptHiddenDirectoriesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelOtherLayout = new javax.swing.GroupLayout(panelOther);
        panelOther.setLayout(panelOtherLayout);
        panelOtherLayout.setHorizontalGroup(
            panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxIsAcceptHiddenDirectories)
                    .addComponent(panelAutoCopyDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelLogfile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelOtherLayout.setVerticalGroup(
            panelOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelLogfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelAutoCopyDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxIsAcceptHiddenDirectories)
                .addContainerGap(137, Short.MAX_VALUE))
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
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonDefaultImageOpenAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDefaultImageOpenAppActionPerformed
    setDefaultOpenApp();
}//GEN-LAST:event_buttonDefaultImageOpenAppActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    writePersistent();
}//GEN-LAST:event_formWindowClosing

private void checkBoxIsCreateThumbnailsWithExternalAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsCreateThumbnailsWithExternalAppActionPerformed
    handleActionCheckBoxExternalThumbnail();
}//GEN-LAST:event_checkBoxIsCreateThumbnailsWithExternalAppActionPerformed

private void buttonAddOtherImageOpenAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddOtherImageOpenAppActionPerformed
    addOtherOpenImageApp();
}//GEN-LAST:event_buttonAddOtherImageOpenAppActionPerformed

private void buttonMoveUpOtherImageOpenAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMoveUpOtherImageOpenAppActionPerformed
    moveUpOpenImageApp();
}//GEN-LAST:event_buttonMoveUpOtherImageOpenAppActionPerformed

private void buttonMoveDownOtherImageOpenAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMoveDownOtherImageOpenAppActionPerformed
    moveDownOpenImageApp();
}//GEN-LAST:event_buttonMoveDownOtherImageOpenAppActionPerformed

private void buttonRemoveOtherImageOpenAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveOtherImageOpenAppActionPerformed
    removeOtherOpenImageApp();
}//GEN-LAST:event_buttonRemoveOtherImageOpenAppActionPerformed

private void listOtherImageOpenAppsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listOtherImageOpenAppsValueChanged
    setEnabled();
}//GEN-LAST:event_listOtherImageOpenAppsValueChanged

private void listAutoscanDirectoriesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listAutoscanDirectoriesKeyReleased
    handleKeyEventListTasksAutoscanDirectories(evt);
}//GEN-LAST:event_listAutoscanDirectoriesKeyReleased

private void buttonAddAutoscanDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddAutoscanDirectoriesActionPerformed
    addAutoscanDirectories();
}//GEN-LAST:event_buttonAddAutoscanDirectoriesActionPerformed

private void buttonRemoveAutoscanDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveAutoscanDirectoriesActionPerformed
    removeSelectedAutoscanDirectories();
}//GEN-LAST:event_buttonRemoveAutoscanDirectoriesActionPerformed

private void comboBoxThreadPriorityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxThreadPriorityActionPerformed
    handleActionComboBoxThreadPriorityAction();
}//GEN-LAST:event_comboBoxThreadPriorityActionPerformed

private void listAutoscanDirectoriesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listAutoscanDirectoriesValueChanged
    setEnabledButtonRemoveAutoscanDirectory();
}//GEN-LAST:event_listAutoscanDirectoriesValueChanged

private void spinnerMaxThumbnailWidthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMaxThumbnailWidthStateChanged
    handleStateChangedSpinnerMaxThumbnailWidth();
}//GEN-LAST:event_spinnerMaxThumbnailWidthStateChanged

private void checkBoxIsUseEmbeddedThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsUseEmbeddedThumbnailsActionPerformed
    handleActionCheckBoxUseEmbeddedThumbnails();
}//GEN-LAST:event_checkBoxIsUseEmbeddedThumbnailsActionPerformed

private void buttonUpdateAllThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateAllThumbnailsActionPerformed
    updateAllThumbnails();
}//GEN-LAST:event_buttonUpdateAllThumbnailsActionPerformed

private void textFieldExternalThumbnailCreationCommandKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldExternalThumbnailCreationCommandKeyReleased
    handleKeyEventTextFieldExternalThumbnailCreationCommand();
}//GEN-LAST:event_textFieldExternalThumbnailCreationCommandKeyReleased

private void comboBoxIptcCharsetPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_comboBoxIptcCharsetPropertyChange
    handleActionComboBoxIptcCharset();
}//GEN-LAST:event_comboBoxIptcCharsetPropertyChange

private void checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed
    handleActionCheckBoxIsAutoscanIncludeSubdirectories();
}//GEN-LAST:event_checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed

private void checkBoxIsTaskRemoveRecordsWithNotExistingFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsTaskRemoveRecordsWithNotExistingFilesActionPerformed
    handleActionCheckBoxIsTaskRemoveRecordsWithNotExistingFiles();
}//GEN-LAST:event_checkBoxIsTaskRemoveRecordsWithNotExistingFilesActionPerformed

private void spinnerMinutesToStartScheduledTasksStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMinutesToStartScheduledTasksStateChanged
    handleStateChangedSpinnerMinutesToStartScheduledTasks();
}//GEN-LAST:event_spinnerMinutesToStartScheduledTasksStateChanged

private void checkBoxIsAutocompleteDisabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsAutocompleteDisabledActionPerformed
    handleActionPerformedCheckBoxIsAutocompleteDisabled();
}//GEN-LAST:event_checkBoxIsAutocompleteDisabledActionPerformed

private void comboBoxLogLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxLogLevelActionPerformed
    handleActionPerformedComboBoxLogLevel();
}//GEN-LAST:event_comboBoxLogLevelActionPerformed

private void comboBoxLogfileFormatterClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxLogfileFormatterClassActionPerformed
    handleActionPerformedComboBoxLogfileFormatterClass();
}//GEN-LAST:event_comboBoxLogfileFormatterClassActionPerformed

private void checkBoxIsAcceptHiddenDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsAcceptHiddenDirectoriesActionPerformed
    handleActionPerformedCheckBoxIsAcceptHiddenDirectories();
}//GEN-LAST:event_checkBoxIsAcceptHiddenDirectoriesActionPerformed

private void buttonChooseAutocopyDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseAutocopyDirectoryActionPerformed
    chooseAutocopyDirectory();
}//GEN-LAST:event_buttonChooseAutocopyDirectoryActionPerformed

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
    private javax.swing.JButton buttonAddAutoscanDirectories;
    private javax.swing.JButton buttonAddOtherImageOpenApp;
    private javax.swing.JButton buttonChooseAutocopyDirectory;
    private javax.swing.JButton buttonDefaultImageOpenApp;
    private javax.swing.JButton buttonMoveDownOtherImageOpenApp;
    private javax.swing.JButton buttonMoveUpOtherImageOpenApp;
    private javax.swing.JButton buttonRemoveAutoscanDirectories;
    private javax.swing.JButton buttonRemoveOtherImageOpenApp;
    private javax.swing.JButton buttonUpdateAllThumbnails;
    public javax.swing.JCheckBox checkBoxIsAcceptHiddenDirectories;
    public javax.swing.JCheckBox checkBoxIsAutocompleteDisabled;
    public javax.swing.JCheckBox checkBoxIsAutoscanIncludeSubdirectories;
    public javax.swing.JCheckBox checkBoxIsCreateThumbnailsWithExternalApp;
    public javax.swing.JCheckBox checkBoxIsTaskRemoveRecordsWithNotExistingFiles;
    public javax.swing.JCheckBox checkBoxIsUseEmbeddedThumbnails;
    public javax.swing.JComboBox comboBoxIptcCharset;
    public javax.swing.JComboBox comboBoxLogLevel;
    public javax.swing.JComboBox comboBoxLogfileFormatterClass;
    public javax.swing.JComboBox comboBoxThreadPriority;
    public javax.swing.JLabel labelAutocopyDirectory;
    private javax.swing.JLabel labelAutoscanDirectoriesInfo;
    private javax.swing.JLabel labelAutoscanDirectoriesPrompt;
    public javax.swing.JLabel labelDefaultImageOpenApp;
    private javax.swing.JLabel labelDefaultImageOpenAppPrompt;
    private javax.swing.JLabel labelFastSearchColumns;
    private javax.swing.JLabel labelInfoOtherOpenImageApps;
    private javax.swing.JLabel labelInfoThreadPriority;
    private javax.swing.JLabel labelIptcCharset;
    private javax.swing.JLabel labelIsCreateThumbnailsWithExternalApp;
    private javax.swing.JLabel labelLogLevel;
    private javax.swing.JLabel labelLogLogfileFormatterClass;
    private javax.swing.JLabel labelMaxThumbnailWidth;
    private javax.swing.JLabel labelTasksMinutesToStartScheduledTasks;
    private javax.swing.JLabel labelThreadPriority;
    private javax.swing.JLabel labelUpdateAllThumbnails;
    private javax.swing.JLabel labelsAutocompleteDisabled;
    private javax.swing.JList listAutoscanDirectories;
    public javax.swing.JList listOtherImageOpenApps;
    private javax.swing.JPanel panelAccelerateStart;
    private javax.swing.JPanel panelAutoCopyDirectory;
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
    private javax.swing.JScrollPane scrollPaneFastSearchColumns;
    private javax.swing.JScrollPane scrollPaneListAutoscanDirectories;
    private javax.swing.JScrollPane scrollPaneListOtherImageOpenApps;
    public javax.swing.JSpinner spinnerMaxThumbnailWidth;
    public javax.swing.JSpinner spinnerMinutesToStartScheduledTasks;
    private javax.swing.JTabbedPane tabbedPane;
    public javax.swing.JTextField textFieldExternalThumbnailCreationCommand;
    // End of variables declaration//GEN-END:variables
}
