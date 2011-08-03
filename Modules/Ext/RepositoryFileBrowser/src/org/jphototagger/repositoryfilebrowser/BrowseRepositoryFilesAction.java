package org.jphototagger.repositoryfilebrowser;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jphototagger.api.windows.AppMenuAction;
import org.jphototagger.lib.util.Bundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class BrowseRepositoryFilesAction extends AbstractAction implements AppMenuAction {

    private static final long serialVersionUID = 1L;
    public static final BrowseRepositoryFilesAction INSTANCE = new BrowseRepositoryFilesAction();

    private BrowseRepositoryFilesAction() {
        super(Bundle.getString(BrowseRepositoryFilesAction.class, "DisplayFileBrowserAction.Name"));
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
}
