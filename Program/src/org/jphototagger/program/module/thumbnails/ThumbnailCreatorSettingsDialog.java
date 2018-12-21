package org.jphototagger.program.module.thumbnails;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jphototagger.domain.thumbnails.ThumbnailCreator;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class ThumbnailCreatorSettingsDialog extends DialogExt {

    private static final long serialVersionUID = 1L;

    public ThumbnailCreatorSettingsDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        List<ThumbnailCreator> creators = new ArrayList<ThumbnailCreator>(Lookup.getDefault().lookupAll(ThumbnailCreator.class));
        Collections.sort(creators, PositionProviderAscendingComparator.INSTANCE);
        for (ThumbnailCreator creator : creators) {
            Component settingsComponent = creator.getSettingsComponent();
            if (settingsComponent != null) {
                tabbedPane.add(creator.getDisplayName(), settingsComponent);
            }
        }
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = UiFactory.tabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "ThumbnailCreatorSettingsDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        tabbedPane.setMinimumSize(new Dimension(300, 200));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(tabbedPane, gridBagConstraints);

        pack();
    }

    private javax.swing.JTabbedPane tabbedPane;
}
