package de.elmar_baumann.jpt.controller.miscmetadata;

import de.elmar_baumann.jpt.app.JptSelectionLookup;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.event.listener.impl.LookupAction;
import de.elmar_baumann.jpt.helper.MiscMetadataHelper;
import de.elmar_baumann.jpt.resource.JptBundle;

import java.awt.event.ActionEvent;

import javax.swing.Action;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-03-15
 */
public final class DeleteMiscMetadataAction extends LookupAction<Column> {
    private static final long serialVersionUID = -6854701760771564552L;

    public DeleteMiscMetadataAction() {
        super(Column.class);
        setName();
    }

    private void setName() {
        putValue(
            Action.NAME,
            JptBundle.INSTANCE.getString(
                "DeleteMiscMetadataAction.DisplayName"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Column column = JptSelectionLookup.INSTANCE.lookup(Column.class);

        if (column != null) {
            MiscMetadataHelper.deleteSelectedLeafHavingColumnParent();
            JptSelectionLookup.INSTANCE.remove(Column.class, column);
        }
    }
}
