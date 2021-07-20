package org.jphototagger.program.module.filesystem;

import java.awt.Component;
import java.awt.Frame;
import org.jphototagger.domain.editors.RenameTemplatesEditor;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = RenameTemplatesEditor.class)
public final class RenameTemplatesEditorImpl implements RenameTemplatesEditor {

    @Override
    public void displayEditor(Component parentComponent) {
        Frame parentFrame = parentComponent == null
                ? ComponentUtil.findFrameWithIcon()
                : ComponentUtil.findParentFrame(parentComponent);
        RenameTemplatesDialog dialog = new RenameTemplatesDialog(parentFrame == null
                ? ComponentUtil.findFrameWithIcon()
                : parentFrame);

        dialog.setVisible(true);
    }
}
