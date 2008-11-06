package de.elmar_baumann.imv.controller.actions;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.event.DialogActionsEvent;
import de.elmar_baumann.imv.event.DialogActionsListener;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.ActionsDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 * Executes actions of the dialog 
 * {@link de.elmar_baumann.imv.view.dialogs.ActionsDialog}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
public class ControllerActionExecutor extends Controller implements DialogActionsListener {

    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    private JProgressBar progressBar;
    Queue<Execute> queue = new ConcurrentLinkedQueue<Execute>();
    private ActionsDialog dialog = ActionsDialog.getInstance();

    public ControllerActionExecutor() {
        dialog.addActionListener(this);
        progressBar = dialog.getProgressBar(this); // no other executor expected
    }

    @Override
    public void setControl(boolean control) {
        super.setControl(control);
        if (!control) {
        }
    }

    @Override
    public void actionPerformed(DialogActionsEvent evt) {
        if (isControl() && evt.isExecute()) {
            List<File> imageFiles = thumbnailsPanel.getSelectedFiles();
            if (imageFiles.size() <= 0) {
                errorMessageSelection();
                return;
            }
            execute(evt.getProgram(), imageFiles);
        }
    }

    private void execute(Program program, List<File> imageFiles) {
        Execute execute = new Execute(program, imageFiles);
        execute.setPriority(UserSettings.getInstance().getThreadPriority());
        synchronized (queue) {
            if (queue.isEmpty()) {
                execute.start();
            } else {
                queue.add(execute);
            }
        }
    }

    private void errorMessageSelection() {
        JOptionPane.showMessageDialog(
            dialog,
            Bundle.getString("ControllerActionExecutor.ErrorMessage.Selection"),
            Bundle.getString("ControllerActionExecutor.ErrorMessage.Selection.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    private class Execute extends Thread {

        private Program program;
        private List<File> imageFiles;

        public Execute(Program program, List<File> imageFiles) {
            this.imageFiles = imageFiles;
            this.program = program;
        }

        @Override
        public void run() {
            initProgressBar();
            String input = getInput(program.getAlias());
            String parameters = program.getParameters() == null ? "" : program.getParameters();
            int count = 0;
            for (File file : imageFiles) {
                IoUtil.execute(
                    program.getFile().getAbsolutePath(),
                    program.isParametersAfterFilename() 
                    ? " \"" + file.getAbsolutePath() + "\" " + parameters + " " + input
                    : parameters + " " + input + " \"" + file.getAbsolutePath() + "\"");
                progressBar.setValue(++count);
            }
            nextExecutor();
        }

        private String getInput(String actionName) {
            if (program.isInputBeforeExecute()) {
                MessageFormat msg = new MessageFormat(Bundle.getString("ControllerActionExecutor.InformationMessage.Input.Prompt"));
                String input = JOptionPane.showInputDialog(dialog,
                    msg.format(new Object[]{actionName}));
                if (input != null) {
                    return input;
                }
            }
            return "";
        }

        private void initProgressBar() {
            progressBar.setMinimum(0);
            progressBar.setMaximum(imageFiles.size());
            progressBar.setValue(0);
        }

        private void nextExecutor() {
            synchronized (queue) {
                if (!queue.isEmpty()) {
                    queue.poll().start();
                }
            }
        }
    }
}
