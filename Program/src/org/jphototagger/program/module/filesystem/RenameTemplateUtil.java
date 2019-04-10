package org.jphototagger.program.module.filesystem;

import java.awt.Component;
import org.jphototagger.domain.repository.RenameTemplatesRepository;
import org.jphototagger.domain.templates.RenameTemplate;
import org.jphototagger.lib.swing.InputDialog;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class RenameTemplateUtil {

    /**
     * @param template
     * @return true if inserted into the repository
     */
    public static boolean insert(RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }
        boolean saved = false;
        String name = getUniqueName(null);
        if (name != null) {
            template.setName(name);
            RenameTemplatesRepository repo = Lookup.getDefault().lookup(RenameTemplatesRepository.class);
            saved = repo.saveRenameTemplate(template);
            if (!saved) {
                String message = Bundle.getString(RenameTemplateUtil.class, "RenameTemplateHelper.Error.Insert", template);
                MessageDisplayer.error(null, message);
            }
        }
        return saved;
    }

    /**
     * @param template
     * @return true if updated in the repository
     */
    public static boolean update(RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }
        RenameTemplatesRepository repo = Lookup.getDefault().lookup(RenameTemplatesRepository.class);
        boolean updated = repo.updateRenameTemplate(template);
        if (!updated) {
            String message = Bundle.getString(RenameTemplateUtil.class, "RenameTemplateHelper.Error.Update", template);
            MessageDisplayer.error(null, message);
        }
        return updated;
    }

    /**
     * @param template
     * @return true if renamed within the repository
     */
    public static boolean rename(RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }
        String name = getUniqueName(template.getName());
        if (name != null) {
            template.setName(name);
            return update(template);
        }
        return false;
    }

    /**
     * @param template
     * @return true if deleted from the repository
     */
    public static boolean delete(RenameTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }
        Component parentComponent = null;
        String message = Bundle.getString(RenameTemplateUtil.class, "RenameTemplateHelper.Confirm.Delete", template);
        RenameTemplatesRepository repo = Lookup.getDefault().lookup(RenameTemplatesRepository.class);
        boolean deleted = false;
        if (MessageDisplayer.confirmYesNo(parentComponent, message)) {
            deleted = repo.deleteRenameTemplate(template.getName()) >= 1;
            if (!deleted) {
                message = Bundle.getString(RenameTemplateUtil.class, "RenameTemplateHelper.Error.Delete", template);
                MessageDisplayer.error(null, message);
            }
        }
        return deleted;
    }

    private static String getUniqueName(String suggest) {
        String info = Bundle.getString(RenameTemplateUtil.class, "RenameTemplateHelper.Input.Name");
        String input = (suggest == null) ? "" : suggest;
        InputDialog dlg = new InputDialog(info, input);
        dlg.setModal(true);
        dlg.setAlwaysOnTop(true);
        dlg.setVisible(true);
        RenameTemplatesRepository repo = Lookup.getDefault().lookup(RenameTemplatesRepository.class);
        boolean unique = false;
        while (!unique && dlg.isAccepted()) {
            String name = dlg.getInput().trim();
            if (name.isEmpty()) {
                return null;
            }
            unique = !repo.existsRenameTemplate(name);
            Component parentComponent = null;
            String message = Bundle.getString(RenameTemplateUtil.class, "RenameTemplateHelper.Confirm.InputUniqueName", name);
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

    private RenameTemplateUtil() {
    }
}
