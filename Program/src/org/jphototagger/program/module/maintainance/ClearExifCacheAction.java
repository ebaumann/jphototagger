package org.jphototagger.program.module.maintainance;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.exif.ExifCacheProvider;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;

/**
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
            int deletedFileCount = Lookup.getDefault().lookup(ExifCacheProvider.class).clear();
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
