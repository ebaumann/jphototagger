package de.elmar_baumann.imv.controller.actions;

import de.elmar_baumann.imv.event.ProgramEvent;
import de.elmar_baumann.imv.event.listener.ProgramActionListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.ProgramStarter;
import de.elmar_baumann.imv.view.dialogs.ActionsDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;

/**
 * Executes actions of the dialog 
 * {@link de.elmar_baumann.imv.view.dialogs.ActionsDialog}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
public final class ControllerActionExecutor implements ProgramActionListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.
            getAppPanel().getPanelThumbnails();
    private final ActionsDialog actionsDialog = ActionsDialog.INSTANCE;
    private final ProgramStarter programStarter = new ProgramStarter(
            actionsDialog.getProgressBar(this)); // no other executor expected

    public ControllerActionExecutor() {
        listen();
    }

    private void listen() {
        actionsDialog.addActionListener(this);
    }

    @Override
    public void actionPerformed(ProgramEvent evt) {
        if (evt.getType().equals(ProgramEvent.Type.PROGRAM_EXECUTED)) {
            programStarter.startProgram(evt.getProgram(), thumbnailsPanel.
                    getSelectedFiles());
        }
    }
}
