package org.jphototagger.program.view.panels;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.jphototagger.api.core.UserFilesProvider;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.model.TableModelDatabaseInfo;
import org.jphototagger.api.file.Filename;
import org.jphototagger.program.view.renderer.TableCellRendererDatabaseInfoColumns;
import org.openide.util.Lookup;

/**
 * Dislplays the database record count total and of specific columns.
 *
 * @author Elmar Baumann
 */
public final class DatabaseInfoCountPanel extends javax.swing.JPanel {
    private static final long  serialVersionUID = -8537559082830438692L;
    private TableModelDatabaseInfo modelDatabaseInfo;
    private volatile boolean listenToDbChanges;

    public DatabaseInfoCountPanel() {
        initComponents();
        table.setDefaultRenderer(Object.class, new TableCellRendererDatabaseInfoColumns());
        setLabelFilename();
    }

    public void listenToDatabaseChanges(boolean listen) {
        listenToDbChanges = listen;

        if (listen) {
            setModelDatabaseInfo();
        }

        if (modelDatabaseInfo != null) {
            modelDatabaseInfo.setListenToDatabase(listen);
        }
    }

    private void setLabelFilename() {
        ResourceBundle bundle = ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        String pattern = bundle.getString("DatabaseInfoCountPanel.labelFilename.Filename");
        UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
        String databaseFileName = provider.getDatabaseFileName(Filename.FULL_PATH);
        String message = MessageFormat.format(pattern, databaseFileName);

        labelFilename.setText(message);
    }

    private void setModelDatabaseInfo() {
        if (modelDatabaseInfo == null) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    EventQueueUtil.invokeInDispatchThread(new Runnable() {

                        @Override
                        public void run() {
                            modelDatabaseInfo = new TableModelDatabaseInfo();
                            modelDatabaseInfo.setListenToDatabase(listenToDbChanges);
                            table.setModel(modelDatabaseInfo);
                            modelDatabaseInfo.update();
                        }
                    });
                }
            }, "JPhotoTagger: Updating database info");

            thread.start();
        } else {
            modelDatabaseInfo.setListenToDatabase(true);
            modelDatabaseInfo.update();
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        labelTable = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        labelFilename = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        labelTable.setText(bundle.getString("DatabaseInfoCountPanel.labelTable.text")); // NOI18N
        labelTable.setName("labelTable"); // NOI18N

        scrollPane.setName("scrollPane"); // NOI18N

        table.setName("table"); // NOI18N
        scrollPane.setViewportView(table);

        labelFilename.setText(bundle.getString("DatabaseInfoCountPanel.labelFilename.text")); // NOI18N
        labelFilename.setName("labelFilename"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                    .addComponent(labelTable, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelFilename, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelTable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelFilename;
    private javax.swing.JLabel labelTable;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
