package org.jphototagger.program.datatransfer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.data.TextEntry;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.view.panels.EditRepeatableTextEntryPanel;

import java.awt.Component;

import java.util.Collection;

import javax.swing.TransferHandler.TransferSupport;

/**
 *
 *
 * @author Elmar Baumann
 */
final class MetadataTemplateSupport {
    @SuppressWarnings({ "unchecked", "unchecked" })
    public static void setTemplate(TransferSupport support) {
        try {
            Object[] selTemplates =
                (Object[]) support.getTransferable().getTransferData(
                    Flavor.METADATA_TEMPLATES);
            TextEntry textEntry = findParentTextEntry(support.getComponent());

            if ((selTemplates != null) && (textEntry != null)) {
                Column           column   = textEntry.getColumn();
                MetadataTemplate template = (MetadataTemplate) selTemplates[0];
                Object           value    = template.getValueOfColumn(column);

                if (value instanceof String) {
                    textEntry.setText((String) value);
                    textEntry.setDirty(true);
                } else if (value instanceof Collection<?>) {
                    EditRepeatableTextEntryPanel panel =
                        findRepeatableTextEntryPanel(support.getComponent());

                    if (panel == null) {
                        return;
                    }

                    panel.setText((Collection<String>) value);
                    panel.setDirty(true);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(TransferHandlerDropTextComponent.class, ex);
        }
    }

    private static EditRepeatableTextEntryPanel findRepeatableTextEntryPanel(
            Component c) {
        Component parent = c.getParent();

        while (parent != null) {
            if (parent instanceof EditRepeatableTextEntryPanel) {
                return (EditRepeatableTextEntryPanel) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    private static TextEntry findParentTextEntry(Component c) {
        Component parent = c.getParent();

        while (parent != null) {
            if (parent instanceof TextEntry) {
                return (TextEntry) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    private MetadataTemplateSupport() {}
}
