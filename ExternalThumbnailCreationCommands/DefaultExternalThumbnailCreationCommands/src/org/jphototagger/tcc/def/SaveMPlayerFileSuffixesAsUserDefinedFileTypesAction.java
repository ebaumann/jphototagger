package org.jphototagger.tcc.def;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.openide.util.Lookup;

import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.lib.util.Bundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static final Collection<UserDefinedFileType> VIDEO_FILES = new ArrayList<UserDefinedFileType>(10);
    public static final SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction INSTANCE = new SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction();

    {
        VIDEO_FILES.add(createUserDefinedFileType("avi", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.avi.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("m2ts", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.m2ts.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mov", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mov.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mp2", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mp2.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mp4", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mp4.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mpeg", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mpeg.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mpg", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mpg.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mts", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.mts.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("wmv", Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "FileType.wmv.Description")));
    }

    private static UserDefinedFileType createUserDefinedFileType(String suffix, String description) {
        UserDefinedFileType fileType = new UserDefinedFileType();

        fileType.setSuffix(suffix);
        fileType.setDescription(description);
        fileType.setExternalThumbnailCreator(true);

        return fileType;
    }

    private SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction() {
        putValue(Action.NAME, Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.Name"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (confirmSave()) {
            UserDefinedFileTypesRepository repo = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);
            int saveCount = 0;

            for (UserDefinedFileType userDefinedFileType : VIDEO_FILES) {
                if (!repo.existsUserDefinedFileTypeWithSuffix(userDefinedFileType.getSuffix())) {
                    repo.saveUserDefinedFileType(userDefinedFileType);
                    saveCount++;
                }
            }

            showInfoAfterSave(saveCount);
        }
    }

    private boolean confirmSave() {
        String message = Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.ConfirmSave.Message");
        String title = Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.ConfirmSave.Title");
        int result = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);

        return result == JOptionPane.YES_OPTION;
    }

    private void showInfoAfterSave(int saveCount) {
        String pattern = Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.InfoAfterSave.MessageTemplate");
        String title = Bundle.getString(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.class, "SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.InfoAfterSave.Title");
        String message = MessageFormat.format(pattern, saveCount);
        int messageType = JOptionPane.INFORMATION_MESSAGE;

        JOptionPane.showMessageDialog(null, message, title, messageType);
    }
}
