package org.jphototagger.lib.swing;

import javax.swing.JPanel;
import org.jphototagger.resources.UiFactory;

/**
 * Panel to use in JPhotoTagger instead of {@link JPanel} to achieve a unique
 * Behaviour and Look and Feel.
 *
 * @author Elmar Baumann
 */
public class PanelExt extends JPanel {

    private static final long serialVersionUID = 1L;

    public PanelExt() {
        init();
    }

    private void init() {
        UiFactory.configure(this);
    }
}
