package org.jphototagger.program.module.miscmetadata;

import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * @author Elmar Baumann
 */
public final class RenameMiscMetadataController extends MiscMetadataController {

    private final JMenuItem itemRename;

    public RenameMiscMetadataController(MiscMetadataPopupMenu popup) {
        if (popup == null) {
            throw new NullPointerException("popup == null");
        }

        itemRename = popup.getItemRename();
        listen(popup);
    }

    private void listen(MiscMetadataPopupMenu popup) {
        popup.addListener(itemRename, this);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    protected void action(MetaDataValue mdValue, String value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        RenameDeleteXmpValue.rename(mdValue, value);
    }
}
