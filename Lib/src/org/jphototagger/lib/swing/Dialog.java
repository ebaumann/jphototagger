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
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.help.HelpUtil;
import org.openide.util.Lookup;

/**
 * Dialog which should be used instead of JDialog to achieve the same behaviour
 * and Look and Feel. Extensions:
 *
 * <ul>
 * <li>Will be closed by pressing the ESC key</li>
 * <li>Showing the Help Browser by pressing the F1 key</li>
 * <li>Persists size and Location</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public class Dialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private transient ActionListener actionListenerEscape;
    private transient ActionListener actionListenerHelp;
    private String helpPageUrl;
    private String preferencesKey;
    private boolean persistSizeAndLocation = true;

    public Dialog() {
        init();
    }

    public Dialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        init();
    }

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
        org.jphototagger.resources.UiFactory.configure(this);
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
     * <p>
     * If {@link #setHelpPageUrl(java.lang.String)} was called,
     * {@link #showHelp(java.lang.String)} will be called.
     * <p>
     * Specialized classes can call {@code #showHelp(java.lang.String)} with an
     * appropriate URL.
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
     * This method will be called if the user presses the ESC key. The default
     * implementation calls <code>setVisible(false)</code>.
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
     * This dialog persists size and location if a {@link Preferences}
     * implementation is present, by default it uses the class' name as key,
     * here a different key can be set. This makes sense if the same dialog is
     * used whithin different contexts or if it ist not derived (and hence the
     * persistence key is always this class name).
     *
     * @param preferencesKey key
     */
    public void setPreferencesKey(String preferencesKey) {
        this.preferencesKey = preferencesKey;
    }

    /**
     * Sets whether this dialog persists it's size and location. Hence this is
     * the default behaviour, this method should be called, if the size and
     * location shouldn't be persisted.
     *
     * @param persist true if the size and location should be persisted.
     *                Default: true.
     */
    public void setPersistSizeAndLocation(boolean persist) {
        persistSizeAndLocation = persist;
    }

    protected void persistSizeAndLocation() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (!persistSizeAndLocation || prefs == null) {
            return;
        }
        String key = getSizeAndLocationPreferencesKey();
        prefs.setSize(key, this);
        prefs.setLocation(key, this);
    }

    protected void restoreSizeAndLocation() {
        if (!persistSizeAndLocation) {
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

    private final WindowListener sizeAndLocationPersister = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent evt) {
            persistSizeAndLocation();
        }
    };
}
