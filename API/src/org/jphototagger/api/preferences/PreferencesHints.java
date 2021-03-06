package org.jphototagger.api.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @author Elmar Baumann
 */
public final class PreferencesHints {

    private final List<String> excludedKeys = new ArrayList<>();
    private final Set<Option> options;

    public enum Option {

        /**
         * All components of the tabbed pane shall be set recursively.
         * Default: false.
         */
        SET_TABBED_PANE_CONTENT,;
    }

    public PreferencesHints() {
        options = EnumSet.noneOf(Option.class);
    }

    public PreferencesHints(Option... options) {
        if (options == null) {
            throw new NullPointerException("options == null");
        }

        this.options = (options.length == 0)
                ? EnumSet.noneOf(Option.class)
                : EnumSet.copyOf(Arrays.asList(options));
    }

    public void addKeyToExclude(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        excludedKeys.add(key);
    }

    public boolean isExclude(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        return excludedKeys.contains(key);
    }

    public boolean isOption(Option option) {
        if (option == null) {
            throw new NullPointerException("option == null");
        }

        return options.contains(option);
    }
}
