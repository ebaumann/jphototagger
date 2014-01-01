package org.jphototagger.program.module.programs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jphototagger.api.file.FileViewer;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileViewer.class)
public final class OpenFilesWithStandardAppController implements ActionListener, FileViewer {

    private final ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);

    public OpenFilesWithStandardAppController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemOpenFilesWithStandardApp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        openFiles(GUI.getSelectedImageFiles());
    }

    private void openFiles(Collection<? extends File> files) {
        Map<String, List<File>> selectedImageFilesWithSuffix = FileUtil.getFilesWithSuffixIgnoreCase(files);
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

    @Override
    public void view(File file) {
        openFiles(Collections.singleton(file));
    }
}
