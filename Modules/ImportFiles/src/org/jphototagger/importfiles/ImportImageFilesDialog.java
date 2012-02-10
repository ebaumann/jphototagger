package org.jphototagger.importfiles;

import java.awt.Color;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.DirectoryChooser.Option;
import org.jphototagger.lib.swing.FileChooserHelper;
import org.jphototagger.lib.swing.FileChooserProperties;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public class ImportImageFilesDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private static final String KEY_LAST_SRC_DIR = "ImportImageFiles.LastSrcDir";
    private static final String KEY_LAST_TARGET_DIR = "ImportImageFiles.LastTargetDir";
    private static final String KEY_DEL_SRC_AFTER_COPY = "ImportImageFiles.DelSrcAfterCopy";
    private static final String KEY_LAST_SCRIPT_DIR = "ImportImageFiles.LastScriptDir";
    private static final String KEY_SCRIPT_FILE = "ImportImageFiles.LastScriptFile";
    private final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private static final Color LABEL_FOREGROUND = getLabelForeground();
    private final Preferences storage = Lookup.getDefault().lookup(Preferences.class);
    private File sourceDir = new File(storage.getString(KEY_LAST_SRC_DIR));
    private File targetDir = new File(storage.getString(KEY_LAST_TARGET_DIR));
    private File scriptFile;
    private String lastChoosenScriptDir = "";
    private final List<File> sourceFiles = new ArrayList<File>();
    private boolean filesChoosed;
    private boolean accepted;
    private boolean deleteSrcFilesAfterCopying;
    private boolean listenToCheckBox = true;

    public ImportImageFilesDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
        setHelpPage();
        init();
    }

    private static Color getLabelForeground() {
        Color foreground = UIManager.getColor("Label.foreground");
        return foreground == null ? Color.BLACK : foreground;
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(ImportImageFilesDialog.class, "ImportImageFilesDialog.HelpPage"));
    }

    private void init() {
        if (sourceDir.isDirectory()) {
            setDirLabel(labelSourceDir, sourceDir);
        }

        if (dirsValid()) {
            setDirLabel(labelTargetDir, targetDir);
            buttonOk.setEnabled(true);
        }

        initDeleteSrcFilesAfterCopying();
        lookupPersistedScriptFile();
        lookupPersistedLastChoosenScriptDir();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void initDeleteSrcFilesAfterCopying() {
        listenToCheckBox = false;
        checkBoxDeleteAfterCopy.setSelected(storage.getBoolean(KEY_DEL_SRC_AFTER_COPY));
        deleteSrcFilesAfterCopying = checkBoxDeleteAfterCopy.isSelected();
        listenToCheckBox = true;
    }

    public boolean isDeleteSourceFilesAfterCopying() {
        return deleteSrcFilesAfterCopying;
    }

    /**
     * Call prior <code>setVisible()</code>.
     * @param dir
     */
    public void setSourceDir(File dir) {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }

        sourceDir = dir;
        init();
    }

    /**
     * Call prior <code>setVisible()</code>.
     * @param dir
     */
    public void setTargetDir(File dir) {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }

        targetDir = dir;
        init();
    }

    /**
     * Returns the source directory if the user choosed a source directory
     * rather than separate files.
     * @return source directory
     */
    public File getSourceDir() {
        return sourceDir;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public boolean isAccepted() {
        return accepted;
    }

    private void setAccepted(boolean accepted) {
        this.accepted = accepted;
        setVisible(false);
    }

    private void chooseSourceDir() {
        File dir = chooseDir(sourceDir);

        if (dir == null) {
            return;
        }

        if (!targetDir.exists() || checkDirsDifferent(dir, targetDir)) {
            sourceDir = dir;
            filesChoosed = false;
            sourceFiles.clear();
            toSettings(KEY_LAST_SRC_DIR, dir);
            resetLabelChoosedFiles();
            setDirLabel(labelSourceDir, dir);
        }

        setEnabledOkButton();
    }

    private void chooseSourceFiles() {
        JFileChooser fileChooser = new JFileChooser(sourceDir);
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
            sourceDir = selFiles[0].getParentFile();
            toSettings(KEY_LAST_SRC_DIR, sourceDir);
            filesChoosed = true;
            resetLabelSourceDir();
            setFileLabel(selFiles[0], selFiles.length > 1);
        }

        setEnabledOkButton();
    }

    /**
     * Returns the choosen files.
     * <p>
     * <em>Verify, that {@code #filesChoosed()} returns true!</em>
     *
     * @return choosen files
     */
    public List<File> getSourceFiles() {
        return new ArrayList<File>(sourceFiles);
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
        File dir = chooseDir(targetDir);

        if (dir == null) {
            return;
        }

        if (!sourceDir.exists() || checkDirsDifferent(sourceDir, dir)) {
            targetDir = dir;
            toSettings(KEY_LAST_TARGET_DIR, dir);
            setDirLabel(labelTargetDir, dir);
        }

        setEnabledOkButton();
    }

    private void lookupPersistedScriptFile() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        if (preferences.containsKey(KEY_SCRIPT_FILE)) {
            String scriptFilePath = preferences.getString(KEY_SCRIPT_FILE);
            File persistedScriptFile = new File(scriptFilePath);
            scriptFileLabel.setText(scriptFilePath);
            boolean scriptFileExists = persistedScriptFile.isFile();
            if (scriptFileExists) {
                scriptFile = persistedScriptFile;
                scriptFileLabel.setIcon(fileSystemView.getSystemIcon(scriptFile));
                buttonRemoveScriptFile.setEnabled(true);
            }
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
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        preferences.setString(KEY_SCRIPT_FILE, scriptFilePath);
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
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        preferences.setString(KEY_LAST_SCRIPT_DIR, lastChoosenScriptDir);
    }

    private void lookupPersistedLastChoosenScriptDir() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);

        if (preferences.containsKey(KEY_LAST_SCRIPT_DIR)) {
            lastChoosenScriptDir = preferences.getString(KEY_LAST_SCRIPT_DIR);
        }
    }

    private void removeScriptFile() {
        scriptFile = null;
        scriptFileLabel.setText("");
        scriptFileLabel.setIcon(null);
        buttonRemoveScriptFile.setEnabled(false);
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        preferences.removeKey(KEY_SCRIPT_FILE);
    }

    /**
     * @return null if not defined
     */
    public File getScriptFile() {
        return scriptFile;
    }

    private void toSettings(String key, File dir) {
        storage.setString(key, dir.getAbsolutePath());
    }

    private File chooseDir(File startDir) {
        Option showHiddenDirs = getDirChooserOptionShowHiddenDirs();
        DirectoryChooser dlg = new DirectoryChooser(ComponentUtil.findFrameWithIcon(), startDir, showHiddenDirs);

        dlg.setStorageKey("ImportImageFilesDialog.DirChooser");
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
        return storage.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? storage.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
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
            return !sourceFiles.isEmpty() && targetDir.isDirectory() && dirsDifferent();
        } else {
            return existsBothDirs() && dirsDifferent();
        }
    }

    private boolean dirsDifferent() {
        return !sourceDir.equals(targetDir);
    }

    private boolean existsBothDirs() {
        return sourceDir.isDirectory() && targetDir.isDirectory();
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

        deleteSrcFilesAfterCopying = selected;
        storage.setBoolean(KEY_DEL_SRC_AFTER_COPY, deleteSrcFilesAfterCopying);
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

        sourceDirPanel = new org.jdesktop.swingx.JXPanel();
        labelSourceDir = new javax.swing.JLabel();
        buttonChooseSourceDir = new javax.swing.JButton();
        checkBoxDeleteAfterCopy = new javax.swing.JCheckBox();
        choosenFilesPanel = new org.jdesktop.swingx.JXPanel();
        labelChoosenFiles = new javax.swing.JLabel();
        buttonChooseFiles = new javax.swing.JButton();
        targetDirPanel = new org.jdesktop.swingx.JXPanel();
        labelTargetDir = new javax.swing.JLabel();
        buttonChooseTargetDir = new javax.swing.JButton();
        scriptFilePanel = new org.jdesktop.swingx.JXPanel();
        scriptFileLabel = new javax.swing.JLabel();
        buttonRemoveScriptFile = new javax.swing.JButton();
        buttonChooseScriptFile = new javax.swing.JButton();
        labelScriptFileInfo = new javax.swing.JLabel();
        vPaddingPanel = new javax.swing.JPanel();
        dialogControlButtonsPanel = new org.jdesktop.swingx.JXPanel();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/importfiles/Bundle"); // NOI18N
        setTitle(bundle.getString("ImportImageFilesDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        sourceDirPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ImportImageFilesDialog.sourceDirPanel.border.title"))); // NOI18N
        sourceDirPanel.setName("sourceDirPanel"); // NOI18N
        sourceDirPanel.setLayout(new java.awt.GridBagLayout());

        labelSourceDir.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelSourceDir.setName("labelSourceDir"); // NOI18N
        labelSourceDir.setPreferredSize(new java.awt.Dimension(400, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        sourceDirPanel.add(labelSourceDir, gridBagConstraints);

        buttonChooseSourceDir.setText(bundle.getString("ImportImageFilesDialog.buttonChooseSourceDir.text")); // NOI18N
        buttonChooseSourceDir.setName("buttonChooseSourceDir"); // NOI18N
        buttonChooseSourceDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseSourceDirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        sourceDirPanel.add(buttonChooseSourceDir, gridBagConstraints);

        checkBoxDeleteAfterCopy.setText(bundle.getString("ImportImageFilesDialog.checkBoxDeleteAfterCopy.text")); // NOI18N
        checkBoxDeleteAfterCopy.setName("checkBoxDeleteAfterCopy"); // NOI18N
        checkBoxDeleteAfterCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteAfterCopyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        sourceDirPanel.add(checkBoxDeleteAfterCopy, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(sourceDirPanel, gridBagConstraints);

        choosenFilesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ImportImageFilesDialog.choosenFilesPanel.border.title"))); // NOI18N
        choosenFilesPanel.setName("choosenFilesPanel"); // NOI18N
        choosenFilesPanel.setLayout(new java.awt.GridBagLayout());

        labelChoosenFiles.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelChoosenFiles.setName("labelChoosenFiles"); // NOI18N
        labelChoosenFiles.setPreferredSize(new java.awt.Dimension(400, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        choosenFilesPanel.add(labelChoosenFiles, gridBagConstraints);

        buttonChooseFiles.setText(bundle.getString("ImportImageFilesDialog.buttonChooseFiles.text")); // NOI18N
        buttonChooseFiles.setName("buttonChooseFiles"); // NOI18N
        buttonChooseFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFilesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        choosenFilesPanel.add(buttonChooseFiles, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(choosenFilesPanel, gridBagConstraints);

        targetDirPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ImportImageFilesDialog.targetDirPanel.border.title"))); // NOI18N
        targetDirPanel.setName("targetDirPanel"); // NOI18N
        targetDirPanel.setLayout(new java.awt.GridBagLayout());

        labelTargetDir.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelTargetDir.setName("labelTargetDir"); // NOI18N
        labelTargetDir.setPreferredSize(new java.awt.Dimension(400, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        targetDirPanel.add(labelTargetDir, gridBagConstraints);

        buttonChooseTargetDir.setText(bundle.getString("ImportImageFilesDialog.buttonChooseTargetDir.text")); // NOI18N
        buttonChooseTargetDir.setName("buttonChooseTargetDir"); // NOI18N
        buttonChooseTargetDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseTargetDirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        targetDirPanel.add(buttonChooseTargetDir, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(targetDirPanel, gridBagConstraints);

        scriptFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ImportImageFilesDialog.scriptFilePanel.border.title"))); // NOI18N
        scriptFilePanel.setName("scriptFilePanel"); // NOI18N
        scriptFilePanel.setLayout(new java.awt.GridBagLayout());

        scriptFileLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        scriptFileLabel.setName("scriptFileLabel"); // NOI18N
        scriptFileLabel.setPreferredSize(new java.awt.Dimension(400, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        scriptFilePanel.add(scriptFileLabel, gridBagConstraints);

        buttonRemoveScriptFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/importfiles/delete.png"))); // NOI18N
        buttonRemoveScriptFile.setToolTipText(bundle.getString("ImportImageFilesDialog.buttonRemoveScriptFile.toolTipText")); // NOI18N
        buttonRemoveScriptFile.setEnabled(false);
        buttonRemoveScriptFile.setName("buttonRemoveScriptFile"); // NOI18N
        buttonRemoveScriptFile.setPreferredSize(new java.awt.Dimension(16, 16));
        buttonRemoveScriptFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveScriptFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        scriptFilePanel.add(buttonRemoveScriptFile, gridBagConstraints);

        buttonChooseScriptFile.setText(bundle.getString("ImportImageFilesDialog.buttonChooseScriptFile.text")); // NOI18N
        buttonChooseScriptFile.setName("buttonChooseScriptFile"); // NOI18N
        buttonChooseScriptFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseScriptFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        scriptFilePanel.add(buttonChooseScriptFile, gridBagConstraints);

        labelScriptFileInfo.setText(bundle.getString("ImportImageFilesDialog.labelScriptFileInfo.text")); // NOI18N
        labelScriptFileInfo.setName("labelScriptFileInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        scriptFilePanel.add(labelScriptFileInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(scriptFilePanel, gridBagConstraints);

        vPaddingPanel.setName("vPaddingPanel"); // NOI18N

        javax.swing.GroupLayout vPaddingPanelLayout = new javax.swing.GroupLayout(vPaddingPanel);
        vPaddingPanel.setLayout(vPaddingPanelLayout);
        vPaddingPanelLayout.setHorizontalGroup(
            vPaddingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        vPaddingPanelLayout.setVerticalGroup(
            vPaddingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(vPaddingPanel, gridBagConstraints);

        dialogControlButtonsPanel.setName("dialogControlButtonsPanel"); // NOI18N
        dialogControlButtonsPanel.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        buttonCancel.setText(bundle.getString("ImportImageFilesDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        dialogControlButtonsPanel.add(buttonCancel);

        buttonOk.setText(bundle.getString("ImportImageFilesDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.setName("buttonOk"); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        dialogControlButtonsPanel.add(buttonOk);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        getContentPane().add(dialogControlButtonsPanel, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setAccepted(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        setAccepted(true);
    }//GEN-LAST:event_buttonOkActionPerformed

    private void buttonChooseSourceDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseSourceDirActionPerformed
        chooseSourceDir();
    }//GEN-LAST:event_buttonChooseSourceDirActionPerformed

    private void buttonChooseTargetDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseTargetDirActionPerformed
        chooseTargetDir();
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
    private javax.swing.JButton buttonOk;
    private javax.swing.JButton buttonRemoveScriptFile;
    private javax.swing.JCheckBox checkBoxDeleteAfterCopy;
    private org.jdesktop.swingx.JXPanel choosenFilesPanel;
    private org.jdesktop.swingx.JXPanel dialogControlButtonsPanel;
    private javax.swing.JLabel labelChoosenFiles;
    private javax.swing.JLabel labelScriptFileInfo;
    private javax.swing.JLabel labelSourceDir;
    private javax.swing.JLabel labelTargetDir;
    private javax.swing.JLabel scriptFileLabel;
    private org.jdesktop.swingx.JXPanel scriptFilePanel;
    private org.jdesktop.swingx.JXPanel sourceDirPanel;
    private org.jdesktop.swingx.JXPanel targetDirPanel;
    private javax.swing.JPanel vPaddingPanel;
    // End of variables declaration//GEN-END:variables
}
