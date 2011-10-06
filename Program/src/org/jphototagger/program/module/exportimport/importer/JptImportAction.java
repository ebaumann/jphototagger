package org.jphototagger.program.module.exportimport.importer;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jphototagger.program.module.exportimport.ExportImportDialog;
import org.jphototagger.program.module.exportimport.ExportImportContext;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class JptImportAction extends AbstractAction {

    private static final long serialVersionUID = 7147327788643508948L;
    public static final JptImportAction INSTANCE = new JptImportAction();

    @Override
    public void actionPerformed(ActionEvent evt) {
        new ExportImportDialog(ExportImportContext.IMPORT).setVisible(true);
    }

    private JptImportAction() {
    }
}
