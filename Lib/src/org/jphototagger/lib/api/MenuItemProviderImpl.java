package org.jphototagger.lib.api;

import javax.swing.Action;
import javax.swing.JMenuItem;

import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.swing.util.MenuUtil;

/**
 * @author Elmar Baumann
 */
public final class MenuItemProviderImpl implements MenuItemProvider {

    private final Action action;
    private final int position;
    private final boolean isSeparatorBefore;

    public MenuItemProviderImpl(Action action, int position, boolean isSeparatorBefore) {
        this.action = action;
        this.position = position;
        this.isSeparatorBefore = isSeparatorBefore;
    }

    @Override
    public JMenuItem getMenuItem() {
        JMenuItem item = new JMenuItem(action);
        MenuUtil.setMnemonics(item);
        return item;
    }

    @Override
    public boolean isSeparatorBefore() {
        return isSeparatorBefore;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return action.toString();
    }
}
