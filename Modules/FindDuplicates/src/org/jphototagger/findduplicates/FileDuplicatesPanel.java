package org.jphototagger.findduplicates;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.image.util.ThumbnailCreatorService;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.runtime.External;
import org.jphototagger.lib.runtime.RuntimeUtil;
import org.jphototagger.lib.swing.ImagePanel;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Images;
import org.openide.util.Lookup;

/**
 * Fires {@link #PROPERTY_FILE_SELECTED} if a file will be selected or unselected.
 * @author Elmar Baumann
 */
public class FileDuplicatesPanel extends javax.swing.JPanel {

    public static final String PROPERTY_FILE_SELECTED = "fileSelected";
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(FileDuplicatesPanel.class.getName());
    private static final Image ERROR_THUMBNAIL = Images.getLocalizedImage("/org/jphototagger/resources/images/thumbnail_not_rendered.png");
    private int fileSelectionCount;
    private int rowCount;

    public FileDuplicatesPanel() {
        initComponents();
    }

    public boolean isFileSelected() {
        return fileSelectionCount > 0;
    }

    public void clear() {
        rowCount = 0;
        fileSelectionCount = 0;
        removeAll();
    }

    /**
     * @return Faster than determine the size of {@link #getSelectedFiles()}
     */
    public int getFileSelectionCount() {
        return fileSelectionCount;
    }

    public List<File> getSelectedFiles() {
        List<File> files = new ArrayList<>();
        List<FilePanel> filePanels = ComponentUtil.getAllOf(this, FilePanel.class);
        for (FilePanel filePanel : filePanels) {
            if (filePanel.isFileSelected()) {
                files.add(filePanel.getFile());
            }
        }
        return files;
    }

    public void removeFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        for (FilePanel panel : ComponentUtil.getAllOf(this, FilePanel.class)) {
            if (panel.getFile().equals(file)) {
                panel.getParent().remove(panel);
                if (panel.isFileSelected()) {
                    // panel.checkBoxFileSelected.setSelected(false); does not work
                    panel.checkBoxFileSelected.removeActionListener(fileSelectedAction);
                    fileSelectionCount--;
                    firePropertyChange(PROPERTY_FILE_SELECTED, true, false);
                }
                ComponentUtil.forceRepaint(this);
            }
        }
    }

    private static final Comparator<File> FILE_SORT_COMPARATOR = new Comparator<File>() {

        private final Comparator<String> delegate = String.CASE_INSENSITIVE_ORDER;

        @Override
        public int compare(File o1, File o2) {
            String pathname1 = o1.getAbsolutePath();
            String pathname2 = o2.getAbsolutePath();
            return delegate.compare(pathname1, pathname2);
        }
    };

    public void addDuplicates(Collection<? extends File> duplicates) {
        List<File> dups = new ArrayList<File>(duplicates);
        Collections.sort(dups, FILE_SORT_COMPARATOR);
        JPanel panel = new JPanel(new GridBagLayout());
        boolean first = true;
        for (File file : dups) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, first ? 0 : 10, 0, 0);
            first = false;
            panel.add(new FilePanel(file), gbc);
        }
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(rowCount == 0 ? 0 : 10, 0, 0, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(panel, gbc);
        rowCount++;
    }

    private final class FilePanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private final JCheckBox checkBoxFileSelected = new JCheckBox(fileSelectedAction);
        private final File file;

        private FilePanel(File file) {
            this.file = file;
            initComponents();
        }

        private void initComponents() {
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEtchedBorder());
            Image thumbnail = null;
            try {
                thumbnail = ThumbnailCreatorService.INSTANCE.createThumbnail(file);
            } catch (Throwable t) {
                Logger.getLogger(FilePanel.class.getName()).log(Level.SEVERE, null, t);
            }
            if (thumbnail == null) {
                thumbnail = ERROR_THUMBNAIL;
            }
            ImagePanel imagePanel = new ImagePanel();
            imagePanel.setPreferredSize(new Dimension(thumbnail.getWidth(null), thumbnail.getHeight(null)));
            imagePanel.setImage(thumbnail);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.insets = new Insets(5, 5, 0, 5);
            add(imagePanel, gbc);
            gbc.insets = new Insets(5, 5, 0, 5);
            JLabel labelFilepath = new JLabel();
            labelFilepath.setText(getShortFilepath());
            labelFilepath.setToolTipText(file.getAbsolutePath());
            add(labelFilepath, gbc);
            gbc.insets = new Insets(5, 5, 5, 0);
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(5, 5, 5, 0);
            add(checkBoxFileSelected, gbc);
            gbc.anchor = GridBagConstraints.EAST;
            JButton buttonOpenFile = new JButton(new OpenFileAction(file));
            gbc.insets = new Insets(5, 5, 5, 5);
            add(buttonOpenFile, gbc);
            JButton buttonOpenDir = new JButton(new OpenDirectoryAction(file.getParentFile()));
            gbc.insets = new Insets(5, 5, 5, 5);
            add(buttonOpenDir, gbc);
        }

        private String getShortFilepath() {
            String path = file.getAbsolutePath();
            int maxLength = 45;
            int pathLength = path.length();
            if (pathLength > maxLength) {
                return path.substring(0, 11) + "..." + path.substring(pathLength - maxLength + 14);
            } else {
                return path;
            }
        }

        private boolean isFileSelected() {
            return checkBoxFileSelected.isSelected();
        }

        private File getFile() {
            return file;
        }
    }

    private final class OpenDirectoryAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final File directory;

        private OpenDirectoryAction(File directory) {
            this.directory = directory;
            putValue(Action.NAME, Bundle.getString(OpenDirectoryAction.class, "OpenDirectoryAction.Name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DesktopUtil.open(directory, "FileDuplicatesPanel.OpenDirectoryAction.OpenCommand");
        }
    }

    private final class OpenFileAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);
        private final File file;

        private OpenFileAction(File file) {
            this.file = file;
            putValue(Action.NAME, Bundle.getString(OpenDirectoryAction.class, "OpenFileAction.Name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Program program = findProgramForFile(FileUtil.getSuffix(file));
            if (program == null) {
                LOGGER.log(Level.INFO, "Open file ''{0}'' with Desktop because no default program is defined", file);
                DesktopUtil.open(file, "FileDuplicatesPanel.OpenFileAction.OpenCommand");
            } else {
                String command = RuntimeUtil.quoteForCommandLine(program.getFile(), file);
                External.execute(command);
            }
        }

        private Program findProgramForFile(String suffix) {
            Program program = repo.findDefaultProgram(suffix);
            return program == null
                    ? repo.findDefaultImageOpenProgram()
                    : null;
        }
    }

    private final Action fileSelectedAction = new AbstractAction() {

        private static final long serialVersionUID = 1L;

        {
            putValue(Action.NAME, Bundle.getString(FileDuplicatesPanel.this.getClass(), "FileDuplicatesPanel.SelectFile"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AbstractButton button = (AbstractButton) e.getSource();
            boolean selected = button.isSelected();
            if (selected) {
                fileSelectionCount++;
            } else {
                fileSelectionCount--;
            }
            FileDuplicatesPanel.this.firePropertyChange(PROPERTY_FILE_SELECTED, !selected, selected);
        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(new java.awt.GridBagLayout());
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
