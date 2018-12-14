package org.jphototagger.importfiles;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.util.MenuUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public final class ImportImageFilesAction extends AbstractAction implements MenuItemProvider {

    private static final long serialVersionUID = 1L;

    public ImportImageFilesAction() {
        super(Bundle.getString(ImportImageFilesAction.class, "ImportImageFilesAction.Name"));
        putValue(SMALL_ICON, org.jphototagger.resources.Icons.getIcon("icon_card.png"));
        putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_P));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ImportImageFiles.importFrom(null);
    }

    @Override
    public JMenuItem getMenuItem() {
        JMenuItem item = UiFactory.menuItem(this);
        MenuUtil.setMnemonics(item);
        return item;
    }

    @Override
    public boolean isSeparatorBefore() {
        return true;
    }

    @Override
    public int getPosition() {
        return 4;
    }
}
