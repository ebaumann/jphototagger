package de.elmar_baumann.lib.renderer;

import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.io.DirectoryTreeModelFile;
import de.elmar_baumann.lib.io.DirectoryTreeModelRoots;
import java.awt.Component;
import java.io.File;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renderer für JTrees mit einem {@link de.elmar_baumann.lib.model.TreeModelDirectories }:
 * Für Verzeichnisse ohne Unterverzeichnisse (leafs) wird auch ein Ordnersymbol angzeigt.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/23
 */
public class TreeCellRendererDirectories extends DefaultTreeCellRenderer {

    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private Object root;

    public TreeCellRendererDirectories() {
    }

    @Override
    public Component getTreeCellRendererComponent(
        JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
        int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);

        if (value instanceof DirectoryTreeModelFile || value instanceof DirectoryTreeModelRoots) {
            if (root == null) {
                root = tree.getModel().getRoot();
            }

            if (value == root) {
                setIcon(IconUtil.getImageIcon("/de/elmar_baumann/lib/resource/workspaceicon.png")); // NOI18N
            }

            int indexRoot = ((DirectoryTreeModelRoots) root).getIndexOfChild(value);
            if (indexRoot >= 0) {
                setIcon(fileSystemView.getSystemIcon((File) value));
            }
        }
        return this;
    }
}
