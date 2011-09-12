package org.jphototagger.program.helper;

import org.jphototagger.domain.repository.MetadataTemplatesRepository;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.openide.util.Lookup;

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
        InputHelperDialog owner = InputHelperDialog.INSTANCE;
        InputDialog dlg = new InputDialog(owner);
        String info = Bundle.getString(MetadataTemplateHelper.class, "MetadataTemplateHelper.Info.InputName");

        dlg.setInfo(info);

        if (fromName != null) {
            dlg.setInput(fromName);
        }

        MetadataTemplatesRepository repo = Lookup.getDefault().lookup(MetadataTemplatesRepository.class);

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
                String message = Bundle.getString(MetadataTemplateHelper.class, "MetadataTemplateHelper.Error.NamEquals");

                if (!MessageDisplayer.confirmYesNo(null, message)) {
                    return null;
                }
            }

            if (!namesEqual && repo.existsMetadataTemplate(name)) {
                String message = Bundle.getString(MetadataTemplateHelper.class, "MetadataTemplateHelper.Error.NameExists", name);

                if (!MessageDisplayer.confirmYesNo(null, message)) {
                    return null;
                }
            } else {
                return name;
            }
        }
    }

    private MetadataTemplateHelper() {
    }
}
