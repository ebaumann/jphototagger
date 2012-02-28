package org.jphototagger.fileeventhooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;

/**
 * @author Elmar Baumann
 */
public final class FilenameSuffixesListModel extends DefaultListModel {

    private static final long serialVersionUID = 1L;

    public FilenameSuffixesListModel() {
        addElements();
    }

    private void addElements() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null) {
            Set<String> suffixes = new HashSet<String>(prefs.getStringCollection(FileEventHooksPreferencesKeys.FILENAME_SUFFIXES_KEY));
            for (String suffix : suffixes) {
                addElement(suffix);
            }
        }
    }

    public void addSuffix(String suffix) {
        if (suffix == null) {
            throw new NullPointerException("suffix == null");
        }
        String trimmedSuffix = suffix.trim();
        if (trimmedSuffix.length() > 1 && trimmedSuffix.startsWith(".")) {
            trimmedSuffix = trimmedSuffix.replaceFirst("[\\.]+", "");
        }
        if (!trimmedSuffix.isEmpty() && !contains(trimmedSuffix) && !"xmp".equalsIgnoreCase(suffix)) {
            addElement(trimmedSuffix);
            updatePreferences();
        }
    }

    public void removeSuffix(String suffix) {
        if (suffix == null) {
            throw new NullPointerException("suffix == null");
        }
        boolean removed = removeElement(suffix);
        if (removed) {
            updatePreferences();
        }
    }

    public boolean renameSuffix(String oldName, String newName) {
        if (oldName == null) {
            throw new NullPointerException("oldName == null");
        }
        if (newName == null) {
            throw new NullPointerException("newName == null");
        }
        if (oldName.equals(newName)) {
            return false;
        }
        int oldNameIndex = indexOf(oldName);
        if (oldNameIndex >= 0) {
            set(oldNameIndex, newName);
            updatePreferences();
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void updatePreferences() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setStringCollection(FileEventHooksPreferencesKeys.FILENAME_SUFFIXES_KEY,
                (ArrayList<String>) Collections.list(elements()));
    }
}
