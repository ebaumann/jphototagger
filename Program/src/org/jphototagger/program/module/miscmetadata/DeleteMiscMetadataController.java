package org.jphototagger.program.module.miscmetadata;

import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * @author Elmar Baumann
 */
public final class DeleteMiscMetadataController extends MiscMetadataController {

    private final JMenuItem itemDelete;

    public DeleteMiscMetadataController(MiscMetadataPopupMenu popup) {
        if (popup == null) {
            throw new NullPointerException("popup == null");
        }

        itemDelete = popup.getItemDelete();
        listen(popup);
    }

    private void listen(MiscMetadataPopupMenu popup) {
        popup.addListener(itemDelete, this);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected void action(MetaDataValue mdValue, String value) {
        if (value == null) {
            throw new NullPointerException("mdValue == null");
        }

        RenameDeleteXmpValue.delete(mdValue, value);
    }
}
