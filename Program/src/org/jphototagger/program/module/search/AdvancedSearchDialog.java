package org.jphototagger.program.module.search;

import javax.swing.SwingUtilities;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.resources.UiFactory;

/**
 * Nicht modaler DialogExt für eine erweiterte Suche.
 *
 * @author Elmar Baumann
 */
public final class AdvancedSearchDialog extends DialogExt implements NameListener {

    public static final AdvancedSearchDialog INSTANCE = new AdvancedSearchDialog(false);
    private static final long serialVersionUID = 1L;

    private AdvancedSearchDialog(boolean modal) {
        super(GUI.getAppFrame(), modal);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        panel.addNameListener(this);
        AnnotationProcessor.process(this);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            panel.restore();
            setSearchName(panel.getSearchName());
        }
        super.setVisible(visible);
    }

    private void beforeWindowClosing() {
        panel.persist();
        panel.willDispose();
        setVisible(false);
    }

    public AdvancedSearchPanel getPanel() {
        return panel;
    }

    @Override
    protected void showHelp() {
        showHelp(Bundle.getString(AdvancedSearchDialog.class, "AdvancedSearchDialog.HelpPage"));
    }

    @Override
    protected void escape() {
        beforeWindowClosing();
    }

    public AdvancedSearchPanel getAdvancedSearchPanel() {
        return panel;
    }

    @Override
    public void nameChanged(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        setSearchName(name);
    }

    private void setSearchName(String name) {
        String delimiter =  ": ";
        setTitle(Bundle.getString(AdvancedSearchDialog.class, "AdvancedSearchDialog.TitlePrefix") + delimiter + name);
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panel = new org.jphototagger.program.module.search.AdvancedSearchPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "AdvancedSearchDialog.title")); // NOI18N
        
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
        setSize(UiFactory.dimension(650, 250));

        pack();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        beforeWindowClosing();
    }

    private org.jphototagger.program.module.search.AdvancedSearchPanel panel;
}
