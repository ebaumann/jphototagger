/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.plugin.flickrupload;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-02-14
 */
public class LargeMessagesDialog extends javax.swing.JDialog {
    private static final long serialVersionUID = 8014430500435229969L;

    public LargeMessagesDialog(String message) {
        super((java.awt.Frame) null, true);
        initComponents();
        textPane.setText(message);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        scrollPane = new javax.swing.JScrollPane();
        textPane   = new javax.swing.JTextPane();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        java.util.ResourceBundle bundle =
            java.util.ResourceBundle.getBundle(
                "de/elmar_baumann/jpt/plugin/flickrupload/Bundle");    // NOI18N

        setTitle(bundle.getString("LargeMessagesDialog.title"));    // NOI18N
        textPane.setContentType(
            bundle.getString("LargeMessagesDialog.textPane.contentType"));    // NOI18N
        textPane.setEditable(false);
        scrollPane.setViewportView(textPane);

        javax.swing.GroupLayout layout =
            new javax.swing.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 623,
                    Short.MAX_VALUE).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addContainerGap().addComponent(
                    scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 294,
                    Short.MAX_VALUE).addContainerGap()));
        pack();
    }    // </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LargeMessagesDialog dialog =
                    new LargeMessagesDialog(
                        FlickrBundle.INSTANCE.getString(
                            "Auth.Info.GetToken.Browse"));

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
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextPane   textPane;

    // End of variables declaration//GEN-END:variables
}
