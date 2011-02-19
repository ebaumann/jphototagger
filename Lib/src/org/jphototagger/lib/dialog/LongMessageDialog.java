package org.jphototagger.lib.dialog;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Frame;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.jphototagger.lib.clipboard.ClipboardUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.resource.JslBundle;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.lib.util.Settings;

/**
 * Dialog for displaying long messages.
 *
 * @author Elmar Baumann
 */
public class LongMessageDialog extends Dialog {
    private static final long serialVersionUID = 4797253320918587438L;
    private String mailTo;
    private String mailSubject;
    private static final Map<Integer, String> ICON_KEY = new HashMap<Integer, String>();

    static {
        ICON_KEY.put(JOptionPane.ERROR_MESSAGE, "OptionPane.errorIcon");
        ICON_KEY.put(JOptionPane.QUESTION_MESSAGE, "OptionPane.questionIcon");
        ICON_KEY.put(JOptionPane.INFORMATION_MESSAGE,
                "OptionPane.informationIcon");
        ICON_KEY.put(JOptionPane.WARNING_MESSAGE, "OptionPane.warningIcon");
    }

    public LongMessageDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        init();
    }

    public LongMessageDialog(JDialog owner, Settings settings,
                             String settingsKey) {
        super(owner, settings, settingsKey);
        init();
    }

    public LongMessageDialog(Frame owner, Settings settings,
                             String settingsKey) {
        super(owner, settings, settingsKey);
        init();
    }

    public LongMessageDialog(JDialog owner, boolean modal, Settings settings,
                             String settingsKey) {
        super(owner, modal, settings, settingsKey);
        init();
    }

    public LongMessageDialog(Frame owner, boolean modal, Settings settings,
                             String settingsKey) {
        super(owner, modal, settings, settingsKey);
        init();
    }

    private void init() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
    }

    public void setMail(String to, String subject) {
        if (to == null) {
            throw new NullPointerException("to == null");
        }

        if (subject == null) {
            throw new NullPointerException("subject == null");
        }

        mailTo = to;
        mailSubject = subject;
        buttonMail.setEnabled(SystemUtil.canMail());
    }

    public void setShortMessage(String message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }

        labelShortMessage.setText(message);
    }

    public void setLongMessage(String message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }

        textArea.setText(message);
    }

    public void setErrorIcon() {
        labelIcon.setIcon(
                UIManager.getIcon(ICON_KEY.get(JOptionPane.ERROR_MESSAGE)));
    }

    public void setWarningIcon() {
        labelIcon.setIcon(
                UIManager.getIcon(ICON_KEY.get(JOptionPane.WARNING_MESSAGE)));
    }

    public void setInformationIcon() {
        labelIcon.setIcon(
                UIManager.getIcon(ICON_KEY.get(JOptionPane.INFORMATION_MESSAGE)));
    }

    public void setQuestionIcon() {
        labelIcon.setIcon(
                UIManager.getIcon(ICON_KEY.get(JOptionPane.QUESTION_MESSAGE)));
    }

    private void copyToClipboard() {
        ClipboardUtil.copyToSystemClipboard(textArea.getText(), null);
    }

    private void sendMail() {
        try {
            URI uri = new URI("mailto", mailTo + "?subject=" + mailSubject,
                              null);

            Desktop.getDesktop().mail(uri);
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
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
        java.awt.GridBagConstraints gridBagConstraints;

        labelIcon = new javax.swing.JLabel();
        labelShortMessage = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        panelControls = new javax.swing.JPanel();
        panelSearch = new org.jphototagger.lib.component.TextAreaSearchPanel();
        panelSearch.setTextArea(textArea);
        buttonMail = new javax.swing.JButton();
        buttonCopy = new javax.swing.JButton();
        buttonClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        labelIcon.setName("labelIcon"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(labelIcon, gridBagConstraints);

        labelShortMessage.setText(JslBundle.INSTANCE.getString("LongMessageDialog.labelShortMessage.text")); // NOI18N
        labelShortMessage.setName("labelShortMessage"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        getContentPane().add(labelShortMessage, gridBagConstraints);

        scrollPane.setName("scrollPane"); // NOI18N

        textArea.setColumns(20);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        textArea.setName("textArea"); // NOI18N
        scrollPane.setViewportView(textArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(scrollPane, gridBagConstraints);

        panelControls.setName("panelControls"); // NOI18N

        panelSearch.setName("panelSearch"); // NOI18N

        buttonMail.setText(JslBundle.INSTANCE.getString("LongMessageDialog.buttonMail.text")); // NOI18N
        buttonMail.setToolTipText(JslBundle.INSTANCE.getString("LongMessageDialog.buttonMail.toolTipText")); // NOI18N
        buttonMail.setEnabled(false);
        buttonMail.setName("buttonMail"); // NOI18N
        buttonMail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMailActionPerformed(evt);
            }
        });

        buttonCopy.setText(JslBundle.INSTANCE.getString("LongMessageDialog.buttonCopy.text")); // NOI18N
        buttonCopy.setToolTipText(JslBundle.INSTANCE.getString("LongMessageDialog.buttonCopy.toolTipText")); // NOI18N
        buttonCopy.setName("buttonCopy"); // NOI18N
        buttonCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCopyActionPerformed(evt);
            }
        });

        buttonClose.setText(JslBundle.INSTANCE.getString("LongMessageDialog.buttonClose.text")); // NOI18N
        buttonClose.setToolTipText(JslBundle.INSTANCE.getString("LongMessageDialog.buttonClose.toolTipText")); // NOI18N
        buttonClose.setName("buttonClose"); // NOI18N
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelControlsLayout = new javax.swing.GroupLayout(panelControls);
        panelControls.setLayout(panelControlsLayout);
        panelControlsLayout.setHorizontalGroup(
            panelControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonMail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonCopy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonClose))
        );

        panelControlsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonClose, buttonCopy, buttonMail});

        panelControlsLayout.setVerticalGroup(
            panelControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelControlsLayout.createSequentialGroup()
                .addComponent(panelSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonClose)
                    .addComponent(buttonCopy)
                    .addComponent(buttonMail)))
        );

        panelControlsLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonClose, buttonCopy, buttonMail, panelSearch});

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(panelControls, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCopyActionPerformed
        copyToClipboard();
    }//GEN-LAST:event_buttonCopyActionPerformed

    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCloseActionPerformed

    private void buttonMailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMailActionPerformed
        sendMail();
    }//GEN-LAST:event_buttonMailActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                LongMessageDialog dialog =
                    new LongMessageDialog(new javax.swing.JFrame(), true);

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
    private javax.swing.JButton buttonClose;
    private javax.swing.JButton buttonCopy;
    private javax.swing.JButton buttonMail;
    private javax.swing.JLabel labelIcon;
    private javax.swing.JLabel labelShortMessage;
    private javax.swing.JPanel panelControls;
    private org.jphototagger.lib.component.TextAreaSearchPanel panelSearch;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}
