package org.jphototagger.program.controller.miscmetadata;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.helper.MiscMetadataHelper;
import org.jphototagger.program.view.popupmenus.MiscMetadataPopupMenu;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class AddMetadataToSelectedImagesController extends MiscMetadataController {

    private final JMenuItem itemAdd;

    public AddMetadataToSelectedImagesController(MiscMetadataPopupMenu popup) {
        if (popup == null) {
            throw new NullPointerException("popup == null");
        }

        itemAdd = popup.getItemAddToEditPanel();
        popup.addListener(itemAdd, this);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_B);
    }

    @Override
    protected void action(MetaDataValue mdValue, String value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        MiscMetadataHelper.addMetadataToSelectedImages(mdValue, value);
    }
}
