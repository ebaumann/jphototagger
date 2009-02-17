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

    private final ImageFileThumbnailsPanel thumbnailsPanel;
    private final ProgramExecutor executor;
    private final ActionsDialog actionsDialog;

    public ControllerActionExecutor() {
        thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
        actionsDialog = ActionsDialog.getInstance();
        listen();
        executor = new ProgramExecutor(
                ActionsDialog.getInstance().getProgressBar(this)); // no other executor expected
    }

    @Override
    public void actionPerformed(ProgramActionEvent evt) {
        executor.execute(evt.getProgram(), thumbnailsPanel.getSelectedFiles());
    }

    private void listen() {
        actionsDialog.addActionListener(this);
    }
}
