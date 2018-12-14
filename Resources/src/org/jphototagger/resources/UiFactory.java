package org.jphototagger.resources;

import java.awt.Dimension;
import java.awt.Insets;
import org.jphototagger.api.preferences.CommonPreferences;

/**
 * @author Elmar Baumann
 */
public final class UiFactory {

    private static final double SCALE_FACTOR;
    private static final boolean IS_SCALE;

    static {
        SCALE_FACTOR = (double) CommonPreferences.getFontScale();
        IS_SCALE = Double.compare(SCALE_FACTOR, 1.0) != 0;
    }

    /**
     * Creates a scaled Dimension.
     *
     * @param width
     * @param height
     *
     * @return
     */
    public static Dimension dimension(int width, int height) {
        return IS_SCALE
                ? new Dimension(scale(width), scale(height))
                : new Dimension(width, height);
    }

    /**
     * Creates scaled Insets.
     *
     * @param top
     * @param left
     * @param bottom
     * @param right
     *
     * @return
     */
    public static Insets insets(int top, int left, int bottom, int right) {
        return IS_SCALE
                ? new Insets(scale(top), scale(left), scale(bottom), scale(right))
                : new Insets(top, left, bottom, right);
    }

    /**
     * Scales a width.
     *
     * @param width
     *
     * @return
     */
    public static int scale(int width) {
        return IS_SCALE
                ? (int) ((double) width * SCALE_FACTOR)
                : width;
    }

    private UiFactory() {
    }
}
