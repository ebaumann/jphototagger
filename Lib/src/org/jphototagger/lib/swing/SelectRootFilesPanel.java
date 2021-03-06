package org.jphototagger.lib.swing;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class SelectRootFilesPanel extends PanelExt implements Persistence {

    private static final long serialVersionUID = 1L;
    private final Map<JCheckBox, File> ROOT_FILE_OF_CHECKBOX = new HashMap<>();
    private String persistenceKey = "SelectRootFilesPanel";
    private boolean listenToCheckBoxSelection = true;
    private final CheckBoxSelectionListener checkBoxSelectionListener = new CheckBoxSelectionListener();

    public SelectRootFilesPanel() {
        initComponents();
        addRootFileCheckboxes();
    }

    private void addRootFileCheckboxes() {
        File[] roots = File.listRoots();

        if (roots == null) {
            Logger.getLogger(SelectRootFilesPanel.class.getName()).log(Level.SEVERE, "No root files found");
            return;
        }

        int rootFileCount = roots.length;

        for (int index = 0; index < rootFileCount; index++) {
            File rootFile = roots[index];
            String rootFilePath = rootFile.getAbsolutePath();
            JCheckBox rootFileCheckBox = UiFactory.checkBox(rootFilePath);

            ROOT_FILE_OF_CHECKBOX.put(rootFileCheckBox, rootFile);
            add(rootFileCheckBox, getCheckboxConstraints(index == rootFileCount - 1));
            rootFileCheckBox.addActionListener(checkBoxSelectionListener);
        }
    }

    private GridBagConstraints getCheckboxConstraints(boolean isLast) {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = isLast ? GridBagConstraints.REMAINDER : 1;
        gbc.weightx = 1.0;
        gbc.weighty = isLast ? 1.0 : 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        return gbc;
    }

    public List<File> getSelectedRootFiles() {
        List<File> selectedRootFiles = new ArrayList<>();
        Set<JCheckBox> checkBoxes = ROOT_FILE_OF_CHECKBOX.keySet();

        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selectedRootFiles.add(ROOT_FILE_OF_CHECKBOX.get(checkBox));
            }
        }

        return selectedRootFiles;
    }

    public void setPersistenceKey(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        persistenceKey = key;
    }

    public static List<File> readPersistentRootFiles(String key) {
        List<File> rootFiles = new ArrayList<>();
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        List<String> rootFilePaths = prefs.getStringCollection(key);

        for (String rootFilePath : rootFilePaths) {
            File rootFile = new File(rootFilePath);

            rootFiles.add(rootFile);
        }

        return rootFiles;
    }

    @Override
    public void restore() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        List<String> rootFilePaths = prefs.getStringCollection(persistenceKey);
        Set<JCheckBox> checkBoxes = ROOT_FILE_OF_CHECKBOX.keySet();

        boolean prevListenToCheckBoxSelection = listenToCheckBoxSelection;
        listenToCheckBoxSelection = false;
        for (JCheckBox checkBox : checkBoxes) {
            File rootFile = ROOT_FILE_OF_CHECKBOX.get(checkBox);
            String rootFilePath = rootFile.getAbsolutePath();
            boolean isSelected = rootFilePaths.contains(rootFilePath);

            checkBox.setSelected(isSelected);
        }
        listenToCheckBoxSelection = prevListenToCheckBoxSelection;
    }

    @Override
    public void persist() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        List<File> selectedRootFiles = getSelectedRootFiles();
        List<String> selectedRootFilePaths = new ArrayList<>(selectedRootFiles.size());

        prefs.removeKey(persistenceKey);

        for (File selectedRootFile : selectedRootFiles) {
            selectedRootFilePaths.add(selectedRootFile.getAbsolutePath());
        }

        prefs.setStringCollection(persistenceKey, selectedRootFilePaths);
    }

    private class CheckBoxSelectionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!listenToCheckBoxSelection) {
                return;
            }

            Object source = e.getSource();

            if (source instanceof JCheckBox) {
                persist();
            }
        }
    }

    private void initComponents() {
        setLayout(new java.awt.GridBagLayout());
    }
}
