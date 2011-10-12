package org.jphototagger.plugin.iviewsshow;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.FileChooserHelper;
import org.jphototagger.lib.swing.FileChooserProperties;
import org.jphototagger.lib.io.filefilter.AcceptExactFilenamesFileFilter;
import org.jphototagger.lib.util.Bundle;

/**
 * @author  Elmar Baumann
 */
final class IrfanViewSlideshowCommand {

    private String irfanViewExecutablePath;
    private static final String PROGRAM_FILES_DIRECTORY_PATH = System.getenv("ProgramFiles");
    private boolean isReloadOnLoop;
    private static final Set<String> VALID_IRFAN_VIEW_EXECUTABLE_NAMES = new HashSet<String>();

    static {
        VALID_IRFAN_VIEW_EXECUTABLE_NAMES.add("i_view32.exe");
    }

    IrfanViewSlideshowCommand() {
        setReloadOnLoop();
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    private void setReloadOnLoop() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        isReloadOnLoop = prefs.getBoolean(IrfanViewSlideshowUserPreferencesKeys.KEY_RELOAD_ON_LOOP);
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void userPropertyChanged(PreferencesChangedEvent evt) {
        String propertyKey = evt.getKey();

        if (IrfanViewSlideshowUserPreferencesKeys.KEY_RELOAD_ON_LOOP.equals(propertyKey)) {
            isReloadOnLoop = (Boolean) evt.getNewValue();
        }
    }

    String getCommandForFile(String filepath) {
        if (!ensureExecutablePathExists()) {
            return null;
        }

        String reloadOnLoop = isReloadOnLoop ? " /reloadonloop" : "";
        String pattern = "{0} /slideshow=\"{1}\"{2}";
        String command = MessageFormat.format(pattern, irfanViewExecutablePath, filepath, reloadOnLoop);

        return command;
    }

    private boolean ensureExecutablePathExists() {
        if (irfanViewExecutablePath != null) {
            return true;
        }

        irfanViewExecutablePath = lookupRepositoryForExecutablePath();

        if (irfanViewExecutablePath != null) {
            return true;
        }

        irfanViewExecutablePath = chooseIrfanViewExcecutable();

        return irfanViewExecutablePath != null;
    }

    private String lookupRepositoryForExecutablePath() {
        ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);
        List<Program> programs = repo.findAllPrograms(ProgramType.PROGRAM);

        for (Program program : programs) {
            File programFile = program.getFile();
            boolean irfanViewProgramExists = VALID_IRFAN_VIEW_EXECUTABLE_NAMES.contains(programFile.getName());

            if (irfanViewProgramExists) {
                return programFile.getAbsolutePath();
            }
        }

        return null;
    }

    private String chooseIrfanViewExcecutable() {
        File irfanViewExecutable = chooseIrfanViewExecutable();

        if (irfanViewExecutable == null) {
            return null;
        }

        ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);
        Program program = new Program(irfanViewExecutable, "IrfanView");
        repo.saveProgram(program);

        return irfanViewExecutable.getAbsolutePath();
    }

    private File chooseIrfanViewExecutable() {
        if (!MessageDisplayer.confirmYesNo(null, Bundle.getString(IrfanViewSlideshowCommand.class, "IrfanViewSlideshowCommand.Confirm.ChooseExecutable"))) {
            return null;
        }

        FileChooserProperties fcProps = new FileChooserProperties();

        fcProps.dialogTitle(Bundle.getString(IrfanViewSlideshowCommand.class, "IrfanViewSlideshowCommand.FileChooser.Title"));
        fcProps.currentDirectoryPath(PROGRAM_FILES_DIRECTORY_PATH == null ? "" : PROGRAM_FILES_DIRECTORY_PATH);
        fcProps.multiSelectionEnabled(false);
        fcProps.fileFilter(createFileFilter());
        fcProps.fileSelectionMode(JFileChooser.FILES_ONLY);

        return FileChooserHelper.chooseFile(fcProps);
    }

    private FileFilter createFileFilter() {
        AcceptExactFilenamesFileFilter filter = new AcceptExactFilenamesFileFilter(VALID_IRFAN_VIEW_EXECUTABLE_NAMES);
        String fileFilterDescription = Bundle.getString(IrfanViewSlideshowCommand.class, "IrfanViewSlideshowCommand.FileFilterDescription");

        return filter.forFileChooser(fileFilterDescription);
    }
}
