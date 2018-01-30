package org.jphototagger.program.module.fileexcludepatterns;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.lib.util.SystemProperties;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class FileExcludePatternsController {

    private static final String KEY_START_DIR = "";

    /**
     * @return Directory-Regex-Patterns from directories chosen via a directory
     *         chooser.
     */
    public Collection<String> chooseDirectoryPatterns() {
        DirectoryChooser chooser = createDirChooser();

        chooser.setVisible(true);
        if (chooser.isAccepted()) {
            List<File> selDirs = chooser.getSelectedDirectories();

            persistStartDir(selDirs);
            return createPatterns(selDirs);
        } else {
            return Collections.emptyList();
        }
    }

    private Collection<String> createPatterns(List<File> selDirs) {
        Collection<String> result = new ArrayList<>(selDirs.size());

        for (File selDir : selDirs) {
            String path = selDir.getAbsolutePath();
            String quotedPath = Pattern.quote(path + File.separator);

            result.add(quotedPath + ".*");
        }

        return result;
    }

    private DirectoryChooser createDirChooser() {
        DirectoryChooser chooser = new DirectoryChooser(
                ComponentUtil.findFrameWithIcon(),
                getStartDir(),
                getDirChooserOptions()
                );

        chooser.setTitle(Bundle.getString(FileExcludePatternsController.class, "FileExcludePatternsController.DirChooser.Title"));
        chooser.setModal(true);

        return chooser;
    }

    private DirectoryChooser.Option[] getDirChooserOptions() {
        return isAcceptHiddenDirectories()
                ? new DirectoryChooser.Option[] {
                    DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES,
                    DirectoryChooser.Option.MULTI_SELECTION}
                : new DirectoryChooser.Option[] {
                    DirectoryChooser.Option.NO_OPTION,
                    DirectoryChooser.Option.MULTI_SELECTION};
    }

    private boolean isAcceptHiddenDirectories() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? prefs.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    private void persistStartDir(List<File> selDirs) {
        if (selDirs.isEmpty()) {
            return;
        }

        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setString(KEY_START_DIR, selDirs.get(0).getAbsolutePath());
    }

    private File getStartDir() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        String dir = prefs.getString(KEY_START_DIR);

        return StringUtil.hasContent(dir)
                ? new File(dir)
                : new File(SystemProperties.getUserHome());
    }
}
