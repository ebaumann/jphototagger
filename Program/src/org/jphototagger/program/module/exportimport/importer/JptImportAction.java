package org.jphototagger.program.module.exportimport.importer;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.openide.util.Lookup;

import org.jphototagger.api.branding.Branding;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.exportimport.ExportImportContext;
import org.jphototagger.program.module.exportimport.ExportImportDialog;

/**
 * @author Elmar Baumann
 */
public final class JptImportAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public JptImportAction() {
        super(Bundle.getString(JptImportAction.class, "JptImportAction.Name"));
        Branding branding = Lookup.getDefault().lookup(Branding.class);
        putValue(SMALL_ICON, IconUtil.getImageIcon(JptImportAction.class, "jpt.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ExportImportDialog importDialog = new ExportImportDialog(ExportImportContext.IMPORT);
        importDialog.setVisible(true);
    }
}
