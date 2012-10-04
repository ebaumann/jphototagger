package org.jphototagger.lib.nodes;

import java.util.Collection;
import java.util.Collections;
import javax.swing.Action;
import javax.swing.Icon;
import org.jphototagger.api.component.HtmlDisplayNameProvider;
import org.jphototagger.api.nodes.Node;
import org.jphototagger.lib.util.StringEscapeUtil;

/**
 * @author Elmar Baumann
 */
public abstract class AbstractNode implements Node, HtmlDisplayNameProvider {

    /**
     *
     * @return {@code #getDisplayName()} whithin HTML tags, HTML charactes will be escaped
     */
    @Override
    public String getHtmlDisplayName() {
        String displayName = getDisplayName();
        String escapedDisplayName = StringEscapeUtil.escapeHTML(displayName);

        return "<html>" + escapedDisplayName + "</html>";
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

    /**
     *
     * @return empty collection
     */
    @Override
    public Collection<? extends Action> getActions() {
        return Collections.<Action>emptyList();
    }

    /**
     *
     * @return empty collection
     */
    @Override
    public Collection<? extends Action> getActionsForTemporarySelections() {
        return Collections.<Action>emptyList();
    }

    /**
     *
     * @return null
     */
    @Override
    public Action getPreferredAction() {
        return null;
    }
}
