package org.jphototagger.program.controller.miscmetadata;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.helper.MiscMetadataHelper;
import org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerAddMetadataToSelImages
        extends ControllerMiscMetadata {
    private final JMenuItem itemAdd;

    public ControllerAddMetadataToSelImages(PopupMenuMiscMetadata popup) {
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
    protected void action(Column column, String value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        MiscMetadataHelper.addMetadataToSelectedImages(column, value);
    }
}
