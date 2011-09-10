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
public final class ControllerDeleteMiscMetadata extends ControllerMiscMetadata {

    private final JMenuItem itemDelete;

    public ControllerDeleteMiscMetadata(PopupMenuMiscMetadata popup) {
        if (popup == null) {
            throw new NullPointerException("popup == null");
        }

        itemDelete = popup.getItemDelete();
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
