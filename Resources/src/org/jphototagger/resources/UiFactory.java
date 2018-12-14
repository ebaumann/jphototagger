package org.jphototagger.resources;

import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
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

    public static JCheckBox checkBox() {
        JCheckBox cb = new JCheckBox();

        configure(cb);

        return cb;
    }

    public static JCheckBox checkBox(String text) {
        JCheckBox cb = new JCheckBox(text);

        configure(cb);

        return cb;
    }

    public static JCheckBox checkBox(Action a) {
        JCheckBox cb = new JCheckBox(a);

        configure(cb);

        return cb;
    }

    private static void configure(AbstractButton b) {
        b.setIconTextGap(scale(b.getIconTextGap()));

        Insets margin = b.getMargin();
        b.setMargin(new Insets(
                scale(margin.top),
                scale(margin.left),
                scale(margin.bottom),
                scale(margin.right)));
    }

    public static JRadioButton radioButton() {
        JRadioButton rb = new JRadioButton();

        configure(rb);

        return rb;
    }

    public static JButton button() {
        JButton b = new JButton();

        configure(b);

        return b;
    }

    public static JButton button(String text) {
        JButton b = new JButton(text);

        configure(b);

        return b;
    }

    public static JButton button(Action a) {
        JButton b = new JButton(a);

        configure(b);

        return b;
    }

    private UiFactory() {
    }
}
