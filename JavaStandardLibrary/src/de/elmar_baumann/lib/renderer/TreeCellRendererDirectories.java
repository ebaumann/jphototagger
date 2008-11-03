package de.elmar_baumann.lib.renderer;

import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.resource.Bundle;
import java.awt.Component;
import java.io.File;
import javax.swing.Icon;
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
    Icon rootIcon = IconUtil.getImageIcon("/de/elmar_baumann/lib/resource/workspaceicon.png"); // NOI18N

    @Override
    public Component getTreeCellRendererComponent(
        JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
        int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false, row, hasFocus);

        if (value == tree.getModel().getRoot()) {
            setIcon(rootIcon);
            setText(Bundle.getString("DirectoryTreeModel.Root.Text"));
        } else if (value instanceof File) {
            File file = (File) value;
            try {
                setIcon(fileSystemView.getSystemIcon(file));
                setText(getDirectoryName(file));
            } catch (Exception ex) {
            }
        }
        return this;
    }

    private String getDirectoryName(File file) {
        String name = file.getName();

        // Windows drive letters
        if (name.isEmpty()) {
            name = file.getAbsolutePath();
            if (name.endsWith("\\")) { // NOI18N
                name = name.substring(0, name.length() - 2) + ":"; // NOI18N
            }
        }
        return name;
    }
}
