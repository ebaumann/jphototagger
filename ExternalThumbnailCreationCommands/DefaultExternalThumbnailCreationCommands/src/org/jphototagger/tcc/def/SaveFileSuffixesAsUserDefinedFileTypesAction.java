package org.jphototagger.tcc.def;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class SaveFileSuffixesAsUserDefinedFileTypesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final Collection<UserDefinedFileType> filetypes;

    protected SaveFileSuffixesAsUserDefinedFileTypesAction(Collection<? extends UserDefinedFileType> filetypes) {
        this.filetypes =  new ArrayList<UserDefinedFileType>(filetypes);
    }

    static UserDefinedFileType createUserDefinedFileType(String suffix, String description) {
        UserDefinedFileType fileType = new UserDefinedFileType();
        fileType.setSuffix(suffix);
        fileType.setDescription(description);
        fileType.setExternalThumbnailCreator(true);
        return fileType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (confirmSave()) {
            UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);
            int saveCount = 0;
            for (UserDefinedFileType filetype : filetypes) {
                if (!repo.existsUserDefinedFileTypeWithSuffix(filetype.getSuffix())) {
                    repo.saveUserDefinedFileType(filetype);
                    saveCount++;
                }
            }
            showInfoAfterSave(saveCount);
        }
    }

    private boolean confirmSave() {
        String message = Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "SaveFileSuffixesAsUserDefinedFileTypesAction.ConfirmSave.Message");
        String title = Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "SaveFileSuffixesAsUserDefinedFileTypesAction.ConfirmSave.Title");
        int result = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    private void showInfoAfterSave(int saveCount) {
        String pattern = Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "SaveFileSuffixesAsUserDefinedFileTypesAction.InfoAfterSave.MessageTemplate");
        String title = Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "SaveFileSuffixesAsUserDefinedFileTypesAction.InfoAfterSave.Title");
        String message = MessageFormat.format(pattern, saveCount);
        int messageType = JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(null, message, title, messageType);
    }
}
