package org.jphototagger.program.controller.miscmetadata;

import org.jphototagger.lib.event.listener.PopupMenuTree;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.helper.MiscMetadataHelper;
import org.jphototagger.program.resource.GUI;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author Elmar Baumann
 */
public abstract class ControllerMiscMetadata extends Controller implements PopupMenuTree.Listener {
    protected ControllerMiscMetadata() {
        listenToKeyEventsOf(GUI.getMiscMetadataTree());
    }

    protected abstract void action(Column column, String value);

    private void action(List<Pair<Column, String>> pairs) {
        for (Pair<Column, String> pair : pairs) {
            action(pair.getFirst(), pair.getSecond());
        }
    }

    @Override
    protected void action(ActionEvent evt) {
        throw new IllegalStateException("Shall never be called!");
    }

    @Override
    public void action(JTree tree, List<TreePath> treePaths) {
        if (treePaths == null) {
            throw new NullPointerException("treePaths == null");
        }

        action(MiscMetadataHelper.getColValuesFrom(treePaths));
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return false;
    }

    @Override
    protected void action(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        TreePath[] selPaths = GUI.getMiscMetadataTree().getSelectionPaths();

        if (selPaths != null) {
            action(MiscMetadataHelper.getColValuesFrom(Arrays.asList(selPaths)));
        }
    }
}
