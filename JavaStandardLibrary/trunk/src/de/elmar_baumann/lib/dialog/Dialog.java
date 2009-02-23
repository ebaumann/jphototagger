package de.elmar_baumann.lib.dialog;

import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * Dialog which can close by pressing the ESC key and showing the Help dialog
 * by pressing the F1 key.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/19
 */
public class Dialog extends JDialog {

    private ActionListener actionListenerEscape;
    private ActionListener actionListenerHelp;
    private final HelpBrowser help = HelpBrowser.INSTANCE;

    protected Dialog() {
        super();
        init();
    }

    public Dialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        init();
    }

    public Dialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);
        init();
    }

    public Dialog(Window owner, String title) {
        super(owner, title);
        init();
    }

    public Dialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
        init();
    }

    public Dialog(Window owner) {
        super(owner);
        init();
    }

    public Dialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
    }

    public Dialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        init();
    }

    public Dialog(Dialog owner, String title) {
        super(owner, title);
        init();
    }

    public Dialog(Dialog owner, boolean modal) {
        super(owner, modal);
        init();
    }

    public Dialog(Dialog owner) {
        super(owner);
        init();
    }

    public Dialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
    }

    public Dialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        init();
    }

    public Dialog(Frame owner, String title) {
        super(owner, title);
        init();
    }

    public Dialog(Frame owner, boolean modal) {
        super(owner, modal);
        init();
    }

    public Dialog(Frame owner) {
        super(owner);
        init();
    }

    /**
     * Sets the contents URL of the help and must be called <em>before</em>
     * {@link #help(java.lang.String)}.
     * 
     * @param url  contents URL
     * @see HelpBrowser#setContentsUrl(java.lang.String)
     */
    protected void setHelpContentsUrl(String url) {
        if (url == null)
            throw new NullPointerException("url == null");
        help.setContentsUrl(url);
    }

    /**
     * This method will be called if the user presses F1. Does nothing.
     * Specialized classes can call {@link #help(java.lang.String)}
     * with an appropriate URL.
     */
    protected void help() {
    }

    /**
     * Shows the help dialog with an specific URL. Previous to this call once
     * {@link #setHelpContentsUrl(java.lang.String)} have to be called.
     * 
     * The dialog is an instance of {@link HelpBrowser}.
     * 
     * @param url  URL to display
     */
    protected void help(String url) {
        if (url == null)
            throw new NullPointerException("url == null");
        if (help.isVisible()) {
            help.showUrl(url);
            help.toFront();
        } else {
            help.setStartUrl(url);
            help.setVisible(true);
        }
    }

    /**
     * This method is called if the user presses the ESC key. The
     * default implementation calls <code>setVisible(false)</code>.
     */
    protected void escape() {
        setVisible(false);
    }

    private void init() {
        createActionListener();
    }

    /**
     * If a dialog has more components, it must call this method to register
     * the accelerators to all components.
     */
    protected void registerKeyStrokes() {
        for (Component component : getComponents()) {
            if (component instanceof JComponent) {
                JComponent jComponent = (JComponent) component;
                KeyStroke strokeEscape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
                KeyStroke strokeHelp = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
                jComponent.registerKeyboardAction(actionListenerEscape, strokeEscape, JComponent.WHEN_IN_FOCUSED_WINDOW);
                jComponent.registerKeyboardAction(actionListenerHelp, strokeHelp, JComponent.WHEN_IN_FOCUSED_WINDOW);
            }
        }
    }

    private void createActionListener() {
        actionListenerEscape = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                escape();
            }
        };
        actionListenerHelp = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                help();
            }
        };
    }

    @Override
    protected JRootPane createRootPane() {
        KeyStroke strokeEscape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke strokeHelp = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
        JRootPane pane = new JRootPane();
        pane.registerKeyboardAction(actionListenerEscape, strokeEscape, JComponent.WHEN_IN_FOCUSED_WINDOW);
        pane.registerKeyboardAction(actionListenerHelp, strokeHelp, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return pane;
    }
}
