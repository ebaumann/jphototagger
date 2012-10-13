package org.jphototagger.importfiles.subdircreators;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.jphototagger.api.file.SubdirectoryCreateStrategy;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class SubdirectoryCreateStrategyComboBoxModel extends DefaultComboBoxModel<Object> {

    private static final long serialVersionUID = 1L;

    public SubdirectoryCreateStrategyComboBoxModel() {
        addElements();
    }

    private void addElements() {
        List<SubdirectoryCreateStrategy> strategies = new LinkedList<>(
                Lookup.getDefault().lookupAll(SubdirectoryCreateStrategy.class));
        Collections.sort(strategies, PositionProviderAscendingComparator.INSTANCE);
        for (SubdirectoryCreateStrategy strategy : strategies) {
            addElement(strategy);
        }
    }
}
