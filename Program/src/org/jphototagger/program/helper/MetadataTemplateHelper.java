package org.jphototagger.program.helper;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.DatabaseMetadataTemplates;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.dialogs.InputHelperDialog;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class MetadataTemplateHelper {

    /**
     * Returns a not existing template name.
     *
     * @param  fromName old name or null. If not null, the new name has to be
     *                 different from <code>fromName</code>.
     * @return         name or null
     */
    public static String getNewTemplateName(String fromName) {
        InputDialog dlg = new InputDialog(InputHelperDialog.INSTANCE);

        dlg.setInfo(JptBundle.INSTANCE.getString("MetadataTemplateHelper.Info.InputName"));

        if (fromName != null) {
            dlg.setInput(fromName);
        }

        while (true) {
            ComponentUtil.show(dlg);

            if (!dlg.isAccepted()) {
                return null;
            }

            String name = dlg.getInput();

            if ((name == null) || (name.trim().length() == 0)) {
                return null;
            }

            boolean namesEqual = (fromName != null) && name.equalsIgnoreCase(fromName);

            if (namesEqual) {
                if (!MessageDisplayer.confirmYesNo(null, "MetadataTemplateHelper.Error.NamEquals")) {
                    return null;
                }
            }

            if (!namesEqual && DatabaseMetadataTemplates.INSTANCE.exists(name)) {
                if (!MessageDisplayer.confirmYesNo(null, "MetadataTemplateHelper.Error.NameExists", name)) {
                    return null;
                }
            } else {
                return name;
            }
        }
    }

    private MetadataTemplateHelper() {}
}
