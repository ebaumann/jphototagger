package org.jphototagger.program.controller.maintainance;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jphototagger.iptc.IptcIgnoreCache;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ClearIptcIgnoreCacheAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    public static final ClearIptcIgnoreCacheAction INSTANCE = new ClearIptcIgnoreCacheAction();

    private ClearIptcIgnoreCacheAction() {
        super(Bundle.getString(ClearIptcIgnoreCacheAction.class, "ClearIptcIgnoreCacheAction.Name"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (confirm()) {
            int deletedFileCount = IptcIgnoreCache.INSTANCE.clear();
            showDeletedFileCountInfo(deletedFileCount);
        }
    }

    public boolean confirm() {
        String message = Bundle.getString(ClearIptcIgnoreCacheAction.class, "ClearIptcIgnoreCacheAction.Confirm");

        return MessageDisplayer.confirmYesNo(null, message);
    }

    private void showDeletedFileCountInfo(int deletedFileCount) {
        String message = Bundle.getString(ClearIptcIgnoreCacheAction.class, "ClearIptcIgnoreCacheAction.Info.DeletedFileCount", deletedFileCount);
        MessageDisplayer.information(null, message);
    }
}
