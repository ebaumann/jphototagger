package org.jphototagger.program.controller.maintainance;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.cache.IptcIgnoreCache;
import org.jphototagger.program.resource.JptBundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ClearIptcIgnoreCacheAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    public static final ClearIptcIgnoreCacheAction INSTANCE = new ClearIptcIgnoreCacheAction();

    private ClearIptcIgnoreCacheAction() {
        super(JptBundle.INSTANCE.getString("ClearIptcIgnoreCacheAction.Name"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (confirm()) {
            int deletedFileCount = IptcIgnoreCache.INSTANCE.clear();
            showDeletedFileCountInfo(deletedFileCount);
        }
    }

    public boolean confirm() {
        return MessageDisplayer.confirmYesNo(null, "ClearIptcIgnoreCacheAction.Confirm");
    }

    private void showDeletedFileCountInfo(int deletedFileCount) {
        MessageDisplayer.information(null, "ClearIptcIgnoreCacheAction.Info.DeletedFileCount", deletedFileCount);
    }
}
