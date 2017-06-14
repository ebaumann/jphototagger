package org.jphototagger.importfiles.filerenamers;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.jphototagger.api.file.FileRenameStrategy;
import org.jphototagger.api.file.FileRenameStrategyProvider;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class FileRenameStrategyComboBoxModel extends DefaultComboBoxModel<Object> {

    private static final long serialVersionUID = 1L;

    public FileRenameStrategyComboBoxModel() {
        addElements();
    }

    private void addElements() {
        Collection<? extends FileRenameStrategy> lookup = Lookup.getDefault().lookupAll(FileRenameStrategy.class);
        List<FileRenameStrategy> strategies = new LinkedList<FileRenameStrategy>(lookup);
        addProvidedStrategies(strategies);
        Collections.sort(strategies, PositionProviderAscendingComparator.INSTANCE);
        for (FileRenameStrategy strategy : strategies) {
            addElement(strategy);
        }
    }

    private void addProvidedStrategies(Collection<FileRenameStrategy> strategies) {
        for (FileRenameStrategyProvider provider : Lookup.getDefault().lookupAll(FileRenameStrategyProvider.class)) {
            strategies.addAll(provider.getFileRenameStrategies());
        }
    }
}
