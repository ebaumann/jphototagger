/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.dialog;

import java.awt.Component;
import java.awt.Frame;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-19
 */
public class Dialog extends JDialog {

    private static final long           serialVersionUID    = 847375186274302816L;
    private              ActionListener actionListenerEscape;
    private              ActionListener actionListenerHelp;
    private              String         helpContentsUrl     = "";
    private              String         helpPageUrl;
    private final        HelpBrowser    help                = HelpBrowser.INSTANCE;

    public Dialog(Frame owner, boolean modal) {
        super(owner, modal);
        init();
        registerKeyboardActions();
    }

    public Dialog(Frame owner) {
        super(owner);
        init();
        registerKeyboardActions();
    }

    /**
     * Sets the contents URL of the help and must be called <em>before</em>
     * {@link #help(java.lang.String)}.
     *
     * @param url  contents URL
     * @see HelpBrowser#setContentsUrl(java.lang.String)
     */
    protected void setHelpContentsUrl(String url) {
        if (url == null) throw new NullPointerException("url == null");
        helpContentsUrl = url;
    }

    /**
     * Sets the url to display in the help browser.
     *
     * @param url  URL
     */
    public void setHelpPageUrl(String url) {
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
        if (url == null) throw new NullPointerException("url == null");
        if (help.getContentsUrl() == null ||
           !help.getContentsUrl().equals(helpContentsUrl)) {
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

    private void init() {
        createActionListener();
    }

    private void registerKeyboardActions() {
        KeyStroke strokeEscape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke strokeHelp   = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
        for (Component component : getComponents()) {
            if (component instanceof JComponent) {
                JComponent comp = (JComponent) component;
                comp.registerKeyboardAction(actionListenerEscape, strokeEscape, JComponent.WHEN_IN_FOCUSED_WINDOW);
                comp.registerKeyboardAction(actionListenerHelp  , strokeHelp  , JComponent.WHEN_IN_FOCUSED_WINDOW);
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

        pane.registerKeyboardAction(actionListenerEscape, strokeEscape, JComponent.WHEN_IN_FOCUSED_WINDOW);
        pane.registerKeyboardAction(actionListenerHelp  , strokeHelp  , JComponent.WHEN_IN_FOCUSED_WINDOW);
        return pane;
    }
}
