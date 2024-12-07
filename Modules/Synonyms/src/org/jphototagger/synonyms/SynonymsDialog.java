package org.jphototagger.synonyms;

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
public class SynonymsDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    public static final SynonymsDialog INSTANCE = new SynonymsDialog();

    private SynonymsDialog() {
        super(ComponentUtil.findFrameWithIcon(), false);
        setPreferencesKey("SynonymsDialog");
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        AnnotationProcessor.process(this);
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(SynonymsDialog.class, "SynonymsDialog.HelpPage"));
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        synonymsPanel1 = new org.jphototagger.synonyms.SynonymsPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "SynonymsDialog.title")); // NOI18N
        
        getContentPane().setLayout(new java.awt.GridBagLayout());

        synonymsPanel1.setName("synonymsPanel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(synonymsPanel1, gridBagConstraints);

        pack();
    }

    private org.jphototagger.synonyms.SynonymsPanel synonymsPanel1;
}
