package org.jphototagger.program.helper;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.RenameTemplate;
import org.jphototagger.program.database.DatabaseRenameTemplates;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.dialog.InputDialog;

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
                MessageDisplayer.error(null,
                                       "RenameTemplateHelper.Error.Insert",
                                       template);
            }
        }
    }

    public static void update(RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        assert template.getId() != null : template.getId();

        if (!DatabaseRenameTemplates.INSTANCE.update(template)) {
            MessageDisplayer.error(null, "RenameTemplateHelper.Error.Update",
                                   template);
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

        if (MessageDisplayer.confirmYesNo(
                null, "RenameTemplateHelper.Confirm.Delete", template)) {
            if (DatabaseRenameTemplates.INSTANCE.delete(template.getName())
                    != 1) {
                MessageDisplayer.error(null,
                                       "RenameTemplateHelper.Error.Delete",
                                       template);
            }
        }
    }

    private static String getUniqueName(String suggest) {
        InputDialog dlg = new InputDialog(
                              JptBundle.INSTANCE.getString(
                                  "RenameTemplateHelper.Input.Name"), (suggest
                                      == null)
                ? ""
                : suggest, UserSettings.INSTANCE.getProperties(),
                           "RenameTemplateHelper.Input.Name");

        dlg.setVisible(true);

        boolean unique = false;

        while (!unique && dlg.isAccepted()) {
            String name = dlg.getInput().trim();

            if (name.isEmpty()) {
                return null;
            }

            unique = !DatabaseRenameTemplates.INSTANCE.exists(name);

            if (!unique
                    && MessageDisplayer.confirmYesNo(null,
                        "RenameTemplateHelper.Confirm.InputUniqueName", name)) {
                dlg.setVisible(true);
            } else if (unique) {
                return name;
            } else {
                return null;
            }
        }

        return null;
    }

    private RenameTemplateHelper() {}
}
