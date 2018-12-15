package org.jphototagger.program.module.editmetadata;

import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
class EditMetaDataPanelsWrapperPanel extends PanelExt {

    private static final long serialVersionUID = 1L;
    private final transient EditMetaDataPanels editMetadtaPanels;

    EditMetaDataPanelsWrapperPanel() {
        initComponents();
        editMetadtaPanels = new EditMetaDataPanels(panelEditMetadata);
    }

    EditMetaDataPanels getEditMetadtaPanels() {
        return editMetadtaPanels;
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPaneEditMetadata = UiFactory.scrollPane();
        panelEditMetadata = UiFactory.panel();

        setName("Edit Metadata Container"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        panelEditMetadata.setLayout(new java.awt.GridBagLayout());
        scrollPaneEditMetadata.setViewportView(panelEditMetadata);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPaneEditMetadata, gridBagConstraints);
    }

    private javax.swing.JPanel panelEditMetadata;
    private javax.swing.JScrollPane scrollPaneEditMetadata;
}
