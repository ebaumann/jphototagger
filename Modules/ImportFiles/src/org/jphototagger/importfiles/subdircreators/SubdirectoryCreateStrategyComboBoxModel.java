package org.jphototagger.importfiles.subdircreators;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.openide.util.Lookup;

import org.jphototagger.api.file.SubdirectoryCreateStrategy;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;

/**
 * @author Elmar Baumann
 */
public final class SubdirectoryCreateStrategyComboBoxModel extends DefaultComboBoxModel {

    private static final long serialVersionUID = 1L;

    public SubdirectoryCreateStrategyComboBoxModel() {
        addElements();
    }

    private void addElements() {
        List<SubdirectoryCreateStrategy> strategies = new LinkedList<SubdirectoryCreateStrategy>(
                Lookup.getDefault().lookupAll(SubdirectoryCreateStrategy.class));
        Collections.sort(strategies, PositionProviderAscendingComparator.INSTANCE);
        for (SubdirectoryCreateStrategy strategy : strategies) {
            addElement(strategy);
        }
    }
}
