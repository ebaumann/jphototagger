package org.jphototagger.program.module.miscmetadata;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.program.view.popupmenus.MiscMetadataPopupMenu;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class RemoveMetadataFromSelectedImagesController extends MiscMetadataController {

    private final JMenuItem itemRemove;

    public RemoveMetadataFromSelectedImagesController(MiscMetadataPopupMenu popup) {
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
