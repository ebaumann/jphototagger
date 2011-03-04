package org.jphototagger.program.controller.misc;

import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.helper.BackupDatabase;
import org.jphototagger.program.resource.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerBackupDatabase extends Controller {
    public ControllerBackupDatabase() {
        listenToActionsOf(GUI.getAppFrame().getMenuItemBackupDatabase());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return false;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == GUI.getAppFrame().getMenuItemBackupDatabase();
    }

    @Override
    protected void action(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        addBackupTask();
    }

    @Override
    protected void action(KeyEvent evt) {

        // Ignore
    }

    private void addBackupTask() {
        MessageDisplayer.information(null, "ControllerBackupDatabase.Info.ChooseDir");
        AppLifeCycle.INSTANCE.addFinalTask(BackupDatabase.INSTANCE);
    }
}
