package de.elmar_baumann.jpt.controller.miscmetadata;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.helper.RenameDeleteXmpValue;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMiscMetadata;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-03-15
 */
public final class ControllerDeleteMiscMetadata extends ControllerMiscMetadata {
    private final JMenuItem itemDelete =
        PopupMenuMiscMetadata.INSTANCE.getItemDelete();

    public ControllerDeleteMiscMetadata() {
        listenToActionsOf(itemDelete);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == itemDelete;
    }

    @Override
    protected void action(Column column, String value) {
        RenameDeleteXmpValue.delete(column, value);
    }
}
