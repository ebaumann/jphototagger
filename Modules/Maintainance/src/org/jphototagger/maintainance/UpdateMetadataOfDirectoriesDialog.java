package org.jphototagger.maintainance;

import javax.swing.SwingUtilities;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public final class UpdateMetadataOfDirectoriesDialog extends DialogExt {

    public static final UpdateMetadataOfDirectoriesDialog INSTANCE = new UpdateMetadataOfDirectoriesDialog();
    private static final long serialVersionUID = 1L;

    private UpdateMetadataOfDirectoriesDialog() {
        super(ComponentUtil.findFrameWithIcon(), false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        AnnotationProcessor.process(this);
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(UpdateMetadataOfDirectoriesDialog.class, "UpdateMetadataOfDirectoriesDialog.HelpPage"));
    }

    private void endDialog() {
        panel.willDispose();
        setVisible(false);
    }

    @Override
    protected void escape() {
        endDialog();
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panel = new org.jphototagger.maintainance.UpdateMetadataOfDirectoriesPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "UpdateMetadataOfDirectoriesDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel.setName("panel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panel, gridBagConstraints);

        pack();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        endDialog();
    }

    private org.jphototagger.maintainance.UpdateMetadataOfDirectoriesPanel panel;
}
