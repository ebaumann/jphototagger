package org.jphototagger.program.view.panels;

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
import org.jphototagger.lib.util.Settings;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.types.Persistence;

/**
 *
 *
 * @author Elmar Baumann
 */
public class SelectRootFilesPanel extends javax.swing.JPanel implements Persistence {
    private static final long serialVersionUID = 1L;
    private final Map<JCheckBox, File> ROOT_FILE_OF_CHECKBOX = new HashMap<JCheckBox, File>();
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
            JCheckBox rootFileCheckBox = new JCheckBox(rootFilePath);

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
        List<File> selectedRootFiles = new ArrayList<File>();
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
        List<File> rootFiles = new ArrayList<File>();
        Settings settings = UserSettings.INSTANCE.getSettings();
        List<String> rootFilePaths = settings.getStringCollection(key);

        for (String rootFilePath : rootFilePaths) {
            File rootFile = new File(rootFilePath);

            rootFiles.add(rootFile);
        }

        return rootFiles;
    }

    @Override
    public void readProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();
        List<String> rootFilePaths = settings.getStringCollection(persistenceKey);
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
    public void writeProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();
        List<File> selectedRootFiles = getSelectedRootFiles();
        List<String> selectedRootFilePaths = new ArrayList<String>(selectedRootFiles.size());

        settings.removeKey(persistenceKey);

        for (File selectedRootFile : selectedRootFiles) {
            selectedRootFilePaths.add(selectedRootFile.getAbsolutePath());
        }

        settings.setStringCollection(selectedRootFilePaths, persistenceKey);
        UserSettings.INSTANCE.writeToFile();
    }

    private class CheckBoxSelectionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!listenToCheckBoxSelection) {
                return;
            }

            Object source = e.getSource();

            if (source instanceof JCheckBox) {
                writeProperties();
            }
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
