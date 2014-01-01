package org.jphototagger.lib.io;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import org.jphototagger.api.applifecycle.generics.Functor;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;

/**
 * Persists to {@link Preferences} selected files.
 *
 * @author Elmar Baumann
 */
public final class PreviousSelectedFiles {

    private final String preferencesKey;
    private final int maxCount;
    private final Preferences preferences = Lookup.getDefault().lookup(Preferences.class);

    public PreviousSelectedFiles(String preferencesKey, int maxCount) {
        if (preferencesKey == null) {
            throw new NullPointerException("preferencesKey == null");
        }
        if (maxCount < 1) {
            throw new IllegalArgumentException("Maximum count has to be 1 or greater and not " + maxCount);
        }
        this.preferencesKey = preferencesKey;
        this.maxCount = maxCount;
    }

    /**
     * @param file null is ok (will not be added). If max count is reached, the eldest entry will be removed (expected,
     * the Preferences returning files in the same order as persisted)
     */
    public void add(File file) {
        if (file == null) {
            return;
        }
        LinkedList<File> files = new LinkedList<>(getFiles());
        files.addFirst(file);
        if (files.size() > maxCount) {
            files.removeLast();
        }
        setFiles(files);
    }

    public void setFiles(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        int countAdded = 0;
        Set<String> pathnames = new LinkedHashSet<>(files.size()); // avoiding duplicates
        for (File file : files) {
            if (countAdded <= maxCount) {
                pathnames.add(file.getAbsolutePath());
                countAdded++;
            } else {
                break;
            }
        }
        preferences.setStringCollection(preferencesKey, pathnames);
    }

    /**
     * @return all persisted files or empty list
     */
    public List<File> getFiles() {
        return getFiles(false);
    }

    /**
     * @return persisted still existing files or empty list
     */
    public List<File> getExistingFiles() {
        return getFiles(true);
    }

    private List<File> getFiles(boolean onlyExisting) {
        List<String> pathnames = preferences.getStringCollection(preferencesKey);
        Set<File> files = new LinkedHashSet<>(pathnames.size()); // avoiding duplicates
        int countAdded = 0;
        for (int i = 0; i < pathnames.size() && countAdded <= maxCount; i++) {
            File file = new File(pathnames.get(i));
            if (!onlyExisting || file.exists()) {
                files.add(file);
                countAdded++;
            }
        }
        return new ArrayList<>(files);
    }

    public int getExistingFileCount() {
        return getExistingFiles().size();
    }

    /**
     * @param functor {@link Functor#execute(java.lang.Object)} with a file as argument will be called when a menu item
     * was selected
     * @param skip optional files not to add as menu item
     * @return popup menu with existing file items displaying the file paths
     */
    public JPopupMenu createPopupMenu(Functor<File> functor, File... skip) {
        if (functor == null) {
            throw new NullPointerException("functor == null");
        }
        Set<File> skipFiles = skip == null || skip.length == 0 ? Collections.<File>emptySet() : new HashSet<>(Arrays.asList(skip));
        JPopupMenu popupMenu = new JPopupMenu();
        for (File file : getExistingFiles()) {
            if (!skipFiles.contains(file)) {
                popupMenu.add(new FileFunctorAction(file, functor));
            }
        }
        return popupMenu;
    }

    private static final class FileFunctorAction extends AbstractAction {

        private static final int MAX_NAME_LENGTH = 125;
        private static final long serialVersionUID = 1L;
        private final File file;
        private final Functor<File> functor;

        private FileFunctorAction(File file, Functor<File> functor) {
            super(createDisplayname(file));
            this.file = file;
            this.functor = functor;
        }

        private static String createDisplayname(File file) {
            String pathname = file.getAbsolutePath();
            return pathname.length() <= MAX_NAME_LENGTH
                    ? pathname
                    : StringUtil.getPrefixDotted(pathname, MAX_NAME_LENGTH);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            functor.execute(file);
        }

    }

    public void clear() {
        preferences.removeStringCollection(preferencesKey);
    }
}
