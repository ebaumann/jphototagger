package org.jphototagger.program.module.search;

import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@code org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class SavedSearchesPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;
    public static final SavedSearchesPopupMenu INSTANCE = new SavedSearchesPopupMenu();
    private final JMenuItem itemDelete = UiFactory.menuItem(Bundle.getString(SavedSearchesPopupMenu.class, "SavedSearchesPopupMenu.DisplayName.Action.Delete"), Icons.ICON_DELETE);
    private final JMenuItem itemEdit = UiFactory.menuItem(Bundle.getString(SavedSearchesPopupMenu.class, "SavedSearchesPopupMenu.DisplayName.Action.Edit"), Icons.ICON_EDIT);
    private final JMenuItem itemCreate = UiFactory.menuItem(Bundle.getString(SavedSearchesPopupMenu.class, "SavedSearchesPopupMenu.DisplayName.Action.New"), Icons.ICON_NEW);
    private final JMenuItem itemRename = UiFactory.menuItem(Bundle.getString(SavedSearchesPopupMenu.class, "SavedSearchesPopupMenu.DisplayName.Action.Rename"), Icons.ICON_RENAME);
    private transient SavedSearch savedSearch;

    private SavedSearchesPopupMenu() {
        init();
    }

    public JMenuItem getItemCreate() {
        return itemCreate;
    }

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    public JMenuItem getItemEdit() {
        return itemEdit;
    }

    /**
     * Setzt die gespeicherte Suche.
     *
     * @param savedSearch Gespeicherte Suche. Default: null.
     */
    public void setSavedSearch(SavedSearch savedSearch) {
        this.savedSearch = savedSearch;
    }

    /**
     * Liefert die gespeicherte Suche.
     *
     * @return Gespeicherte Suche
     */
    public SavedSearch getSavedSearch() {
        return savedSearch;
    }

    private void init() {
        UiFactory.configure(this);
        addItems();
        setAccelerators();
    }

    private void addItems() {
        add(itemCreate);
        add(itemEdit);
        add(itemRename);
        add(itemDelete);
    }

    private void setAccelerators() {
        itemCreate.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemEdit.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_E));
        itemDelete.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemRename.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
    }
}
