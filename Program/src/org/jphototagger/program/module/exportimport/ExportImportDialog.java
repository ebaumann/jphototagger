package org.jphototagger.program.module.exportimport;


import javax.swing.SwingUtilities;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.exportimport.ExportImportPanel.ExportImportListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class ExportImportDialog extends DialogExt implements ExportImportListener {

    private static final long serialVersionUID = 1L;
    private final ExportImportContext context;

    public ExportImportDialog(ExportImportContext context) {
        super(GUI.getAppFrame());
        setPreferencesKey("ExportImportDialog");
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        this.context = context;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setTitle();
        setHelpPage();
        panelExportImport.setContext(context);
        panelExportImport.addListener(this);
        AnnotationProcessor.process(this);
    }

    private void setTitle() {
        setTitle(context.equals(ExportImportContext.EXPORT)
                 ? Bundle.getString(ExportImportDialog.class, "ExportImportDialog.Title.Export")
                 : Bundle.getString(ExportImportDialog.class, "ExportImportDialog.Title.Import"));
    }

    private void setHelpPage() {
        setHelpPageUrl(context.equals(ExportImportContext.EXPORT)
                ? Bundle.getString(ExportImportDialog.class, "ExportImportDialog.HelpPage.Export")
                : Bundle.getString(ExportImportDialog.class, "ExportImportDialog.HelpPage.Import"));
    }

    @Override
    public void done() {
        setVisible(false);
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelExportImport = new org.jphototagger.program.module.exportimport.ExportImportPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "ExportImportDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelExportImport.setName("panelExportImport"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelExportImport, gridBagConstraints);

        pack();
    }


    private org.jphototagger.program.module.exportimport.ExportImportPanel panelExportImport;
}
