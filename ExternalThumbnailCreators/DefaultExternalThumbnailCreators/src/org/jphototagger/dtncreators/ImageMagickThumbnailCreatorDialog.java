package org.jphototagger.dtncreators;

import org.jphototagger.api.core.Branding;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public class ImageMagickThumbnailCreatorDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private boolean browse;
    private boolean chooseConvert;

    public ImageMagickThumbnailCreatorDialog() {
        super((java.awt.Frame) null, true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
    }

    public boolean isBrowse() {
        return browse;
    }

    public boolean isChooseConvert() {
        return chooseConvert;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        labelInfo = new org.jdesktop.swingx.JXLabel();
        buttonCancel = new javax.swing.JButton();
        buttonBrowse = new javax.swing.JButton();
        buttonChooseConvert = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/dtncreators/Bundle"); // NOI18N
        setTitle(bundle.getString("ImageMagickThumbnailCreatorDialog.title")); // NOI18N
        setIconImages(Lookup.getDefault().lookup(Branding.class).getAppIcons());

        labelInfo.setLineWrap(true);
        labelInfo.setText(bundle.getString("ImageMagickThumbnailCreatorDialog.labelInfo.text")); // NOI18N

        buttonCancel.setText(bundle.getString("ImageMagickThumbnailCreatorDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonBrowse.setText(bundle.getString("ImageMagickThumbnailCreatorDialog.buttonBrowse.text")); // NOI18N
        buttonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseActionPerformed(evt);
            }
        });

        buttonChooseConvert.setText(bundle.getString("ImageMagickThumbnailCreatorDialog.buttonChooseConvert.text")); // NOI18N
        buttonChooseConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseConvertActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonBrowse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChooseConvert))
                    .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonChooseConvert)
                    .addComponent(buttonBrowse)
                    .addComponent(buttonCancel))
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseActionPerformed
        browse = true;
        setVisible(false);
    }//GEN-LAST:event_buttonBrowseActionPerformed

    private void buttonChooseConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseConvertActionPerformed
        chooseConvert = true;
        setVisible(false);
    }//GEN-LAST:event_buttonChooseConvertActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ImageMagickThumbnailCreatorDialog dialog = new ImageMagickThumbnailCreatorDialog();
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
    private javax.swing.JButton buttonBrowse;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseConvert;
    private org.jdesktop.swingx.JXLabel labelInfo;
    // End of variables declaration//GEN-END:variables
}
