package org.jphototagger.program.module.editmetadata;

import java.util.Objects;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class EditMetadtaUtil {

    public static void addCopyPasteToComponentPopupMenu(JTextComponent tc) {
        Objects.requireNonNull(tc, "tc == null");

        Action copyAction = new DefaultEditorKit.CopyAction();
        Action cutAction = new DefaultEditorKit.CutAction();
        Action pasteAction = new DefaultEditorKit.PasteAction();

        copyAction.putValue(Action.NAME, Bundle.getString(EditMetadtaUtil.class, "EditMetadtaUtil.CopyAction.Name"));
        copyAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
        cutAction.putValue(Action.NAME, Bundle.getString(EditMetadtaUtil.class, "EditMetadtaUtil.CutAction.Name"));
        cutAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
        pasteAction.putValue(Action.NAME, Bundle.getString(EditMetadtaUtil.class, "EditMetadtaUtil.PasteAction.Name"));
        pasteAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));

        JPopupMenu popup = tc.getComponentPopupMenu();
        if (popup == null) {
            popup = new JPopupMenu();
            tc.setComponentPopupMenu(popup);
        }

        popup.add(copyAction);
        popup.add(cutAction);
        popup.add(pasteAction);
    }

    private EditMetadtaUtil() {
    }

}
