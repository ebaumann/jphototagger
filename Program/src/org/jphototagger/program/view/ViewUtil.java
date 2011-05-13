package org.jphototagger.program.view;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author Elmar Baumann
 */
public class ViewUtil {
    private ViewUtil() {}

    /**
     * Returns the selected file in a {@link JTree} if the selected node is a
     * {@link DefaultMutableTreeNode} and it's user object is a {@link File} or
     * a {@link Favorite}.
     *
     * @param  tree a tree
     * @return      file or null if no node with a file user object is selected
     */
    public static File getSelectedFile(JTree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        TreePath path = tree.getSelectionPath();

        if (path != null) {
            Object o = path.getLastPathComponent();

            if (o instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) o).getUserObject();

                if (userObject instanceof File) {
                    return (File) userObject;
                } else if (userObject instanceof Favorite) {
                    return ((Favorite) userObject).getDirectory();
                }
            }
        }

        return null;
    }

    public static void setDisplayedMnemonicsToLabels(Container container, Character... exclude) {
        if (container == null) {
            throw new NullPointerException("container == null");
        }

        if (exclude == null) {
            throw new NullPointerException("exclude == null");
        }

        List<JLabel> labels = ComponentUtil.getAllOf(container, JLabel.class);
        List<Character> mnemonics = new ArrayList<Character>(labels.size());
        final char invalidMn = '\0';

        Collections.addAll(mnemonics, exclude);

        for (JLabel label : labels) {
            char mnemonic = MnemonicUtil.getNotExistingMnemonicChar(label.getText(), mnemonics);

            if (mnemonic != invalidMn) {
                label.setDisplayedMnemonic(mnemonic);
                mnemonics.add(mnemonic);
            }
        }
    }

    /**
     *
     * @param  keyCurrentDir
     * @param  filter        can be null
     * @param  parent        can be null
     * @return               choosed file or null
     */
    public static File chooseFile(String keyCurrentDir, FileFilter filter, Component parent) {
        if (keyCurrentDir == null) {
            throw new NullPointerException("keyCurrentDir == null");
        }

        String prevCurrentDir = UserSettings.INSTANCE.getSettings().getString(keyCurrentDir);
        File currentDir = new File(prevCurrentDir.isEmpty()
                                   ? UserSettings.INSTANCE.getSettingsDirectoryName()
                                   : prevCurrentDir);
        JFileChooser fc = new JFileChooser(currentDir);

        if (filter != null) {
            fc.setFileFilter(filter);
        }

        Component parentComp = parent;

        if (parentComp == null) {
            parentComp = GUI.getAppFrame();
        }

        if (fc.showOpenDialog(parentComp) == JFileChooser.APPROVE_OPTION) {
            File selFile = fc.getSelectedFile();

            UserSettings.INSTANCE.getSettings().set(selFile.getAbsolutePath(), keyCurrentDir);

            return selFile;
        }

        return null;
    }

    /**
     * Checks whether selected images are editable.
     *
     * @param  errorMessage true if an error message shall be displayed
     * @return              true if the selected images are editable
     */
    public static boolean checkSelImagesEditable(boolean errorMessage) {
        EditMetadataPanels ep = GUI.getAppPanel().getEditMetadataPanels();

        if (!ep.isEditable()) {
            if (errorMessage) {
                MessageDisplayer.error(null, "ViewUtil.Error.NotEditable");
            }

            return false;
        }

        return true;
    }
}
