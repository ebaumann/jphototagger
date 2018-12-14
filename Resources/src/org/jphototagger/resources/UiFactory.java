package org.jphototagger.resources;

import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
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

    public static JMenuItem menuItem() {
        JMenuItem mi = new JMenuItem();

        configure(mi);

        return mi;
    }

    public static JMenuItem menuItem(String text) {
        JMenuItem mi = new JMenuItem(text);

        configure(mi);

        return mi;
    }

    public static JMenuItem menuItem(String text, Icon icon) {
        JMenuItem mi = new JMenuItem(text, icon);

        configure(mi);

        return mi;
    }

    public static JMenuItem menuItem(Action a) {
        JMenuItem mi = new JMenuItem(a);

        configure(mi);

        return mi;
    }

    public static JMenuItem checkBoxMenuItem(Action a) {
        JCheckBoxMenuItem mi = new JCheckBoxMenuItem(a);

        configure(mi);

        return mi;
    }

    public static JRadioButtonMenuItem radioButtonMenuItem() {
        JRadioButtonMenuItem mi = new JRadioButtonMenuItem();

        configure(mi);

        return mi;
    }

    public static JLabel label() {
        JLabel l = new JLabel();

        configure(l);

        return l;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);

        configure(l);

        return l;
    }

    public static JLabel label(String text, int horizontalAlignment) {
        JLabel l = new JLabel(text, horizontalAlignment);

        configure(l);

        return l;
    }

    private static void configure(JLabel l) {
        l.setIconTextGap(scale(l.getIconTextGap()));
    }

    public static JTabbedPane tabbedPane() {
        JTabbedPane tp = new JTabbedPane();

        configure(tp);

        return tp;
    }

    private static void configure(JTabbedPane tp) {
        // For future usage
    }

    private UiFactory() {
    }
}
