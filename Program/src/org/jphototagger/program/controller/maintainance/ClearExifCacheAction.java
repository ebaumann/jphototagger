package org.jphototagger.program.controller.maintainance;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jphototagger.exif.cache.ExifCache;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.dialog.MessageDisplayer;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ClearExifCacheAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    public static final ClearExifCacheAction INSTANCE = new ClearExifCacheAction();

    private ClearExifCacheAction() {
        super(Bundle.getString(ClearExifCacheAction.class, "ClearExifCacheAction.Name"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (confirm()) {
            int deletedFileCount = ExifCache.INSTANCE.clear();
            showDeletedFileCountInfo(deletedFileCount);
        }
    }

    public boolean confirm() {
        String message = Bundle.getString(ClearExifCacheAction.class, "ClearExifCacheAction.Confirm");

        return MessageDisplayer.confirmYesNo(null, message);
    }

    private void showDeletedFileCountInfo(int deletedFileCount) {
        String message = Bundle.getString(ClearExifCacheAction.class, "ClearExifCacheAction.Info.DeletedFileCount", deletedFileCount);
        MessageDisplayer.information(null, message);
    }
}
