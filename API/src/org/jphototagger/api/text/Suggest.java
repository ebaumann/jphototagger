package org.jphototagger.api.text;

import java.util.Collection;

/**
 * @author Elmar Baumann
 */
public interface Suggest {

    Collection<String> suggest(String input);

    String getDescription();

    String getRequiresDescription();

    boolean isAccepted();
}
