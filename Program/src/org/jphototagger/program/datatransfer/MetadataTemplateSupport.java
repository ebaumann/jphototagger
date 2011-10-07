package org.jphototagger.program.datatransfer;

import java.awt.Component;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.TransferHandler.TransferSupport;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.domain.text.TextEntry;
import org.jphototagger.program.app.ui.EditRepeatableTextEntryPanel;

/**
 *
 *
 * @author Elmar Baumann
 */
final class MetadataTemplateSupport {

    @SuppressWarnings({"unchecked", "unchecked"})
    public static void setTemplate(TransferSupport support) {
        try {
            Object[] selTemplates = (Object[]) support.getTransferable().getTransferData(Flavor.METADATA_TEMPLATES);
            TextEntry textEntry = findParentTextEntry(support.getComponent());

            if ((selTemplates != null) && (textEntry != null)) {
                MetaDataValue mdValue = textEntry.getMetaDataValue();
                MetadataTemplate template = (MetadataTemplate) selTemplates[0];
                Object value = template.getMetaDataValue(mdValue);

                if (value instanceof String) {
                    textEntry.setText((String) value);
                    textEntry.setDirty(true);
                } else if (value instanceof Collection<?>) {
                    EditRepeatableTextEntryPanel panel = findRepeatableTextEntryPanel(support.getComponent());

                    if (panel == null) {
                        return;
                    }

                    panel.setTexts((Collection<String>) value);
                    panel.setDirty(true);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MetadataTemplateSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static EditRepeatableTextEntryPanel findRepeatableTextEntryPanel(Component c) {
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

    private MetadataTemplateSupport() {
    }
}
