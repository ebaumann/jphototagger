package org.jphototagger.program.module.actions;

import javax.swing.JMenu;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.event.programs.ProgramDeletedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramInsertedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramUpdatedEvent;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;

/**
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
        Program program = evt.getProgram();
        JMenu actionMenu = ThumbnailsPopupMenu.INSTANCE.getMenuActions();

        ActionsUtil.removeAction(actionMenu, program);
    }

    @EventSubscriber(eventClass = ProgramInsertedEvent.class)
    public void programInserted(final ProgramInsertedEvent evt) {
        Program program = evt.getProgram();

        if (program.isAction()) {
            JMenu actionMenu = ThumbnailsPopupMenu.INSTANCE.getMenuActions();

            ActionsUtil.addAction(actionMenu, program);
        }
    }

    @EventSubscriber(eventClass = ProgramUpdatedEvent.class)
    public void programUpdated(final ProgramUpdatedEvent evt) {
        Program program = evt.getProgram();

        if (program.isAction()) {
            JMenu actionMenu = ThumbnailsPopupMenu.INSTANCE.getMenuActions();

            ActionsUtil.updateAction(actionMenu, program);
        }
    }
}
