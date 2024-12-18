package org.jphototagger.program.module.actions;

import javax.swing.SwingUtilities;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.repository.event.programs.ProgramInsertedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramUpdatedEvent;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.resources.UiFactory;

/**
 * Non modal dialog for actions: {@code org.jphototagger.program.data.Program}
 * where {@code org.jphototagger.program.data.Program#isAction()} is true.
 *
 * @author Elmar Baumann
 */
public final class ActionsDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    public static final ActionsDialog INSTANCE = new ActionsDialog();

    private ActionsDialog() {
        super(GUI.getAppFrame(), false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        AnnotationProcessor.process(this);
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(ActionsDialog.class, "ActionsDialog.HelpPage"));
    }

    public ActionsPanel getPanelActions() {
        return panelActions;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            panelActions.setEnabled();
        }
        super.setVisible(visible);
    }

    private void toFrontIfVisible() {
        if (isVisible()) {
            toFront();
        }
    }

    @EventSubscriber(eventClass = ProgramInsertedEvent.class)
    public void programInserted(final ProgramInsertedEvent evt) {
        toFrontIfVisible();
    }

    @EventSubscriber(eventClass = ProgramUpdatedEvent.class)
    public void programUpdated(final ProgramUpdatedEvent evt) {
        toFrontIfVisible();
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelActions = new org.jphototagger.program.module.actions.ActionsPanel();

        setTitle(Bundle.getString(getClass(), "ActionsDialog.title")); // NOI18N
        setAlwaysOnTop(true);
        
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelActions.setName("panelActions"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelActions, gridBagConstraints);

        pack();
    }

    private org.jphototagger.program.module.actions.ActionsPanel panelActions;
}
