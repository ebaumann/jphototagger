package org.jphototagger.lib.swing;

import javax.swing.SwingUtilities;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * Contains a {@code org.jphototagger.lib.component.SystemOutputPanel}.
 *
 * This dialog is a singleton. It starts to capture output after calling
 * {@code #captureOutput()}.
 *
 * @author Elmar Baumann
 */
public class SystemOutputDialog extends DialogExt {

    public static final SystemOutputDialog INSTANCE = new SystemOutputDialog(ComponentUtil.findFrameWithIcon());
    private static final long serialVersionUID = 1L;

    public SystemOutputDialog(java.awt.Frame parent) {
        super(parent, false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        AnnotationProcessor.process(this);
    }

    /**
     * Starts to capture the output. Calls {@code SystemOutputPanel#caputure()}.
     */
    public void captureOutput() {
        panelSystemOutput.caputure();
    }

    public String getOutput() {
        return panelSystemOutput.getOutput();
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelSystemOutput = new org.jphototagger.lib.swing.SystemOutputPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "SystemOutputDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelSystemOutput.setName("panelSystemOutput"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelSystemOutput, gridBagConstraints);

        pack();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        setVisible(false);
    }

    private org.jphototagger.lib.swing.SystemOutputPanel panelSystemOutput;
}
