package org.jphototagger.api.preferences;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class CommonPreferences {

    public static final String PREF_KEY_FONT_SCALE = "JPhotoTaggerFontScale";
    private static final float RECOMMENDED_FONT_SCALE = calculateRecommendedFontScale();
    private static final Collection<Float> VALID_FONT_SCALES = Arrays.asList(1.0f, 1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.75f, 2.0f, 2.25f, 2.5f, 3.0f);

    public static float getFontScale() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs.containsKey(PREF_KEY_FONT_SCALE)) {
            try {
                String scalePref = prefs.getString(PREF_KEY_FONT_SCALE);
                float scale = Float.valueOf(scalePref);
                return isValidFontScale(scale)
                        ? scale
                        : 1.0f;
            } catch (Throwable t) {
                Logger.getLogger(CommonPreferences.class.getName()).log(Level.SEVERE, null, t);
                return 1.0f;
            }
        } else {
            return RECOMMENDED_FONT_SCALE;
        }
    }

    private static float calculateRecommendedFontScale() {
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            double width = screenSize.getWidth();
            double height = screenSize.getHeight();

            return width > 3840 && height > 2160 // Obove 4K
                    ? 3
                    : width >= 3840 && height >= 2160 // Ultra High Definition "4K" 3840 x 2160 pixels
                    ? 2
                    : width > 2560 && height > 1440 // Wide QHD (QHD) 2560 x 1440 pixels
                    ? 1.5f
                    : 1;
        } catch (Throwable t) {
            Logger.getLogger(CommonPreferences.class.getName()).log(Level.SEVERE, null, t);
            return 1;
        }
    }

    public static void persistFontScale(float scale) {
        if (isValidFontScale(scale)) {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            prefs.setString(PREF_KEY_FONT_SCALE, String.valueOf(scale));
        }
    }

    public static boolean isValidFontScale(float scale) {
        return VALID_FONT_SCALES.contains(scale);
    }

    public static Float[] getValidFontScales() {
        return VALID_FONT_SCALES.toArray(new Float[VALID_FONT_SCALES.size()]);
    }

    private CommonPreferences() {
    }
}
