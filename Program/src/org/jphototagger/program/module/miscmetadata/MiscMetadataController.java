package org.jphototagger.program.module.miscmetadata;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.jphototagger.domain.metadata.MetaDataStringValue;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.swing.PopupMenuTree;
import org.jphototagger.program.module.Controller;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public abstract class MiscMetadataController extends Controller implements PopupMenuTree.Listener {

    protected MiscMetadataController() {
        listenToKeyEventsOf(GUI.getMiscMetadataTree());
    }

    protected abstract void action(MetaDataValue mdValue, String value);

    private void action(List<MetaDataStringValue> mdStringValues) {
        for (MetaDataStringValue mdStringValue : mdStringValues) {
            MetaDataValue mdValue = mdStringValue.getMetaDataValue();
            String value = mdStringValue.getValue();

            action(mdValue, value);
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

        action(MiscMetadataUtil.getColValuesFrom(treePaths));
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
            action(MiscMetadataUtil.getColValuesFrom(Arrays.asList(selPaths)));
        }
    }
}
