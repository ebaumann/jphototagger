package org.jphototagger.importfiles.subdircreators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.jphototagger.api.file.SubdirectoryCreateStrategy;
import org.jphototagger.api.file.UserDefinedSubdirectoryCreateStrategies;
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
        List<SubdirectoryCreateStrategy> strategies = new LinkedList<SubdirectoryCreateStrategy>(
                Lookup.getDefault().lookupAll(SubdirectoryCreateStrategy.class));

        addUserDefinedStrategiesToList(strategies);
        addStrategiesToModel(strategies);
    }

    private void addUserDefinedStrategiesToList(List<SubdirectoryCreateStrategy> target) {
        for (UserDefinedSubdirectoryCreateStrategies s : Lookup.getDefault().lookupAll(UserDefinedSubdirectoryCreateStrategies.class)) {
            target.addAll(s.getStrageties());
        }
    }

    private void addStrategiesToModel(List<SubdirectoryCreateStrategy> strategiesInModifiableList) {
        Collections.sort(strategiesInModifiableList, PositionProviderAscendingComparator.INSTANCE);
        for (SubdirectoryCreateStrategy strategy : strategiesInModifiableList) {
            addElement(strategy);
        }
    }

    /**
     * Replaces all user defined strategies with the current available. Intented
     * usage after editing these templates.
     */
    public void replaceUserDefinedStrategies() {
        removeAllUserDefinedStrategies();

        List<SubdirectoryCreateStrategy> strategies = new ArrayList<>();
        addUserDefinedStrategiesToList(strategies);
        addStrategiesToModel(strategies);
    }

    private void removeAllUserDefinedStrategies() {
        for (SubdirectoryCreateStrategy strategy : getUserDefinedStrategies()) {
            removeElement(strategy);
        }
    }

    private Collection<SubdirectoryCreateStrategy> getUserDefinedStrategies() {
        Collection<SubdirectoryCreateStrategy> result = new ArrayList<>();
        int size = getSize();
        for (int i = 0; i < size; i++) {
            SubdirectoryCreateStrategy strategy = (SubdirectoryCreateStrategy) getElementAt(i);
            if (strategy.isUserDefined()) {
                result.add(strategy);
            }
        }
        return result;
    }
}
