package org.jphototagger.lib.dialog;

import org.jphototagger.lib.util.Settings;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * Dialog which can close by pressing the ESC key and showing the Help dialog
 * by pressing the F1 key.
 *
 * @author Elmar Baumann
 */
public class Dialog extends JDialog implements WindowListener {
    private static final long           serialVersionUID = 847375186274302816L;
    private transient ActionListener    actionListenerEscape;
    private transient ActionListener    actionListenerHelp;
    private String                      helpContentsUrl = "";
    private String                      helpPageUrl;
    private transient Settings          settings;
    private String                      settingsKey;
    private transient final HelpBrowser help = HelpBrowser.INSTANCE;

    public Dialog(Frame owner, boolean modal) {
        super(owner, modal);
        init(null, null);
    }

    public Dialog(JDialog owner, boolean modal) {
        super(owner, modal);
        init(null, null);
    }

    public Dialog(Frame owner) {
        super(owner);
        init(null, null);
    }

    public Dialog(JDialog owner) {
        super(owner);
        init(null, null);
    }

    /**
     *
     * @param owner
     * @param modal
     * @param settings     settings for size and location
     * @param settingsKey  key for size and location or null if the class name
     *                     shall be the key
     */
    public Dialog(Frame owner, boolean modal, Settings settings,
                  String settingsKey) {
        super(owner, modal);
        init(settings, settingsKey);
    }

    /**
     *
     * @param owner
     * @param modal
     * @param settings     settings for size and location
     * @param settingsKey  key for size and location or null if the class name
     *                     shall be the key
     */
    public Dialog(JDialog owner, boolean modal, Settings settings,
                  String settingsKey) {
        super(owner, modal);
        init(settings, settingsKey);
    }

    /**
     *
     * @param owner
     * @param settings     settings for size and location
     * @param settingsKey  key for size and location or null if the class name
     *                     shall be the key
     */
    public Dialog(Frame owner, Settings settings, String settingsKey) {
        super(owner);
        init(settings, settingsKey);
    }

    /**
     *
     * @param owner
     * @param settings     settings for size and location
     * @param settingsKey  key for size and location or null if the class name
     *                     shall be the key
     */
    public Dialog(JDialog owner, Settings settings, String settingsKey) {
        super(owner);
        init(settings, settingsKey);
    }

    private void init(Settings settings, String settingsKey) {
        this.settings    = settings;
        this.settingsKey = settingsKey;
        createActionListener();
        registerKeyboardActions();
        addWindowListener(this);
    }

    /**
     * Sets the contents URL of the help and must be called <em>before</em>
     * {@link #help(java.lang.String)}.
     *
     * @param url  contents URL
     * @see HelpBrowser#setContentsUrl(java.lang.String)
     */
    protected void setHelpContentsUrl(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        helpContentsUrl = url;
    }

    /**
     * Sets the url to display in the help browser.
     *
     * @param url  URL
     */
    public void setHelpPageUrl(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        helpPageUrl = url;
    }

    /**
     * This method will be called if the user presses F1. If
     * {@link #setHelpPageUrl(java.lang.String)} was called,
     * {@link #help(java.lang.String)} will be called.
     *
     * Specialized classes can call {@link #help(java.lang.String)}
     * with an appropriate URL.
     */
    protected void help() {
        if (helpPageUrl != null) {
            help(helpPageUrl);
        }
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
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        if ((help.getContentsUrl() == null)
                ||!help.getContentsUrl().equals(helpContentsUrl)) {
            help.setContentsUrl(helpContentsUrl);
        }

        if (help.isVisible()) {
            help.showUrl(url);
            help.toFront();
        } else {
            help.setDisplayUrl(url);
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

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            applySizeAndLocation();
        } else {
            setSizeAndLocation();
        }

        super.setVisible(visible);
    }

    /**
     * Sets the settings for storing and retrieving the size and location on
     * {@link #setVisible(boolean)} and {@link #escape()}.
     *
     * @param settings
     * @param settingsKey key if not the class name of the component shall be
     *                    the key for size and location, else null
     */
    public void setSettings(Settings settings, String settingsKey) {
        if (settings == null) {
            throw new NullPointerException("settings == null");
        }

        this.settings    = settings;
        this.settingsKey = settingsKey;
    }

    private void setSizeAndLocation() {
        if (settings == null) {
            return;
        }

        String key = getSizeAndLocationKey();

        settings.setSize(this, key);
        settings.setLocation(this, key);
    }

    protected void applySizeAndLocation() {
        if (settings == null) {
            return;
        }

        String key = getSizeAndLocationKey();

        settings.applySize(this, key);
        settings.applyLocation(this, key);
    }

    private String getSizeAndLocationKey() {
        return (settingsKey == null)
               ? getClass().getName()
               : settingsKey;
    }

    protected Settings getSettings() {
        return settings;
    }

    private void registerKeyboardActions() {
        KeyStroke strokeEscape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke strokeHelp   = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);

        for (Component component : getComponents()) {
            if (component instanceof JComponent) {
                JComponent comp = (JComponent) component;

                comp.registerKeyboardAction(actionListenerEscape, strokeEscape,
                                            JComponent.WHEN_IN_FOCUSED_WINDOW);
                comp.registerKeyboardAction(actionListenerHelp, strokeHelp,
                                            JComponent.WHEN_IN_FOCUSED_WINDOW);
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
        KeyStroke strokeHelp   = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
        JRootPane pane         = new JRootPane();

        pane.registerKeyboardAction(actionListenerEscape, strokeEscape,
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        pane.registerKeyboardAction(actionListenerHelp, strokeHelp,
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);

        return pane;
    }

    @Override
    public void windowClosing(WindowEvent evt) {
        setSizeAndLocation();
    }

    @Override
    public void windowOpened(WindowEvent evt) {

        // ignore
    }

    @Override
    public void windowClosed(WindowEvent evt) {

        // ignore
    }

    @Override
    public void windowIconified(WindowEvent evt) {

        // ignore
    }

    @Override
    public void windowDeiconified(WindowEvent evt) {

        // ignore
    }

    @Override
    public void windowActivated(WindowEvent evt) {

        // ignore
    }

    @Override
    public void windowDeactivated(WindowEvent evt) {

        // ignore
    }
}
