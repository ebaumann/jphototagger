package org.jphototagger.xmpmodule;

import javax.swing.SwingUtilities;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public class FileEditorDialog extends DialogExt {
    private static final long serialVersionUID = 1L;

    public FileEditorDialog() {
        super(ComponentUtil.findFrameWithIcon(), false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        AnnotationProcessor.process(this);
    }

    public FileEditorPanel getFileEditorPanel() {
        return panelFileEditor;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            setTitle(panelFileEditor.getTitle());
            panelFileEditor.readProperties();
        } else {
            panelFileEditor.writeProperties();
            hideIfNotRunning();
        }

        super.setVisible(visible);
    }

    private void hideIfNotRunning() {
        if (panelFileEditor.isRunning()) {
            String message = Bundle.getString(FileEditorDialog.class, "FileEditorDialog.Error.Running");
            MessageDisplayer.error(this, message);
        } else {
            super.setVisible(false);
        }
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelFileEditor = new org.jphototagger.xmpmodule.FileEditorPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/xmpmodule/Bundle"); // NOI18N
        setTitle(Bundle.getString(getClass(), "FileEditorDialog.title")); // NOI18N
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelFileEditor.setName("panelFileEditor"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(panelFileEditor, gridBagConstraints);

        pack();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        setVisible(false);
    }

    private org.jphototagger.xmpmodule.FileEditorPanel panelFileEditor;
}
