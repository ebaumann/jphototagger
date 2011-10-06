package org.jphototagger.program.module.maintainance;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jphototagger.lib.componentutil.ComponentUtil;

/**
 *
 * @author Elmar Baumann
 */
public final class MaintainRepositoryController implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent evt) {
        maintainRepository();
    }

    private void maintainRepository() {
        ComponentUtil.show(RepositoryMaintainanceDialog.INSTANCE);
    }
}
