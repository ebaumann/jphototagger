package org.jphototagger.program.controller.miscmetadata;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.program.helper.RenameDeleteXmpValue;
import org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerRenameMiscMetadata extends ControllerMiscMetadata {

    private final JMenuItem itemRename;

    public ControllerRenameMiscMetadata(PopupMenuMiscMetadata popup) {
        if (popup == null) {
            throw new NullPointerException("popup == null");
        }

        itemRename = popup.getItemRename();
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
