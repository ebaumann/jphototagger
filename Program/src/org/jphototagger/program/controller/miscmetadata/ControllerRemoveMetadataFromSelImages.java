package org.jphototagger.program.controller.miscmetadata;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.program.helper.MiscMetadataHelper;
import org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerRemoveMetadataFromSelImages extends ControllerMiscMetadata {

    private final JMenuItem itemRemove;

    public ControllerRemoveMetadataFromSelImages(PopupMenuMiscMetadata popup) {
        if (popup == null) {
            throw new NullPointerException("popup == null");
        }

        itemRemove = popup.getItemRemoveFromEditPanel();
        popup.addListener(itemRemove, this);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_BACK_SPACE;
    }

    @Override
    protected void action(MetaDataValue mdValue, String value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        MiscMetadataHelper.removeMetadataFromSelectedImages(mdValue, value);
    }
}
