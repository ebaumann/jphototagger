package org.jphototagger.api.windows;

import javax.swing.Icon;

/**
 *
 *
 * @author Elmar Baumann
 */
public abstract class AbstractAppWindow implements AppWindow {

    /**
     *
     * @return null
     */
    @Override
    public String getTitle() {
        return null;
    }

    /**
     *
     * @return null
     */
    @Override
    public String getTip() {
        return null;
    }

    /**
     *
     * @return null
     */
    @Override
    public Icon getIcon() {
        return null;
    }

    /**
     *
     * @return -1
     */
    @Override
    public int getPosition() {
        return -1;
    }

}
