package org.jphototagger.program.module.programs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;

import org.openide.util.Lookup;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class OpenFilesWithStandardAppController implements ActionListener {

    private final ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);

    public OpenFilesWithStandardAppController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemOpenFilesWithStandardApp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        openSelectedImages();
    }

    private void openSelectedImages() {
        Map<String, List<File>> selectedImageFilesWithSuffix =
                FileUtil.getFilesWithSuffixIgnoreCase(GUI.getSelectedImageFiles());
        if (selectedImageFilesWithSuffix.isEmpty()) {
            return;
        }
        for (String suffix : selectedImageFilesWithSuffix.keySet()) {
            List<File> filesOfSuffix = selectedImageFilesWithSuffix.get(suffix);
            if (filesOfSuffix.isEmpty()) {
                continue;
            }
            File file = CollectionUtil.getFirstElement(filesOfSuffix);
            Program program = findDefaultImageOpenProgram(file);
            if (program == null) {
                String message = Bundle.getString(OpenFilesWithOtherAppController.class, "OpenFilesWithStandardAppController.Info.DefineOpenApp");
                MessageDisplayer.information(null, message);
                ProgramsUtil.openSelectedFilesWidth(ProgramsUtil.addProgram(), false);
            } else {
                StartPrograms startPrograms = new StartPrograms();
                boolean waitForTermination = false;
                startPrograms.startProgram(program, filesOfSuffix, waitForTermination);
            }
        }
    }

    private Program findDefaultImageOpenProgram(File file) {
        String filenameSuffix = FileUtil.getSuffix(file);
        if (!StringUtil.hasContent(filenameSuffix)) {
            return null;
        }
        Program program = repo.findDefaultProgram(filenameSuffix);
        return program == null
                ? repo.findDefaultImageOpenProgram()
                : program;

    }
}
