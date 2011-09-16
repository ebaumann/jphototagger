package org.jphototagger.program.controller.actions;

import javax.swing.JMenu;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.event.programs.ProgramDeletedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramInsertedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.helper.ActionsHelper;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 * Listens to {@code DatabasePrograms} events and inserts or removes actions
 * from the {@code ThumbnailsPopupMenu#getMenuActions()}.
 *
 * @author Elmar Baumann
 */
public final class ActionsMenuUpdater {

    public ActionsMenuUpdater() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ProgramDeletedEvent.class)
    public void programDeleted(final ProgramDeletedEvent evt) {
        final Program program = evt.getProgram();
        if (program.isAction()) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    JMenu actionMenu = ThumbnailsPopupMenu.INSTANCE.getMenuActions();

                    ActionsHelper.removeAction(actionMenu, program);
                }
            });
        }
    }

    @EventSubscriber(eventClass = ProgramInsertedEvent.class)
    public void programInserted(final ProgramInsertedEvent evt) {
        final Program program = evt.getProgram();

        if (program.isAction()) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    JMenu actionMenu = ThumbnailsPopupMenu.INSTANCE.getMenuActions();

                    ActionsHelper.addAction(actionMenu, program);
                }
            });
        }
    }

    @EventSubscriber(eventClass = ProgramUpdatedEvent.class)
    public void programUpdated(final ProgramUpdatedEvent evt) {
        final Program program = evt.getProgram();

        if (program.isAction()) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    JMenu actionMenu = ThumbnailsPopupMenu.INSTANCE.getMenuActions();

                    ActionsHelper.updateAction(actionMenu, program);
                }
            });
        }
    }
}
