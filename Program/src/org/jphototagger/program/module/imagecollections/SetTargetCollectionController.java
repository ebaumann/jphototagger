package org.jphototagger.program.module.imagecollections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import org.jdesktop.swingx.JXList;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ListUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class SetTargetCollectionController implements ActionListener {

    public SetTargetCollectionController() {
        listen();
    }

    private void listen() {
        ImageCollectionsPopupMenu.INSTANCE.getItemTargetCollection().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setSelectedItemAsTargetCollection();
    }

    private void setSelectedItemAsTargetCollection() {
        JXList list = GUI.getImageCollectionsList();
        String collectionName = ListUtil.getItemString(GUI.getImageCollectionsList(),
                ImageCollectionsPopupMenu.INSTANCE.getItemIndex());
        if (StringUtil.hasContent(collectionName)) {
            String errorMessage = Bundle.getString(SetTargetCollectionController.class, "SetTargetCollectionController.Error.IsSpecialCollection");

            if (!ImageCollectionsUtil.checkIsNotSpecialCollection(collectionName, errorMessage)) {
                return;
            }

            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            String oldTargetCollName = prefs.getString(TargetCollectionController.KEY_TARGET_COLLECTION_NAME);

            if (!Objects.equals(oldTargetCollName, collectionName)) {
                prefs.setString(TargetCollectionController.KEY_TARGET_COLLECTION_NAME, collectionName);
                MessageDisplayer.information(list, Bundle.getString(SetTargetCollectionController.class, "SetTargetCollectionController.Info.SpecialCollectionSet", collectionName));
            }
        }
    }
}
