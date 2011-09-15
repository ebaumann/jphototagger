package org.jphototagger.program.repository.exporter;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jphototagger.program.view.dialogs.ExportImportDialog;
import org.jphototagger.program.view.panels.ExportImportPanel.Context;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class JptExportAction extends AbstractAction {

    private static final long serialVersionUID = 2682169629889229733L;
    public static final JptExportAction INSTANCE = new JptExportAction();

    @Override
    public void actionPerformed(ActionEvent evt) {
        new ExportImportDialog(Context.EXPORT).setVisible(true);
    }

    private JptExportAction() {
    }
}
