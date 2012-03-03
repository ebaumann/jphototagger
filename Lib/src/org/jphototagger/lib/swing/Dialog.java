package org.jphototagger.lib.swing;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.help.HelpUtil;

/**
 * Dialog which will be closed by pressing the ESC key and showing the Help Browser by pressing the F1 key.
 *
 * @author Elmar Baumann
 */
public class Dialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private transient ActionListener actionListenerEscape;
    private transient ActionListener actionListenerHelp;
    private String helpPageUrl;
    private String preferencesKey;
    private boolean ignorePersistedSizeAndLocation;

    public Dialog(Frame owner, boolean modal) {
        super(owner, modal);
        init();
    }

    public Dialog(JDialog owner, boolean modal) {
        super(owner, modal);
        init();
    }

    public Dialog(Frame owner) {
        super(owner);
        init();
    }

    public Dialog(JDialog owner) {
        super(owner);
        init();
    }

    private void init() {
        createActionListener();
        registerKeyboardActions();
        addWindowListener(sizeAndLocationPersister);
    }

    /**
     * Sets the URL to display in the Help Browser.
     *
     * @param url URL
     */
    public void setHelpPageUrl(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }
        helpPageUrl = url;
    }

    /**
     * This method will be called if the user presses F1.
     *
     * <p>If {@link #setHelpPageUrl(java.lang.String)} was called,
     * {@link #showHelp(java.lang.String)} will be called.
     *
     * <p> Specialized classes can call {@code #showHelp(java.lang.String)} with an appropriate URL.
     */
    protected void showHelp() {
        if (helpPageUrl != null) {
            showHelp(helpPageUrl);
        }
    }

    protected void showHelp(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }
        HelpUtil.showHelp(url);
    }

    /**
     * This method will be called if the user presses the ESC key. The
     * default implementation calls <code>setVisible(false)</code>.
     */
    protected void escape() {
        setVisible(false);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            restoreSizeAndLocation();
        } else {
            persistSizeAndLocation();
        }
        super.setVisible(visible);
    }

    /**
     * This dialog persists size and location if a {@code Preferences} implementation
     * is present, by default it uses the class' name as key, here a different
     * key can be set. This makes sense if the same dialog is used whithin different
     * contexts.
     *
     * @param preferencesKey key
     */
    public void setPreferencesKey(String preferencesKey) {
        this.preferencesKey = preferencesKey;
    }

    public void setIgnorePersistedSizeAndLocation(boolean ignore) {
        ignorePersistedSizeAndLocation = ignore;
    }

    protected void persistSizeAndLocation() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (ignorePersistedSizeAndLocation || prefs == null) {
            return;
        }
        String key = getSizeAndLocationPreferencesKey();
        prefs.setSize(key, this);
        prefs.setLocation(key, this);
    }

    protected void restoreSizeAndLocation() {
        if (ignorePersistedSizeAndLocation) {
            return;
        }
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs == null) {
            setLocationRelativeTo(null);
            return;
        }
        String key = getSizeAndLocationPreferencesKey();
        prefs.applySize(key, this);
        prefs.applyLocation(key, this);
    }

    private String getSizeAndLocationPreferencesKey() {
        return (preferencesKey == null)
                ? getClass().getName()
                : preferencesKey;
    }

    private void registerKeyboardActions() {
        KeyStroke strokeEscape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke strokeHelp = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
        for (Component component : getComponents()) {
            if (component instanceof JComponent) {
                JComponent jComponent = (JComponent) component;
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
                showHelp();
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

    private WindowListener sizeAndLocationPersister = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent evt) {
            persistSizeAndLocation();
        }
    };
}
