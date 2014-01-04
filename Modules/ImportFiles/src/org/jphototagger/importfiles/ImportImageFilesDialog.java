package org.jphototagger.importfiles;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import org.jphototagger.api.applifecycle.generics.Functor;
import org.jphototagger.api.file.FileRenameStrategy;
import org.jphototagger.api.file.SubdirectoryCreateStrategy;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.editors.RenameTemplatesEditor;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpEditor;
import org.jphototagger.importfiles.filerenamers.FileRenameStrategyComboBoxModel;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.PreviousSelectedFiles;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.DirectoryChooser.Option;
import org.jphototagger.lib.swing.DisplayNameListCellRenderer;
import org.jphototagger.lib.swing.FileChooserHelper;
import org.jphototagger.lib.swing.FileChooserProperties;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComboBoxUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class ImportImageFilesDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private static final Icon ERROR_ICON = IconUtil.getImageIcon(ImportImageFilesDialog.class, "error.png");
    private static final String KEY_LAST_SRC_DIR = "ImportImageFiles.LastSrcDir";
    private static final String KEY_LAST_TARGET_DIR = "ImportImageFiles.LastTargetDir";
    private static final String KEY_DEL_SRC_AFTER_COPY = "ImportImageFiles.DelSrcAfterCopy";
    private static final String KEY_LAST_SCRIPT_DIR = "ImportImageFiles.LastScriptDir";
    private static final String KEY_SOURCE_STRATEGY = "ImportImageFiles.SourceStrategy";
    private static final String KEY_SCRIPT_FILE = "ImportImageFiles.LastScriptFile";
    private static final String KEY_SUBDIRECTORY_CREATE_STRATEGY = "ImportImageFiles.SubdirectoryCreateStrategy";
    private static final String KEY_FILE_RENAME_STRATEGY = "ImportImageFiles.RenameStrategy";
    private static final String KEY_SKIP_DUPLICATES = "ImportImageFiles.SkipDuplicates";
    private static final Color LABEL_FOREGROUND = getLabelForeground();
    private final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private final PreviousSelectedFiles prevSelectedSourceDirs = new PreviousSelectedFiles("IportImageFilesDialog.SourceDirs", 10);
    private final PreviousSelectedFiles prevSelectedTargetDirs = new PreviousSelectedFiles("IportImageFilesDialog.TargetDirs", 10);
    private File sourceDirectory = new File(prefs.getString(KEY_LAST_SRC_DIR));
    private File targetDirectory = new File(prefs.getString(KEY_LAST_TARGET_DIR));
    private File scriptFile;
    private Xmp xmp;
    private String lastChoosenScriptDir = "";
    private final List<File> sourceFiles = new ArrayList<>();
    private final Map<Integer, Component> panelOfSourceStrategyCbIndex = new HashMap<>();
    private Component currentPanelOfSourceStrategy;
    private boolean filesChoosed;
    private boolean accepted;
    private boolean deleteSourceFilesAfterCopying;
    private boolean listenToCheckBox = true;

    public ImportImageFilesDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        MnemonicUtil.setMnemonics(panelSourceDirectory);
        MnemonicUtil.setMnemonics(panelSelectedFiles);
        MnemonicUtil.setMnemonics(dialogExpertSettings);
        MnemonicUtil.setMnemonics(this);
        initSourceStrategyComboBox();
        initDirectories();
        lookupPersistedScriptFile();
        lookupPersistedLastChoosenScriptDir();
        lookupSubdirectoryCreateStrategy();
        lookupFileRenameStrategy();
        lookupSkipDuplicates();
    }

    private void initDirectories() {
        if (sourceDirectory.isDirectory()) {
            setDirLabel(labelSourceDir, sourceDirectory);
        } else {
            labelSourceDir.setIcon(ERROR_ICON);
        }
        if (targetDirectory.isDirectory()) {
            setDirLabel(labelTargetDir, targetDirectory);
        } else {
            labelTargetDir.setIcon(ERROR_ICON);
        }
        buttonOk.setEnabled(dirsValid());
        initDeleteSourceFilesAfterCopying();
    }

    private void initSourceStrategyComboBox() {
        panelOfSourceStrategyCbIndex.put(0, panelSourceDirectory);
        panelOfSourceStrategyCbIndex.put(1, panelSelectedFiles);
        if (prefs.containsKey(KEY_SOURCE_STRATEGY)) {
            int index = prefs.getInt(KEY_SOURCE_STRATEGY);
            if (index >= 0 && index < comboBoxSourceStrategy.getItemCount()) {
                comboBoxSourceStrategy.setSelectedIndex(index);
            }
        } else {
            comboBoxSourceStrategy.setSelectedIndex(0);
        }
    }

    private void setSourceStrategy() {
        int index = comboBoxSourceStrategy.getSelectedIndex();
        Component c = panelOfSourceStrategyCbIndex.get(index);
        if (c == currentPanelOfSourceStrategy) {
            return;
        }
        if (currentPanelOfSourceStrategy != null) {
            panelSourceStrategy.remove(currentPanelOfSourceStrategy);
        }
        currentPanelOfSourceStrategy = c;
        panelSourceStrategy.add(currentPanelOfSourceStrategy, getSourceStrategyGbc());
        pack();
        ComponentUtil.forceRepaint(this);
        prefs.setInt(KEY_SOURCE_STRATEGY, index);
    }

    private GridBagConstraints getSourceStrategyGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private static Color getLabelForeground() {
        Color foreground = UIManager.getColor("Label.foreground");
        return foreground == null ? Color.BLACK : foreground;
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(ImportImageFilesDialog.class, "ImportImageFilesDialog.HelpPage"));
    }

    private void initDeleteSourceFilesAfterCopying() {
        listenToCheckBox = false;
        checkBoxDeleteAfterCopy.setSelected(prefs.getBoolean(KEY_DEL_SRC_AFTER_COPY));
        deleteSourceFilesAfterCopying = checkBoxDeleteAfterCopy.isSelected();
        listenToCheckBox = true;
    }

    public boolean isDeleteSourceFilesAfterCopying() {
        return deleteSourceFilesAfterCopying;
    }

    public void setSourceDirectory(File dir) {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }
        sourceDirectory = dir;
        initDirectories();
        comboBoxSourceStrategy.setSelectedIndex(0);
    }

    public void setTargetDirectory(File dir) {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }
        targetDirectory = dir;
        initDirectories();
        comboBoxSourceStrategy.setSelectedIndex(1);
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public File getTargetDirectory() {
        return targetDirectory;
    }

    public boolean isAccepted() {
        return accepted;
    }

    private void setAccepted(boolean accepted) {
        this.accepted = accepted;
        setVisible(false);
    }

    private void chooseSourceDir() {
        File dir = chooseDir(sourceDirectory);
        if (dir != null) {
            setSourceDir(dir);
        }
    }

    private void setSourceDir(File dir) {
        boolean sourceDirNotEqualsTargetDir = !targetDirectory.exists() || checkDirsDifferent(dir, targetDirectory);
        if (sourceDirNotEqualsTargetDir) {
            sourceDirectory = dir;
            filesChoosed = false;
            sourceFiles.clear();
            persistFilePath(KEY_LAST_SRC_DIR, dir);
            prevSelectedSourceDirs.add(dir);
            resetLabelChoosedFiles();
            setDirLabel(labelSourceDir, dir);
        }
        setEnabledOkButton();
    }

    private void chooseSourceFiles() {
        JFileChooser fileChooser = new JFileChooser(sourceDirectory);
        ImagePreviewPanel imgPanel = new ImagePreviewPanel();
        fileChooser.setAccessory(imgPanel);
        fileChooser.addPropertyChangeListener(imgPanel);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(imgPanel.getFileFilter());
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            sourceFiles.clear();
            File[] selFiles = fileChooser.getSelectedFiles();
            if ((selFiles == null) || (selFiles.length < 1)) {
                return;
            }
            sourceFiles.addAll(Arrays.asList(selFiles));
            sourceDirectory = selFiles[0].getParentFile();
            persistFilePath(KEY_LAST_SRC_DIR, sourceDirectory);
            filesChoosed = true;
            resetLabelSourceDir();
            setFileLabel(selFiles[0], selFiles.length > 1);
        }
        setEnabledOkButton();
    }

    public List<File> getSourceFiles() {
        if (filesChoosed) {
            return new ArrayList<>(sourceFiles);
        } else {
            List<File> sourceDirectoriesRecursive = new LinkedList<>();
            sourceDirectoriesRecursive.add(sourceDirectory);
            sourceDirectoriesRecursive.addAll(FileUtil.getSubDirectoriesRecursive(sourceDirectory, null));
            return FileFilterUtil.getImageFilesOfDirectories(sourceDirectoriesRecursive);
        }
    }

    public ImportData createImportData() {
        return new ImportData.Builder(getSourceFiles(), targetDirectory)
                .deleteSourceFilesAfterCopying(deleteSourceFilesAfterCopying)
                .fileRenameStrategy(getFileRenameStrategy())
                .subdirectoryCreateStrategy(getSubdirectoryCreateStrategy())
                .scriptFile(scriptFile)
                .xmp(xmp)
                .skipDuplicates(checkBoxSkipDuplicates.isSelected())
                .build();
    }

    /**
     * Returns, whether the user choosed files rather than a source directory.
     * <p>
     * In this case, {@code #getSourceFiles()} return the choosen files.
     *
     * @return true if files choosen
     */
    public boolean filesChoosed() {
        return filesChoosed;
    }

    private void chooseTargetDir() {
        File dir = chooseDir(targetDirectory);
        if (dir != null) {
            setTargetDir(dir);
        }
    }

    private void setTargetDir(File dir) {
        boolean targetDirNotEqualsSourceDir = !sourceDirectory.exists() || checkDirsDifferent(sourceDirectory, dir);
        if (targetDirNotEqualsSourceDir) {
            targetDirectory = dir;
            persistFilePath(KEY_LAST_TARGET_DIR, dir);
            prevSelectedTargetDirs.add(dir);
            setDirLabel(labelTargetDir, dir);
        }
        setEnabledOkButton();
    }

    private final Action chooseSourceDirAction = new AbstractAction() {

        private static final long serialVersionUID = 1L;

        {
            putValue(Action.NAME, Bundle.getString(ImportImageFilesDialog.class, "ImportImageFilesDialog.ChooseSourceDirAction.Name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            chooseSourceDir();
        }
    };

    private final Action chooseTargetDirAction = new AbstractAction() {

        private static final long serialVersionUID = 1L;

        {
            putValue(Action.NAME, Bundle.getString(ImportImageFilesDialog.class, "ImportImageFilesDialog.ChooseTargetDirAction.Name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            chooseTargetDir();
        }
    };

    private final Functor<File> setSourceDirFunctor = new Functor<File>()  {
        @Override
        public void execute(File file) {
            if (file.isDirectory()) {
                setSourceDir(file);
            }
        }
    };

    private final Functor<File> setTargetDirFunctor = new Functor<File>()  {
        @Override
        public void execute(File file) {
            if (file.isDirectory()) {
                setTargetDir(file);
            }
        }
    };

    private void setDir(AbstractButton button, Action chooseAction, PreviousSelectedFiles psf,  Functor<File> chooseFunctor) {
        JPopupMenu popupMenu = psf.createPopupMenu(chooseFunctor, sourceDirectory, targetDirectory);
        if (popupMenu.getComponentCount() < 1) {
            chooseAction.actionPerformed(null);
        } else {
            popupMenu.add(chooseAction);
            popupMenu.show(button, 0, button.getHeight());
        }
    }

    private void lookupPersistedScriptFile() {
        if (prefs.containsKey(KEY_SCRIPT_FILE)) {
            String scriptFilePath = prefs.getString(KEY_SCRIPT_FILE);
            File persistedScriptFile = new File(scriptFilePath);
            scriptFileLabel.setText(scriptFilePath);
            boolean scriptFileExists = persistedScriptFile.isFile();
            if (scriptFileExists) {
                scriptFile = persistedScriptFile;
                buttonRemoveScriptFile.setEnabled(true);
            }
            scriptFileLabel.setIcon(scriptFileExists
                    ? fileSystemView.getSystemIcon(scriptFile)
                    : ERROR_ICON);
            scriptFileLabel.setForeground(scriptFileExists ? LABEL_FOREGROUND : Color.RED);
        }
    }

    private void chooseAndSetScriptFile() {
        File choosenScriptFile = chooseScriptFile();
        if (choosenScriptFile == null) {
            return;
        }
        scriptFile = choosenScriptFile;
        String scriptFilePath = scriptFile.getAbsolutePath();
        persistScriptFile(scriptFilePath);
        scriptFileLabel.setText(scriptFilePath);
        scriptFileLabel.setIcon(fileSystemView.getSystemIcon(scriptFile));
        buttonRemoveScriptFile.setEnabled(true);
    }

    private void persistScriptFile(String scriptFilePath) {
        prefs.setString(KEY_SCRIPT_FILE, scriptFilePath);
    }

    private File chooseScriptFile() {
        File choosenScriptFile = FileChooserHelper.chooseFile(createFileChooserPropertiesForScriptFile());
        if (choosenScriptFile != null) {
            String dirOfScriptFile = choosenScriptFile.getParent();
            if (dirOfScriptFile != null) {
                persistLastChoosenScriptDir(dirOfScriptFile);
            }
        }
        return choosenScriptFile;
    }

    private FileChooserProperties createFileChooserPropertiesForScriptFile() {
        FileChooserProperties props = new FileChooserProperties();
        props.currentDirectoryPath(lastChoosenScriptDir);
        props.dialogTitle(Bundle.getString(ImportImageFilesDialog.class, "ImportImageFilesDialog.ChooseScriptFile.FileChooser.Title"));
        props.multiSelectionEnabled(false);
        props.fileSelectionMode(JFileChooser.FILES_ONLY);
        props.propertyKeyPrefix("ImportImageFilesDialog.ChooseScriptFile.FileChooser");
        return props;
    }

    private void persistLastChoosenScriptDir(String scriptDir) {
        lastChoosenScriptDir = scriptDir;
        prefs.setString(KEY_LAST_SCRIPT_DIR, lastChoosenScriptDir);
    }

    private void lookupPersistedLastChoosenScriptDir() {
        if (prefs.containsKey(KEY_LAST_SCRIPT_DIR)) {
            lastChoosenScriptDir = prefs.getString(KEY_LAST_SCRIPT_DIR);
        }
    }

    private void lookupSubdirectoryCreateStrategy() {
        if (prefs.containsKey(KEY_SUBDIRECTORY_CREATE_STRATEGY)) {
            int index = prefs.getInt(KEY_SUBDIRECTORY_CREATE_STRATEGY);
            if (index >= 0 && index < comboBoxSubdirectoryCreateStrategy.getItemCount()) {
                comboBoxSubdirectoryCreateStrategy.setSelectedIndex(index);
            }
        }
    }

    private void persistSubdirectoryCreateStrategy() {
        int selectedIndex = comboBoxSubdirectoryCreateStrategy.getSelectedIndex();
        prefs.setInt(KEY_SUBDIRECTORY_CREATE_STRATEGY, selectedIndex);
    }

    private void lookupFileRenameStrategy() {
        if (prefs.containsKey(KEY_FILE_RENAME_STRATEGY)) {
            int index = prefs.getInt(KEY_FILE_RENAME_STRATEGY);
            if (index >= 0 && index < comboBoxFileRenameStrategy.getItemCount()) {
                comboBoxFileRenameStrategy.setSelectedIndex(index);
            }
        }
    }

    private void persistFileRenameStrategy() {
        int selectedIndex = comboBoxFileRenameStrategy.getSelectedIndex();
        prefs.setInt(KEY_FILE_RENAME_STRATEGY, selectedIndex);
    }

    private void lookupSkipDuplicates() {
        checkBoxSkipDuplicates.setSelected(prefs.containsKey(KEY_SKIP_DUPLICATES)
                ? prefs.getBoolean(KEY_SKIP_DUPLICATES)
                : true);
    }

    private void persistSkipDuplicates() {
        prefs.setBoolean(KEY_SKIP_DUPLICATES, checkBoxSkipDuplicates.isSelected());
    }
    private void removeScriptFile() {
        scriptFile = null;
        scriptFileLabel.setText("");
        scriptFileLabel.setIcon(null);
        buttonRemoveScriptFile.setEnabled(false);
        prefs.removeKey(KEY_SCRIPT_FILE);
    }

    /**
     * @return null if not defined
     */
    public File getScriptFile() {
        return scriptFile;
    }

    public Xmp getXmp() {
        return xmp;
    }

    private void persistFilePath(String key, File file) {
        prefs.setString(key, file.getAbsolutePath());
    }

    private File chooseDir(File startDir) {
        Option showHiddenDirs = getDirChooserOptionShowHiddenDirs();
        DirectoryChooser dlg = new DirectoryChooser(ComponentUtil.findFrameWithIcon(), startDir, showHiddenDirs);
        dlg.setPreferencesKey("ImportImageFilesDialog.DirChooser");
        dlg.setVisible(true);
        toFront();
        return dlg.isAccepted()
               ? dlg.getSelectedDirectories().get(0)
               : null;
    }

    private DirectoryChooser.Option getDirChooserOptionShowHiddenDirs() {
        return isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.NO_OPTION;
    }

    private boolean isAcceptHiddenDirectories() {
        return prefs.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? prefs.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    private void setDirLabel(JLabel label, File dir) {
        label.setIcon(fileSystemView.getSystemIcon(dir));
        label.setText(dir.getAbsolutePath());
    }

    private void setFileLabel(File file, boolean multipleFiles) {
        labelChoosenFiles.setIcon(fileSystemView.getSystemIcon(file));
        labelChoosenFiles.setText(StringUtil.getPrefixDotted(file.getName(), 20) +
                (multipleFiles ? ", ..." : ""));
    }

    private void resetLabelChoosedFiles() {
        labelChoosenFiles.setIcon(null);
        labelChoosenFiles.setText("");
    }

    private void resetLabelSourceDir() {
        labelSourceDir.setText("");
        labelSourceDir.setIcon(null);
    }

    private void setEnabledOkButton() {
        buttonOk.setEnabled(dirsValid());
    }

    private boolean dirsValid() {
        if (filesChoosed) {
            return !sourceFiles.isEmpty() && targetDirectory.isDirectory() && dirsDifferent();
        } else {
            return existsBothDirs() && dirsDifferent();
        }
    }

    private boolean dirsDifferent() {
        return !sourceDirectory.equals(targetDirectory);
    }

    private boolean existsBothDirs() {
        return sourceDirectory.isDirectory() && targetDirectory.isDirectory();
    }

    private boolean checkDirsDifferent(File src, File tgt) {
        if (src.equals(tgt)) {
            String message = Bundle.getString(ImportImageFilesDialog.class, "ImportImageFilesDialog.Error.DirsEquals");
            MessageDisplayer.error(this, message);
            return false;
        }
        return true;
    }

    private void handleCheckBoxDeleteAfterCopyPerformed() {
        if (!listenToCheckBox) {
            return;
        }
        boolean selected = checkBoxDeleteAfterCopy.isSelected();
        if (selected) {
            String message = Bundle.getString(ImportImageFilesDialog.class, "ImportImageFilesDialog.Confirm.DeleteAfterCopy");
            if (!MessageDisplayer.confirmYesNo(this, message)) {
                listenToCheckBox = false;
                selected = false;
                checkBoxDeleteAfterCopy.setSelected(false);
                listenToCheckBox = true;
            }
        }
        deleteSourceFilesAfterCopying = selected;
        prefs.setBoolean(KEY_DEL_SRC_AFTER_COPY, deleteSourceFilesAfterCopying);
    }

    public SubdirectoryCreateStrategy getSubdirectoryCreateStrategy() {
        return (SubdirectoryCreateStrategy) comboBoxSubdirectoryCreateStrategy.getSelectedItem();
    }

    public FileRenameStrategy getFileRenameStrategy() {
        return (FileRenameStrategy) comboBoxFileRenameStrategy.getSelectedItem();
    }

    private void showExpertSettings() {
        dialogExpertSettings.pack();
        dialogExpertSettings.setVisible(true);
    }

    private void editRenameTemplates() {
        int selectedIndex = comboBoxFileRenameStrategy.getSelectedIndex();

        RenameTemplatesEditor editor = Lookup.getDefault().lookup(RenameTemplatesEditor.class);
        editor.displayEditor(this);

        comboBoxFileRenameStrategy.setModel(new FileRenameStrategyComboBoxModel());
        if (ComboBoxUtil.isValidIndex(comboBoxFileRenameStrategy, selectedIndex)) {
            comboBoxFileRenameStrategy.setSelectedIndex(selectedIndex);
        }
    }

    private static class ComboBoxModelSourceStrategy extends DefaultComboBoxModel<Object> {
        private static final long serialVersionUID = 1L;

        private ComboBoxModelSourceStrategy() {
            addElement(Bundle.getString(ComboBoxModelSourceStrategy.class, "ComboBoxModelSourceStrategy.Item.0"));
            addElement(Bundle.getString(ComboBoxModelSourceStrategy.class, "ComboBoxModelSourceStrategy.Item.1"));
        }
    }

    private class EditXmpAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private XmpEditor xmpEditor;

        private EditXmpAction() {
            super(Bundle.getString(ImportImageFilesDialog.class, "ImportImageFilesDialog.EditXmpAction.Name"));
            init();
        }

        private void init() {
            xmpEditor = Lookup.getDefault().lookup(XmpEditor.class);
            setEnabled(xmpEditor != null);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (xmp == null) {
                xmp = xmpEditor.createXmp();
            } else {
                xmpEditor.editXmp(xmp);
            }
        }
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

        panelSourceDirectory = new org.jdesktop.swingx.JXPanel();
        labelSourceDir = new javax.swing.JLabel();
        buttonChooseSourceDir = new javax.swing.JButton();
        panelSelectedFiles = new org.jdesktop.swingx.JXPanel();
        labelChoosenFiles = new javax.swing.JLabel();
        buttonChooseFiles = new javax.swing.JButton();
        dialogExpertSettings = new Dialog(ComponentUtil.findFrameWithIcon(), true);
        panelExpertSettingsContent = new javax.swing.JPanel();
        panelScriptFile = new org.jdesktop.swingx.JXPanel();
        scriptFileLabel = new javax.swing.JLabel();
        buttonRemoveScriptFile = new javax.swing.JButton();
        buttonChooseScriptFile = new javax.swing.JButton();
        labelScriptFileInfo = new javax.swing.JLabel();
        panelExpertSettingsFill = new javax.swing.JPanel();
        panelContent = new javax.swing.JPanel();
        panelSourceStrategy = new javax.swing.JPanel();
        comboBoxSourceStrategy = new javax.swing.JComboBox<>();
        checkBoxDeleteAfterCopy = new javax.swing.JCheckBox();
        checkBoxSkipDuplicates = new javax.swing.JCheckBox();
        panelTargetDir = new org.jdesktop.swingx.JXPanel();
        labelTargetDir = new javax.swing.JLabel();
        buttonChooseTargetDir = new javax.swing.JButton();
        comboBoxSubdirectoryCreateStrategy = new javax.swing.JComboBox<>();
        panelFileRenameStrategy = new javax.swing.JPanel();
        comboBoxFileRenameStrategy = new javax.swing.JComboBox<>();
        buttonEditRenameTemplates = new javax.swing.JButton();
        panelDialogControlButtons = new org.jdesktop.swingx.JXPanel();
        buttonEditMetadata = new javax.swing.JButton();
        buttonExpertSettings = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        panelSourceDirectory.setLayout(new java.awt.GridBagLayout());

        labelSourceDir.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelSourceDir.setPreferredSize(new java.awt.Dimension(400, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelSourceDirectory.add(labelSourceDir, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/importfiles/Bundle"); // NOI18N
        buttonChooseSourceDir.setText(bundle.getString("ImportImageFilesDialog.buttonChooseSourceDir.text")); // NOI18N
        buttonChooseSourceDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseSourceDirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelSourceDirectory.add(buttonChooseSourceDir, gridBagConstraints);

        panelSelectedFiles.setLayout(new java.awt.GridBagLayout());

        labelChoosenFiles.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelChoosenFiles.setPreferredSize(new java.awt.Dimension(400, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelSelectedFiles.add(labelChoosenFiles, gridBagConstraints);

        buttonChooseFiles.setText(bundle.getString("ImportImageFilesDialog.buttonChooseFiles.text")); // NOI18N
        buttonChooseFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFilesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelSelectedFiles.add(buttonChooseFiles, gridBagConstraints);

        dialogExpertSettings.setTitle(bundle.getString("ImportImageFilesDialog.dialogExpertSettings.title")); // NOI18N
        dialogExpertSettings.getContentPane().setLayout(new java.awt.GridBagLayout());

        panelExpertSettingsContent.setLayout(new java.awt.GridBagLayout());

        panelScriptFile.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ImportImageFilesDialog.panelScriptFile.border.title"))); // NOI18N
        panelScriptFile.setLayout(new java.awt.GridBagLayout());

        scriptFileLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        scriptFileLabel.setPreferredSize(new java.awt.Dimension(400, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelScriptFile.add(scriptFileLabel, gridBagConstraints);

        buttonRemoveScriptFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/importfiles/delete.png"))); // NOI18N
        buttonRemoveScriptFile.setToolTipText(bundle.getString("ImportImageFilesDialog.buttonRemoveScriptFile.toolTipText")); // NOI18N
        buttonRemoveScriptFile.setEnabled(false);
        buttonRemoveScriptFile.setPreferredSize(new java.awt.Dimension(16, 16));
        buttonRemoveScriptFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveScriptFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelScriptFile.add(buttonRemoveScriptFile, gridBagConstraints);

        buttonChooseScriptFile.setText(bundle.getString("ImportImageFilesDialog.buttonChooseScriptFile.text")); // NOI18N
        buttonChooseScriptFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseScriptFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelScriptFile.add(buttonChooseScriptFile, gridBagConstraints);

        labelScriptFileInfo.setText(bundle.getString("ImportImageFilesDialog.labelScriptFileInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelScriptFile.add(labelScriptFileInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelExpertSettingsContent.add(panelScriptFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        dialogExpertSettings.getContentPane().add(panelExpertSettingsContent, gridBagConstraints);

        javax.swing.GroupLayout panelExpertSettingsFillLayout = new javax.swing.GroupLayout(panelExpertSettingsFill);
        panelExpertSettingsFill.setLayout(panelExpertSettingsFillLayout);
        panelExpertSettingsFillLayout.setHorizontalGroup(
            panelExpertSettingsFillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelExpertSettingsFillLayout.setVerticalGroup(
            panelExpertSettingsFillLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        dialogExpertSettings.getContentPane().add(panelExpertSettingsFill, gridBagConstraints);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("ImportImageFilesDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        panelSourceStrategy.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ImportImageFilesDialog.panelSourceStrategy.border.title"))); // NOI18N
        panelSourceStrategy.setLayout(new java.awt.GridBagLayout());

        comboBoxSourceStrategy.setModel(new ComboBoxModelSourceStrategy());
        comboBoxSourceStrategy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxSourceStrategyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        panelSourceStrategy.add(comboBoxSourceStrategy, gridBagConstraints);

        checkBoxDeleteAfterCopy.setText(bundle.getString("ImportImageFilesDialog.checkBoxDeleteAfterCopy.text")); // NOI18N
        checkBoxDeleteAfterCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteAfterCopyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelSourceStrategy.add(checkBoxDeleteAfterCopy, gridBagConstraints);

        checkBoxSkipDuplicates.setText(bundle.getString("ImportImageFilesDialog.checkBoxSkipDuplicates.text")); // NOI18N
        checkBoxSkipDuplicates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxSkipDuplicatesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelSourceStrategy.add(checkBoxSkipDuplicates, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(panelSourceStrategy, gridBagConstraints);

        panelTargetDir.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ImportImageFilesDialog.panelTargetDir.border.title"))); // NOI18N
        panelTargetDir.setLayout(new java.awt.GridBagLayout());

        labelTargetDir.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelTargetDir.setPreferredSize(new java.awt.Dimension(400, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        panelTargetDir.add(labelTargetDir, gridBagConstraints);

        buttonChooseTargetDir.setText(bundle.getString("ImportImageFilesDialog.buttonChooseTargetDir.text")); // NOI18N
        buttonChooseTargetDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseTargetDirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        panelTargetDir.add(buttonChooseTargetDir, gridBagConstraints);

        comboBoxSubdirectoryCreateStrategy.setModel(new org.jphototagger.importfiles.subdircreators.SubdirectoryCreateStrategyComboBoxModel());
        comboBoxSubdirectoryCreateStrategy.setRenderer(new DisplayNameListCellRenderer());
        comboBoxSubdirectoryCreateStrategy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxSubdirectoryCreateStrategyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelTargetDir.add(comboBoxSubdirectoryCreateStrategy, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelContent.add(panelTargetDir, gridBagConstraints);

        panelFileRenameStrategy.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ImportImageFilesDialog.panelFileRenameStrategy.border.title"))); // NOI18N
        panelFileRenameStrategy.setLayout(new java.awt.GridBagLayout());

        comboBoxFileRenameStrategy.setModel(new FileRenameStrategyComboBoxModel());
        comboBoxFileRenameStrategy.setRenderer(new DisplayNameListCellRenderer());
        comboBoxFileRenameStrategy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxFileRenameStrategyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 5, 0);
        panelFileRenameStrategy.add(comboBoxFileRenameStrategy, gridBagConstraints);

        buttonEditRenameTemplates.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/importfiles/edit.png"))); // NOI18N
        buttonEditRenameTemplates.setToolTipText(bundle.getString("ImportImageFilesDialog.buttonEditRenameTemplates.toolTipText")); // NOI18N
        buttonEditRenameTemplates.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonEditRenameTemplates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditRenameTemplatesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 5, 5);
        panelFileRenameStrategy.add(buttonEditRenameTemplates, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelContent.add(panelFileRenameStrategy, gridBagConstraints);

        panelDialogControlButtons.setLayout(new java.awt.GridBagLayout());

        buttonEditMetadata.setAction(new EditXmpAction());
        buttonEditMetadata.setText(bundle.getString("ImportImageFilesDialog.buttonEditMetadata.text")); // NOI18N
        buttonEditMetadata.setToolTipText(bundle.getString("ImportImageFilesDialog.buttonEditMetadata.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelDialogControlButtons.add(buttonEditMetadata, gridBagConstraints);

        buttonExpertSettings.setText(bundle.getString("ImportImageFilesDialog.buttonExpertSettings.text")); // NOI18N
        buttonExpertSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExpertSettingsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelDialogControlButtons.add(buttonExpertSettings, gridBagConstraints);

        buttonCancel.setText(bundle.getString("ImportImageFilesDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelDialogControlButtons.add(buttonCancel, gridBagConstraints);

        buttonOk.setText(bundle.getString("ImportImageFilesDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelDialogControlButtons.add(buttonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        panelContent.add(panelDialogControlButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setAccepted(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        setAccepted(true);
    }//GEN-LAST:event_buttonOkActionPerformed

    private void buttonChooseSourceDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseSourceDirActionPerformed
        setDir(buttonChooseSourceDir, chooseSourceDirAction, prevSelectedSourceDirs, setSourceDirFunctor);
    }//GEN-LAST:event_buttonChooseSourceDirActionPerformed

    private void buttonChooseTargetDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseTargetDirActionPerformed
        setDir(buttonChooseTargetDir, chooseTargetDirAction, prevSelectedTargetDirs, setTargetDirFunctor);
    }//GEN-LAST:event_buttonChooseTargetDirActionPerformed

    private void buttonChooseFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseFilesActionPerformed
        chooseSourceFiles();
        toFront();
    }//GEN-LAST:event_buttonChooseFilesActionPerformed

    private void checkBoxDeleteAfterCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDeleteAfterCopyActionPerformed
        handleCheckBoxDeleteAfterCopyPerformed();
    }//GEN-LAST:event_checkBoxDeleteAfterCopyActionPerformed

    private void buttonChooseScriptFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseScriptFileActionPerformed
        chooseAndSetScriptFile();
        toFront();
    }//GEN-LAST:event_buttonChooseScriptFileActionPerformed

    private void buttonRemoveScriptFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveScriptFileActionPerformed
        removeScriptFile();
    }//GEN-LAST:event_buttonRemoveScriptFileActionPerformed

    private void comboBoxSubdirectoryCreateStrategyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxSubdirectoryCreateStrategyActionPerformed
        persistSubdirectoryCreateStrategy();
    }//GEN-LAST:event_comboBoxSubdirectoryCreateStrategyActionPerformed

    private void comboBoxFileRenameStrategyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxFileRenameStrategyActionPerformed
        persistFileRenameStrategy();
    }//GEN-LAST:event_comboBoxFileRenameStrategyActionPerformed

    private void comboBoxSourceStrategyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxSourceStrategyActionPerformed
        setSourceStrategy();
    }//GEN-LAST:event_comboBoxSourceStrategyActionPerformed

    private void buttonExpertSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExpertSettingsActionPerformed
        showExpertSettings();
    }//GEN-LAST:event_buttonExpertSettingsActionPerformed

    private void buttonEditRenameTemplatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditRenameTemplatesActionPerformed
        editRenameTemplates();
    }//GEN-LAST:event_buttonEditRenameTemplatesActionPerformed

    private void checkBoxSkipDuplicatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxSkipDuplicatesActionPerformed
        persistSkipDuplicates();
    }//GEN-LAST:event_checkBoxSkipDuplicatesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ImportImageFilesDialog dialog = new ImportImageFilesDialog();

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
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseFiles;
    private javax.swing.JButton buttonChooseScriptFile;
    private javax.swing.JButton buttonChooseSourceDir;
    private javax.swing.JButton buttonChooseTargetDir;
    private javax.swing.JButton buttonEditMetadata;
    private javax.swing.JButton buttonEditRenameTemplates;
    private javax.swing.JButton buttonExpertSettings;
    private javax.swing.JButton buttonOk;
    private javax.swing.JButton buttonRemoveScriptFile;
    private javax.swing.JCheckBox checkBoxDeleteAfterCopy;
    private javax.swing.JCheckBox checkBoxSkipDuplicates;
    private javax.swing.JComboBox<Object> comboBoxFileRenameStrategy;
    private javax.swing.JComboBox<Object> comboBoxSourceStrategy;
    private javax.swing.JComboBox<Object> comboBoxSubdirectoryCreateStrategy;
    private javax.swing.JDialog dialogExpertSettings;
    private javax.swing.JLabel labelChoosenFiles;
    private javax.swing.JLabel labelScriptFileInfo;
    private javax.swing.JLabel labelSourceDir;
    private javax.swing.JLabel labelTargetDir;
    private javax.swing.JPanel panelContent;
    private org.jdesktop.swingx.JXPanel panelDialogControlButtons;
    private javax.swing.JPanel panelExpertSettingsContent;
    private javax.swing.JPanel panelExpertSettingsFill;
    private javax.swing.JPanel panelFileRenameStrategy;
    private org.jdesktop.swingx.JXPanel panelScriptFile;
    private org.jdesktop.swingx.JXPanel panelSelectedFiles;
    private org.jdesktop.swingx.JXPanel panelSourceDirectory;
    private javax.swing.JPanel panelSourceStrategy;
    private org.jdesktop.swingx.JXPanel panelTargetDir;
    private javax.swing.JLabel scriptFileLabel;
    // End of variables declaration//GEN-END:variables
}
