package org.jphototagger.resources;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Objects;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTree;
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

    public static JToggleButton toggleButton() {
        JToggleButton tb = new JToggleButton();

        configure(tb);

        return tb;
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

    public static JMenuBar menuBar() {
        JMenuBar mb = new JMenuBar();

        Insets insets = mb.getInsets();
        mb.setMargin(insets(insets.top, insets.left, insets.bottom, insets.right));

        return mb;
    }

    public static JMenu menu() {
        JMenu m = new JMenu();

        configure(m);

        return m;
    }

    public static JMenu menu(String s) {
        JMenu m = new JMenu(s);

        configure(m);

        return m;
    }

    public static JPopupMenu popupMenu() {
        JPopupMenu p = new JPopupMenu();

        configure(p);

        return p;
    }

    public static void configure(JPopupMenu p) {
        Objects.requireNonNull(p, "p == null");
        // For future usage
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

    public static JXLabel jxLabel() {
        JXLabel l = new JXLabel();

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

    public static JSplitPane splitPane() {
        JSplitPane sp = new JSplitPane();

        configure(sp);

        return sp;
    }

    private static void configure(JSplitPane sp) {
        // For future usage
    }

    public static JScrollPane scrollPane() {
        JScrollPane sp = new JScrollPane();

        configure(sp);

        return sp;
    }

    private static void configure(JScrollPane sp) {
        int inc = scale(16);
        sp.getHorizontalScrollBar().setUnitIncrement(inc);
        sp.getVerticalScrollBar().setUnitIncrement(inc);
    }

    public static JTable table() {
        JTable table = new JTable();

        configure(table);

        return table;
    }

    private static void configure(JTable table) {
        // For future usage
    }

    public static JTextField textField() {
        JTextField tf = new JTextField();

        configure(tf);

        return tf;
    }

    private static void configure(JTextField tf) {
        // For future Usage
    }

    public static JFormattedTextField formattedTextField() {
        JFormattedTextField tf = new JFormattedTextField();

        configure(tf);

        return tf;
    }

    private static void configure(JFormattedTextField tf) {
        // For future usage
    }

    public static JTextArea textArea() {
        JTextArea ta = new JTextArea();

        configure(ta);

        return ta;
    }

    public static void configure(JTextArea ta) {
        Objects.requireNonNull(ta, "ta == null");
        // For future Usage
    }

    public static JTextPane textPane() {
        JTextPane tp = new JTextPane();

        configure(tp);

        return tp;
    }

    private static void configure(JTextPane tp) {
        // For future Usage
    }

    public static JEditorPane editorPane() {
        JEditorPane ep = new JEditorPane();

        configure(ep);

        return ep;
    }

    private static void configure(JEditorPane ep) {
        // For future Usage
    }

    public static JPanel panel() {
        JPanel jp = new JPanel();

        configure(jp);

        return jp;
    }

    public static JPanel panel(LayoutManager lm) {
        JPanel jp = new JPanel(lm);

        configure(jp);

        return jp;
    }

    public static void configure(JPanel jp) {
        Objects.requireNonNull(jp, "jp == null");
        // For future usages
    }

    public static JTree tree() {
        JTree t = new JTree();

        configure(t);

        return t;
    }

    private static void configure(JTree t) {
        // For future usage
    }

    public static JXTree jxTree() {
        JXTree t = new JXTree();

        configure(t);

        return t;
    }

    public static void configure(JXTree t) {
        Objects.requireNonNull(t, "t == null");
        // For future usage
    }

    public static JXList jxList() {
        JXList l = new JXList();

        configureJx(l);

        return l;
    }

    public static void configureJx(JXList l) {
        Objects.requireNonNull(l, "l == null");
        // For future usage
    }

    public static void configure(JComboBox<?> cb) {
        Objects.requireNonNull(cb, "cb == null");
        // For future usage
    }

    public static void configure(JList<?> l) {
        Objects.requireNonNull(l, "l == null");
        // For future usage
    }

    public static JSlider slider() {
        JSlider s = new JSlider();

        configure(s);

        return s;
    }

    private static void configure(JSlider s) {
        // For future usage
    }

    public static JSpinner spinner() {
        JSpinner s = new JSpinner();

        configure(s);

        return s;
    }

    private static void configure(JSpinner s) {
        // For future usage
    }

    public static JProgressBar progressBar() {
        JProgressBar pb = new JProgressBar();

        configure(pb);

        return pb;
    }

    private static void configure(JProgressBar pb) {
        // For future usage
    }

    public static void configure(JDialog d) {
        Objects.requireNonNull(d, "d == null");
        // For future usage
    }

    public static void configure(JFrame f) {
        Objects.requireNonNull(f, "f == null");
        // For future usage
    }

    private UiFactory() {
    }
}
