package org.jphototagger.program.module.directories;

import org.jphototagger.lib.swing.AllSystemDirectoriesTreeModel;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.module.favorites.FavoritesTreeModel;

/**
 * After renaming directories, the tree model's user objects - files, which are
 * directories - of all descendants are invalid ("broken paths"). E.g. After
 * renaming "/1" to "/2", the directories "/1/2" and "/1/2/3" are no longer
 * existing within the file system. The user objects have to be upated to "/2/2"
 * and "/2/2/3".
 * <p>
 * Two models are affected: One of the direcotry view and one of the favorites
 * view.
 *
 * @author Elmar Baumann
 */
public final class RenameDirectoryFix {

    /**
     * See class documentation
     */
    public static void fixAfterRename() {
        ModelFactory.INSTANCE.getModel(AllSystemDirectoriesTreeModel.class).update();
        ModelFactory.INSTANCE.getModel(FavoritesTreeModel.class).update();
    }

    private RenameDirectoryFix() {
    }
}
