package org.jphototagger.lib.swing;

import javax.swing.JFrame;

/**
 * Frame to use in JPhotoTagger instead of JFrame to gain a unique Behaviour
 * and Look and Feel.
 *
 * @author Elmar Baumann
 */
public class FrameExt extends JFrame {

    private static final long serialVersionUID = 1L;

    public FrameExt() {
        init();
    }

    private void init() {
        org.jphototagger.resources.UiFactory.configure(this);
    }
}
