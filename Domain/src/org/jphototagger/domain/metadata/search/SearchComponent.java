package org.jphototagger.domain.metadata.search;

import java.awt.Component;
import javax.swing.Action;

/**
 * @author Elmar Baumann
 */
public interface SearchComponent {

    Component getSearchComponent();

    Action getSelectSearchComponentAction();
}
