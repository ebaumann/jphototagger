package org.jphototagger.program.module.metadatatemplates;

import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpEditor;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = XmpEditor.class)
public final class XmpEditorImpl implements XmpEditor {

    @Override
    public Xmp createXmp() {
        EditXmpDialog dialog = new EditXmpDialog();
        dialog.setVisible(true);
        return dialog.isAccepted()
                ? dialog.getXmp()
                : null;
    }

    @Override
    public boolean editXmp(Xmp xmp) {
        EditXmpDialog dialog = new EditXmpDialog(xmp);
        dialog.setVisible(true);
        return dialog.isAccepted();
    }
}
