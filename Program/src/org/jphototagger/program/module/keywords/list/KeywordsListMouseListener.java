package org.jphototagger.program.module.keywords.list;

import org.jdesktop.swingx.JXList;

import org.openide.util.Lookup;

import org.jphototagger.program.event.listener.ListMouseListener;
import org.jphototagger.program.module.editmetadata.EditMetaDataPanelsProvider;

/**
 * Do not use this class as a template for other implementations! Instead extend
 * a popup menu from {@code org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class KeywordsListMouseListener extends ListMouseListener {

    private final KeywordsListPopupMenu popup = KeywordsListPopupMenu.INSTANCE;

    @Override
    protected void showPopup(JXList list, int x, int y) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        popup.setSelIndex(getIndex());
        popup.setList(list);
        setEnabled();
        popup.show(list, x, y);
    }

    private void setEnabled() {
        EditMetaDataPanelsProvider provider = Lookup.getDefault().lookup(EditMetaDataPanelsProvider.class);
        boolean editable = provider.getEditMetadataPanels().isEditable();

        popup.getItemAddToEditPanel().setEnabled(editable);
        popup.getItemRemoveFromEditPanel().setEnabled(editable);
    }
}
