package org.jphototagger.program.module.exportimport.exporter;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.exportimport.ExportImportContext;
import org.jphototagger.program.module.exportimport.ExportImportDialog;

/**
 * @author Elmar Baumann
 */
public final class JptExportAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public JptExportAction() {
        super(Bundle.getString(JptExportAction.class, "JptExportAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(JptExportAction.class, "jpt.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ExportImportDialog exportDialog = new ExportImportDialog(ExportImportContext.EXPORT);
        exportDialog.setVisible(true);
    }
}
