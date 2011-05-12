package org.jphototagger.program.view.dialogs;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.event.listener.DatabaseProgramsListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.dialog.Dialog;

import org.jphototagger.program.view.panels.ActionsPanel;

/**
 * Non modal dialog for actions: {@link org.jphototagger.program.data.Program}
 * where {@link org.jphototagger.program.data.Program#isAction()} is true.
 *
 * @author Elmar Baumann
 */
public final class ActionsDialog extends Dialog implements DatabaseProgramsListener {
    public static final ActionsDialog INSTANCE = new ActionsDialog();
    private static final long serialVersionUID = -2671488119703014515L;

    private ActionsDialog() {
        super(GUI.getAppFrame(), false, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPages();
        DatabasePrograms.INSTANCE.addListener(this);
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(JptBundle.INSTANCE.getString("Help.Url.ActionsDialog"));
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

    @Override
    public void programDeleted(Program program) {

        // ignore
    }

    @Override
    public void programInserted(Program program) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                toFrontIfVisible();
            }
        });
    }

    @Override
    public void programUpdated(Program program) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                toFrontIfVisible();
            }
        });
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelActions = new org.jphototagger.program.view.panels.ActionsPanel();

        setTitle(JptBundle.INSTANCE.getString("ActionsDialog.title")); // NOI18N
        setAlwaysOnTop(true);
        setName("Form"); // NOI18N

        panelActions.setName("panelActions"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelActions, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelActions, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ActionsDialog dialog = new ActionsDialog();

                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jphototagger.program.view.panels.ActionsPanel panelActions;
    // End of variables declaration//GEN-END:variables
}
