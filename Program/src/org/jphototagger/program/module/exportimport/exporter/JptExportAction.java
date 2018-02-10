package org.jphototagger.program.module.exportimport.exporter;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.module.exportimport.ExportImportContext;
import org.jphototagger.program.module.exportimport.ExportImportDialog;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class JptExportAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public JptExportAction() {
        super(Bundle.getString(JptExportAction.class, "JptExportAction.Name"));
        putValue(SMALL_ICON, Icons.getIcon("icon_app.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ExportImportDialog exportDialog = new ExportImportDialog(ExportImportContext.EXPORT);
        exportDialog.setVisible(true);
    }
}
