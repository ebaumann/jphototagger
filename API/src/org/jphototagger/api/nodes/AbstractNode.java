package org.jphototagger.api.nodes;

import javax.swing.Icon;

/**
 *
 *
 * @author Elmar Baumann
 */
public abstract class AbstractNode implements Node {

    /**
     *
     * @return {@code #getDisplayName()} whithin HTML tags
     *          <em>Override this implementation, if the display name can contain special HTML characters!</em>
     */
    @Override
    public String getHtmlDisplayName() {
        return "<html>" + getDisplayName() + "</html>";
    }

    /**
     *
     * @return null
     */
    @Override
    public Icon getSmallIcon() {
        return null;
    }

    /**
     *
     * @return null
     */
    @Override
    public Icon getLargeIcon() {
        return null;
    }
}
