package org.jphototagger.program.module.filesystem;

import java.awt.Frame;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class RenameTemplatesDialog extends DialogExt {

    private static final long serialVersionUID = 1L;

    public RenameTemplatesDialog() {
        this(ComponentUtil.findFrameWithIcon());
    }

    public RenameTemplatesDialog(Frame parentFrame) {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPageUrl(Bundle.getString(RenameTemplatesDialog.class, "RenameTemplatesDialog.HelpPage"));
    }

    private void dialogCloses() {
        panelRenameTemplates.checkDirty();
    }

    @Override
    protected void escape() {
        dialogCloses();
        super.escape();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelRenameTemplates = new org.jphototagger.program.module.filesystem.RenameTemplatesPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "RenameTemplatesDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
        getContentPane().add(panelRenameTemplates, gridBagConstraints);

        pack();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        dialogCloses();
    }

    private org.jphototagger.program.module.filesystem.RenameTemplatesPanel panelRenameTemplates;
}
