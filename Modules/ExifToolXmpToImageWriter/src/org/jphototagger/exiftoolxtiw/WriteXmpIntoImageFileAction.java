package org.jphototagger.exiftoolxtiw;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class WriteXmpIntoImageFileAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public WriteXmpIntoImageFileAction() {
        super(Bundle.getString(WriteXmpIntoImageFileAction.class, "WriteXmpIntoImageFileAction.Name"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ExifTooolXmpToImageWriterController ctrl = new ExifTooolXmpToImageWriterController();

        ctrl.executeInDialog();
    }
}
