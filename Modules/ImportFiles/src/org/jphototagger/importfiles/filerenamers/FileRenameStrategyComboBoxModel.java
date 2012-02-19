package org.jphototagger.importfiles.filerenamers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.openide.util.Lookup;

import org.jphototagger.api.file.FileRenameStrategy;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;

/**
 * @author Elmar Baumann
 */
public final class FileRenameStrategyComboBoxModel extends DefaultComboBoxModel {
    private static final long serialVersionUID = 1L;

    public FileRenameStrategyComboBoxModel() {
        addElements();
    }


    private void addElements() {
        List<FileRenameStrategy> strategies = new LinkedList<FileRenameStrategy>(
                Lookup.getDefault().lookupAll(FileRenameStrategy.class));
        Collections.sort(strategies, PositionProviderAscendingComparator.INSTANCE);
        for (FileRenameStrategy strategy : strategies) {
            addElement(strategy);
        }
    }
}
