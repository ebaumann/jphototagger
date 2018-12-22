package org.jphototagger.program.module.programs;

import javax.swing.JDialog;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class EditDefaultProgramsDialog extends DialogExt {

    private static final long serialVersionUID = 1L;

    public EditDefaultProgramsDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        init();
    }

    public EditDefaultProgramsDialog(JDialog owner) {
        super(owner, true);
        init();
    }

    private void init() {
        initComponents();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelEditDefaultPrograms = new org.jphototagger.program.module.programs.EditDefaultProgramsPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "EditDefaultProgramsDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelEditDefaultPrograms, gridBagConstraints);

        pack();
    }

    private org.jphototagger.program.module.programs.EditDefaultProgramsPanel panelEditDefaultPrograms;
}
