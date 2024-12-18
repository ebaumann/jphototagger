package org.jphototagger.program.module.metadatatemplates;

import java.awt.Container;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.JXList;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class MetaDataTemplatesPanel extends PanelExt implements ListSelectionListener {

    private static final long serialVersionUID = 1L;

    public MetaDataTemplatesPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        list.addListSelectionListener(this);
        MnemonicUtil.setMnemonics((Container) this);
        AnnotationProcessor.process(this);
    }

    public JXList getList() {
        return list;
    }

    public JButton getButtonAdd() {
        return buttonAdd;
    }

    public JButton getButtonDelete() {
        return buttonDelete;
    }

    public JButton getButtonEdit() {
        return buttonEdit;
    }

    public JButton getButtonRename() {
        return buttonRename;
    }

    public JButton getButtonAddToSelImages() {
        return buttonAddToSelImages;
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            boolean selected = list.getSelectedIndex() >= 0;

            buttonDelete.setEnabled(selected);
            buttonEdit.setEnabled(selected);
            buttonRename.setEnabled(selected);
            final ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
            buttonAddToSelImages.setEnabled(selected && (tnPanel.isAFileSelected()));
        }
    }

    @EventSubscriber(eventClass=ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        buttonAddToSelImages.setEnabled((list.getSelectedIndex() >= 0) && tnPanel.isAFileSelected());
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPane = UiFactory.scrollPane();
        list = UiFactory.jxList();
        panelButtons = UiFactory.panel();
        panelModifyButtons = UiFactory.panel();
        buttonRename = UiFactory.button();
        buttonAdd = UiFactory.button();
        buttonEdit = UiFactory.button();
        buttonDelete = UiFactory.button();
        buttonAddToSelImages = UiFactory.button();

        
        setLayout(new java.awt.GridBagLayout());

        scrollPane.setName("scrollPane"); // NOI18N

        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new org.jphototagger.program.module.metadatatemplates.MetadataTemplatesListCellRenderer());
        list.setDragEnabled(true);
        list.setName("list"); // NOI18N
        scrollPane.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        panelModifyButtons.setName("panelModifyButtons"); // NOI18N
        panelModifyButtons.setLayout(new java.awt.GridLayout(2, 0));

        buttonRename.setText(Bundle.getString(getClass(), "MetaDataTemplatesPanel.buttonRename.text")); // NOI18N
        buttonRename.setEnabled(false);
        buttonRename.setName("buttonRename"); // NOI18N
        panelModifyButtons.add(buttonRename);

        buttonAdd.setText(Bundle.getString(getClass(), "MetaDataTemplatesPanel.buttonAdd.text")); // NOI18N
        buttonAdd.setName("buttonAdd"); // NOI18N
        panelModifyButtons.add(buttonAdd);

        buttonEdit.setText(Bundle.getString(getClass(), "MetaDataTemplatesPanel.buttonEdit.text")); // NOI18N
        buttonEdit.setEnabled(false);
        buttonEdit.setName("buttonEdit"); // NOI18N
        panelModifyButtons.add(buttonEdit);

        buttonDelete.setText(Bundle.getString(getClass(), "MetaDataTemplatesPanel.buttonDelete.text")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.setName("buttonDelete"); // NOI18N
        panelModifyButtons.add(buttonDelete);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelButtons.add(panelModifyButtons, gridBagConstraints);

        buttonAddToSelImages.setText(Bundle.getString(getClass(), "MetaDataTemplatesPanel.buttonAddToSelImages.text")); // NOI18N
        buttonAddToSelImages.setEnabled(false);
        buttonAddToSelImages.setName("buttonAddToSelImages"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        panelButtons.add(buttonAddToSelImages, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        add(panelButtons, gridBagConstraints);
    }

    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonAddToSelImages;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonEdit;
    private javax.swing.JButton buttonRename;
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelModifyButtons;
    private javax.swing.JScrollPane scrollPane;
}
