package org.jphototagger.maintainance.browse;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public final class ResultSetBrowserAction extends AbstractAction implements MenuItemProvider {

    private static final long serialVersionUID = 1L;

    public ResultSetBrowserAction() {
        super(Bundle.getString(ResultSetBrowserAction.class, "ResultSetBrowserAction.Name"));
        putValue(Action.SMALL_ICON, Icons.getIcon("icon_database.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (MessageDisplayer.confirmYesNo(ComponentUtil.findFrameWithIcon(), Bundle.getString(ResultSetBrowserAction.class, "ResultSetBrowserAction.Confirm"))) {
            ResultSetBrowserController ctrl = new ResultSetBrowserController();

            ctrl.execute();
        }
    }

    @Override
    public JMenuItem getMenuItem() {
        return UiFactory.menuItem(this);
    }

    @Override
    public boolean isSeparatorBefore() {
        return false;
    }

    @Override
    public int getPosition() {
        return 300;
    }
}
