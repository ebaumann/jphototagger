package org.jphototagger.dtncreators;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.jphototagger.domain.UserDefinedFileType;
import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.services.repository.UserDefinedFileTypesRepository;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static final ResourceBundle BUNDLE = java.util.ResourceBundle.getBundle("org/jphototagger/dtncreators/Bundle"); // NOI18N;
    private static final Collection<UserDefinedFileType> VIDEO_FILES = new ArrayList<UserDefinedFileType>(10);
    public static final SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction INSTANCE = new SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction();

    {
        VIDEO_FILES.add(createUserDefinedFileType("avi", BUNDLE.getString("FileType.avi.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("m2ts", BUNDLE.getString("FileType.m2ts.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mov", BUNDLE.getString("FileType.mov.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mp2", BUNDLE.getString("FileType.mp2.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mp4", BUNDLE.getString("FileType.mp4.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mpeg", BUNDLE.getString("FileType.mpeg.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mpg", BUNDLE.getString("FileType.mpg.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("mts", BUNDLE.getString("FileType.mts.Description")));
        VIDEO_FILES.add(createUserDefinedFileType("wmv", BUNDLE.getString("FileType.wmv.Description")));
    }

    private static UserDefinedFileType createUserDefinedFileType(String suffix, String description) {
        UserDefinedFileType fileType = new UserDefinedFileType();

        fileType.setSuffix(suffix);
        fileType.setDescription(description);
        fileType.setExternalThumbnailCreator(true);

        return fileType;
    }

    private SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction() {
        putValue(Action.NAME, BUNDLE.getString("SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.Name"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (confirmSave()) {
            UserDefinedFileTypesRepository repo = ServiceLookup.lookup(UserDefinedFileTypesRepository.class);
            int saveCount = 0;

            for (UserDefinedFileType userDefinedFileType : VIDEO_FILES) {
                if (!repo.existsFileTypeWithSuffix(userDefinedFileType.getSuffix())) {
                    repo.save(userDefinedFileType);
                    saveCount++;
                }
            }

            showInfoAfterSave(saveCount);
        }
    }

    private boolean confirmSave() {
        String message = BUNDLE.getString("SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.ConfirmSave.Message");
        String title = BUNDLE.getString("SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.ConfirmSave.Title");
        int result = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);

        return result == JOptionPane.YES_OPTION;
    }

    private void showInfoAfterSave(int saveCount) {
        String pattern = BUNDLE.getString("SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.InfoAfterSave.MessageTemplate");
        String title = BUNDLE.getString("SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.InfoAfterSave.Title");
        String message = MessageFormat.format(pattern, saveCount);
        int messageType = JOptionPane.INFORMATION_MESSAGE;

        JOptionPane.showMessageDialog(null, message, title, messageType);
    }
}
