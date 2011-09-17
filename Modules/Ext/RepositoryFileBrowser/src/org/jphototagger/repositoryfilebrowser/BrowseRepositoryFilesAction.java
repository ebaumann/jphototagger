package org.jphototagger.repositoryfilebrowser;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jphototagger.api.windows.MainWindowMenuAction;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class BrowseRepositoryFilesAction extends AbstractAction implements MainWindowMenuAction {

    private static final long serialVersionUID = 1L;
    public static final BrowseRepositoryFilesAction INSTANCE = new BrowseRepositoryFilesAction();

    private BrowseRepositoryFilesAction() {
        super(Bundle.getString(BrowseRepositoryFilesAction.class, "DisplayFileBrowserAction.Name"));

        putValue(Action.SMALL_ICON, IconUtil.getImageIcon(BrowseRepositoryFilesAction.class, "icon_database.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RepositoryFileBrowserDialog dialog = new RepositoryFileBrowserDialog(null, true);

        dialog.setVisible(true);
    }

    @Override
    public Action getAction() {
        return INSTANCE;
    }

    @Override
    public int getPosition() {
        return -1;
    }

    @Override
    public boolean isUsedInMenusSeparatorBefore() {
        return true;
    }
}
