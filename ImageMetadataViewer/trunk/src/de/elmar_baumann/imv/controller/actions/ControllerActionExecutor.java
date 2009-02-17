package de.elmar_baumann.imv.controller.actions;

import de.elmar_baumann.imv.event.ProgramActionEvent;
import de.elmar_baumann.imv.event.ProgramActionListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.tasks.ProgramExecutor;
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

    private final ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    private final ActionsDialog actionsDialog = ActionsDialog.getInstance();
    private final ProgramExecutor executor = new ProgramExecutor(actionsDialog.getProgressBar(this)); // no other executor expected

    public ControllerActionExecutor() {
        listen();
    }

    private void listen() {
        actionsDialog.addActionListener(this);
    }

    @Override
    public void actionPerformed(ProgramActionEvent evt) {
        executor.execute(evt.getProgram(), thumbnailsPanel.getSelectedFiles());
    }
}
