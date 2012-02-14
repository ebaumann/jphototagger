package org.jphototagger.program.module.wordsets;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.settings.AppPreferencesKeys;

/**
 * @author Elmar Baumann
 */
public final class WordsetPreferences {

    public static final String AUTOMATIC_WORDSET_NAME = "JPhotoTagger Automatic Wordset";
    public static final String KEY_PREFIX_AUTOMATIC_WORDSET_WORD = "AutomaticWordset.Word.";

    public static boolean isAutomaticWordsetName(String string) {
        if (!StringUtil.hasContent(string)) {
            return false;
        }
        return AUTOMATIC_WORDSET_NAME.equals(string.trim());
    }

    public static boolean isDisplayWordsetsEditPanel() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null && prefs.containsKey(AppPreferencesKeys.KEY_UI_DISPLAY_WORD_SETS_EDIT_PANEL)) {
            return prefs.getBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_WORD_SETS_EDIT_PANEL);
        }
        return true;
    }

    private WordsetPreferences() {
    }
}
