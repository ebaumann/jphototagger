package org.jphototagger.program.app;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class ExitAppAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public ExitAppAction() {
        super(Bundle.getString(ExitAppAction.class, "ExitAppAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(ExitAppAction.class, "exit.png"));
        putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_Q));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AppLifeCycle.INSTANCE.quit();
    }
}
