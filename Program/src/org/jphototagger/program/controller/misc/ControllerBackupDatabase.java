package org.jphototagger.program.controller.misc;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.helper.BackupDatabase;
import org.jphototagger.program.resource.GUI;

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
        String message = Bundle.getString(ControllerBackupDatabase.class, "ControllerBackupDatabase.Info.ChooseDir");
        MessageDisplayer.information(null, message);
        AppLifeCycle.INSTANCE.addFinalTask(BackupDatabase.INSTANCE);
    }
}
