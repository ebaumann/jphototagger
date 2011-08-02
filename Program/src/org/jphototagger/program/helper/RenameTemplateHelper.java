package org.jphototagger.program.helper;

import java.awt.Component;

import org.jphototagger.domain.templates.RenameTemplate;
import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.program.database.DatabaseRenameTemplates;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class RenameTemplateHelper {

    /**
     * Inserts a new rename template into the database.
     *
     * @param  template new template
     */
    public static void insert(RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        assert template.getId() == null;

        String name = getUniqueName(null);

        if (name != null) {
            template.setName(name);

            if (!DatabaseRenameTemplates.INSTANCE.insert(template)) {
                String message = Bundle.getString(RenameTemplateHelper.class, "RenameTemplateHelper.Error.Insert", template);
                MessageDisplayer.error(null, message);
            }
        }
    }

    public static void update(RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        assert template.getId() != null : template.getId();

        if (!DatabaseRenameTemplates.INSTANCE.update(template)) {
            String message = Bundle.getString(RenameTemplateHelper.class, "RenameTemplateHelper.Error.Update", template);
            MessageDisplayer.error(null, message);
        }
    }

    public static void rename(RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        assert template.getId() != null : template.getId();

        String name = getUniqueName(template.getName());

        if (name != null) {
            template.setName(name);
            update(template);
        }
    }

    public static void delete(RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        assert template.getId() != null : template.getId();

        Component parentComponent = null;
        String message = Bundle.getString(RenameTemplateHelper.class, "RenameTemplateHelper.Confirm.Delete", template);

        if (MessageDisplayer.confirmYesNo(parentComponent, message)) {
            if (DatabaseRenameTemplates.INSTANCE.delete(template.getName()) != 1) {
                message = Bundle.getString(RenameTemplateHelper.class, "RenameTemplateHelper.Error.Delete", template);
                MessageDisplayer.error(null, message);
            }
        }
    }

    private static String getUniqueName(String suggest) {
        String info = Bundle.getString(RenameTemplateHelper.class, "RenameTemplateHelper.Input.Name");
        String input = (suggest == null) ? "" : suggest;
        InputDialog dlg = new InputDialog(info, input);

        dlg.setVisible(true);

        boolean unique = false;

        while (!unique && dlg.isAccepted()) {
            String name = dlg.getInput().trim();

            if (name.isEmpty()) {
                return null;
            }

            unique = !DatabaseRenameTemplates.INSTANCE.exists(name);
            Component parentComponent = null;
            String message = Bundle.getString(RenameTemplateHelper.class, "RenameTemplateHelper.Confirm.InputUniqueName", name);

            if (!unique && MessageDisplayer.confirmYesNo(parentComponent, message)) {
                dlg.setVisible(true);
            } else if (unique) {
                return name;
            } else {
                return null;
            }
        }

        return null;
    }

    private RenameTemplateHelper() {
}
}
