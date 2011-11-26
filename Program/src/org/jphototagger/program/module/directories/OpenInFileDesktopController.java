package org.jphototagger.program.module.directories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.lib.awt.DesktopUtil;

/**
 * @author Elmar Baumann
 */
public final class OpenInFileDesktopController extends DirectoryController {

    public OpenInFileDesktopController() {
        listenToActionsOf(DirectoriesPopupMenu.INSTANCE.getItemOpenInDesktop());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_O;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == DirectoriesPopupMenu.INSTANCE.getItemOpenInDesktop();
    }

    @Override
    protected void action(DefaultMutableTreeNode node) {
        File dir = getDirOfNode(node);

        if (dir == null) {
            return;
        }

        DesktopUtil.open(dir, "JPhotoTagger.BrowseFolder.Executable");
    }
}
