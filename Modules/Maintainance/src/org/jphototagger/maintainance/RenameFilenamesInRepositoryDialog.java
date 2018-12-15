package org.jphototagger.maintainance;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class RenameFilenamesInRepositoryDialog extends DialogExt {

    private static final long serialVersionUID = 1L;

    public RenameFilenamesInRepositoryDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String key = RenameFilenamesInRepositoryDialog.class.getName();
        prefs.applySize(key, this);
        prefs.applyLocation(key, this);
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(RenameFilenamesInRepositoryDialog.class, "RenameFilenamesInRepositoryDialog.HelpPage"));
    }

    private void checkClosing() {
        if (!panelDbFilenameReplace.runs()) {
            setVisible(false);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            panelDbFilenameReplace.restore();
        } else {
            panelDbFilenameReplace.persist();
        }
        super.setVisible(visible);
    }

    @Override
    protected void escape() {
        checkClosing();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panelDbFilenameReplace = new org.jphototagger.maintainance.RenameFilenamesInRepositoryPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "RenameFilenamesInRepositoryDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelDbFilenameReplace.setName("panelDbFilenameReplace"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelDbFilenameReplace, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        checkClosing();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jphototagger.maintainance.RenameFilenamesInRepositoryPanel panelDbFilenameReplace;
    // End of variables declaration//GEN-END:variables
}
