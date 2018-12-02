package org.jphototagger.program.module.keywords;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.swing.KeyStroke;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;

/**
 * Model for adding Keywords via Shortcuts.
 *
 * @author Elmar Baumann
 */
public final class AddKeywortsViaShortcutsModel {

    /**
     * Delimiter between multiple keywords.
     */
    public static final String DELIMITER = ";"; // When changing, check Bundle key "AddKeywordsViaShortcutsController.LabelInfo.Text" (uses full name instead of character, so that it can't be done automatically)

    public enum KeywordNumber {
        Keyword0(KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK), "AddKeywordsViaShortcutsAction.Keyword.0", "AddKeywordsViaShortcuts.Keyword.0"),
        Keyword1(KeyStroke.getKeyStroke(KeyEvent.VK_1, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK), "AddKeywordsViaShortcutsAction.Keyword.1", "AddKeywordsViaShortcuts.Keyword.1"),
        Keyword2(KeyStroke.getKeyStroke(KeyEvent.VK_2, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK), "AddKeywordsViaShortcutsAction.Keyword.2", "AddKeywordsViaShortcuts.Keyword.2"),
        Keyword3(KeyStroke.getKeyStroke(KeyEvent.VK_3, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK), "AddKeywordsViaShortcutsAction.Keyword.3", "AddKeywordsViaShortcuts.Keyword.3"),
        Keyword4(KeyStroke.getKeyStroke(KeyEvent.VK_4, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK), "AddKeywordsViaShortcutsAction.Keyword.4", "AddKeywordsViaShortcuts.Keyword.4"),
        Keyword5(KeyStroke.getKeyStroke(KeyEvent.VK_5, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK), "AddKeywordsViaShortcutsAction.Keyword.5", "AddKeywordsViaShortcuts.Keyword.5"),
        Keyword6(KeyStroke.getKeyStroke(KeyEvent.VK_6, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK), "AddKeywordsViaShortcutsAction.Keyword.6", "AddKeywordsViaShortcuts.Keyword.6"),
        Keyword7(KeyStroke.getKeyStroke(KeyEvent.VK_7, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK), "AddKeywordsViaShortcutsAction.Keyword.7", "AddKeywordsViaShortcuts.Keyword.7"),
        Keyword8(KeyStroke.getKeyStroke(KeyEvent.VK_8, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK), "AddKeywordsViaShortcutsAction.Keyword.8", "AddKeywordsViaShortcuts.Keyword.8"),
        Keyword9(KeyStroke.getKeyStroke(KeyEvent.VK_9, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK), "AddKeywordsViaShortcutsAction.Keyword.9", "AddKeywordsViaShortcuts.Keyword.9");

        private final KeyStroke keyStroke;
        private final String actionMapKey;
        private final String persistenceKey;
        private final String displayName;

        private KeywordNumber(KeyStroke keyStroke, String actionMapKey, String persistenceKey) {
            this.keyStroke = keyStroke;
            this.actionMapKey = actionMapKey;
            this.persistenceKey = persistenceKey;
            this.displayName = KeyEvent.getKeyModifiersText(keyStroke.getModifiers()) + "+" + KeyEvent.getKeyText(keyStroke.getKeyCode());
        }

        public KeyStroke getKeyStroke() {
            return keyStroke;
        }

        public String getActionMapKey() {
            return actionMapKey;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Persists keywords.
     *
     * @param keywordNumber number
     * @param keywords      keywords to persist for this number. Null or empty
     *                      for deleting keywords for this number.
     */
    public void save(KeywordNumber keywordNumber, String keywords) {
        Objects.requireNonNull(keywordNumber, "keywordNumber == null");
        Objects.requireNonNull(keywords, "keywords == null");

        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        if (StringUtil.hasContent(keywords)) {
            prefs.setString(keywordNumber.persistenceKey, keywords);
        }
    }

    /**
     * @param keywordNumber
     *
     * @return Keywords for this number or an empty Collection
     */
    public Collection<String> load(KeywordNumber keywordNumber) {
        Objects.requireNonNull(keywordNumber, "keywordNumber == null");

        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        String prefsKeywords = prefs.getString(keywordNumber.persistenceKey);

        if (!prefs.containsKey(keywordNumber.persistenceKey) || !StringUtil.hasContent(prefsKeywords)) {
            return Collections.emptyList();
        }

        Collection<String> keywords = new ArrayList<>();

        for (String keyword : prefsKeywords.split(DELIMITER)) {
            if (StringUtil.hasContent(keyword)) {
                keywords.add(keyword);
            }
        }

        return keywords;
    }

    public String loadSingleString(KeywordNumber keywordNumber) {
        Objects.requireNonNull(keywordNumber, "keywordNumber == null");

        Collection<String> keywords = load(keywordNumber);

        return keywords.isEmpty()
                ? null
                : CollectionUtil.toTokenString(keywords, DELIMITER, DELIMITER);
    }
}
