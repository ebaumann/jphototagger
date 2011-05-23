package org.jphototagger.program.controller.maintainance;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.cache.ExifCache;
import org.jphototagger.program.resource.JptBundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ClearExifCacheAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    public static final ClearExifCacheAction INSTANCE = new ClearExifCacheAction();

    private ClearExifCacheAction() {
        super(JptBundle.INSTANCE.getString("ClearExifCacheAction.Name"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (confirm()) {
            int deletedFileCount = ExifCache.INSTANCE.clear();
            showDeletedFileCountInfo(deletedFileCount);
        }
    }

    public boolean confirm() {
        return MessageDisplayer.confirmYesNo(null, "ClearExifCacheAction.Confirm");
    }

    private void showDeletedFileCountInfo(int deletedFileCount) {
        MessageDisplayer.information(null, "ClearExifCacheAction.Info.DeletedFileCount", deletedFileCount);
    }
}
