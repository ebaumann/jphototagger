package org.jphototagger.program.controller.actions;

import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.event.listener.DatabaseProgramsListener;
import org.jphototagger.program.helper.ActionsHelper;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.EventQueue;

import javax.swing.JMenu;

/**
 * Listens to {@link DatabasePrograms} events and inserts or removes actions
 * from the {@link PopupMenuThumbnails#getMenuActions()}.
 *
 * @author Elmar Baumann
 */
public final class ActionsMenuUpdater implements DatabaseProgramsListener {
    public ActionsMenuUpdater() {
        setMenuItemEnabled();
        listen();
    }

    private void listen() {
        DatabasePrograms.INSTANCE.addListener(this);
    }

    private void setMenuItemEnabled() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JMenu actionMenu = PopupMenuThumbnails.INSTANCE.getMenuActions();

                actionMenu.setEnabled(DatabasePrograms.INSTANCE.hasAction());
            }
        });
    }

    @Override
    public void programDeleted(final Program program) {
        if (program.isAction()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JMenu actionMenu = PopupMenuThumbnails.INSTANCE.getMenuActions();

                    ActionsHelper.removeAction(actionMenu, program);
                }
            });
        }
    }

    @Override
    public void programInserted(final Program program) {
        if (program.isAction()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JMenu actionMenu = PopupMenuThumbnails.INSTANCE.getMenuActions();

                    ActionsHelper.addAction(actionMenu, program);
                }
            });
        }
    }

    @Override
    public void programUpdated(final Program program) {
        if (program.isAction()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JMenu actionMenu = PopupMenuThumbnails.INSTANCE.getMenuActions();

                    ActionsHelper.updateAction(actionMenu, program);
                }
            });
        }
    }
}
