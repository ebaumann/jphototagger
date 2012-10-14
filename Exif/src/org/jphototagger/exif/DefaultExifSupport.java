package org.jphototagger.exif;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.lib.io.FileUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class DefaultExifSupport {

    public static final String PREF_KEY_EXCLUDE_FROM_READ_SUFFIXES = "ExifSettings.ExcludeFromReadSuffixes";
    public static final DefaultExifSupport INSTANCE = new DefaultExifSupport();
    private static final Set<String> SUPPORTED_SUFFIXES_LOWERCASE = new HashSet<>();
    private static final Set<String> EXCLUDE_SUFFIXES_LOWERCASE = new CopyOnWriteArraySet<>();

    static {
        SUPPORTED_SUFFIXES_LOWERCASE.add("arw");
        SUPPORTED_SUFFIXES_LOWERCASE.add("crw");
        SUPPORTED_SUFFIXES_LOWERCASE.add("cr2");
        SUPPORTED_SUFFIXES_LOWERCASE.add("dcr");
        SUPPORTED_SUFFIXES_LOWERCASE.add("dng");
        SUPPORTED_SUFFIXES_LOWERCASE.add("jpg");
        SUPPORTED_SUFFIXES_LOWERCASE.add("jpeg");
        SUPPORTED_SUFFIXES_LOWERCASE.add("mrw");
        SUPPORTED_SUFFIXES_LOWERCASE.add("nef");
        SUPPORTED_SUFFIXES_LOWERCASE.add("thm");
        SUPPORTED_SUFFIXES_LOWERCASE.add("tif");
        SUPPORTED_SUFFIXES_LOWERCASE.add("tiff");
        SUPPORTED_SUFFIXES_LOWERCASE.add("srw");
        initExcludeSuffixes();
        AnnotationProcessor.process(INSTANCE);
    }

    private static void initExcludeSuffixes() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null) {
            setExcludeSuffixes(prefs.getStringCollection(PREF_KEY_EXCLUDE_FROM_READ_SUFFIXES));
        }
    }

    private static void setExcludeSuffixes(Collection<? extends String> suffixes) {
        EXCLUDE_SUFFIXES_LOWERCASE.clear();
        for (String suffix : suffixes) {
            EXCLUDE_SUFFIXES_LOWERCASE.add(suffix.toLowerCase());
        }
    }

    public static Set<String> getSupportedSuffixes() {
        return Collections.unmodifiableSet(SUPPORTED_SUFFIXES_LOWERCASE);
    }

    public boolean canReadExif(File file) {
        String suffix = FileUtil.getSuffix(file);
        String suffixLowerCase = suffix.toLowerCase();
        return SUPPORTED_SUFFIXES_LOWERCASE.contains(suffixLowerCase) && !EXCLUDE_SUFFIXES_LOWERCASE.contains(suffixLowerCase);
    }

    @EventSubscriber(eventClass=PreferencesChangedEvent.class)
    @SuppressWarnings("unchecked")
    public void preferencesChanged(PreferencesChangedEvent evt) {
        if (PREF_KEY_EXCLUDE_FROM_READ_SUFFIXES.equals(evt.getKey())) {
            setExcludeSuffixes((Collection<String>) evt.getNewValue());
        }
    }

    public static Collection<? extends String> getExludeFilenameSuffixes() {
        return Collections.unmodifiableCollection(EXCLUDE_SUFFIXES_LOWERCASE);
    }

    private DefaultExifSupport() {
    }
}
