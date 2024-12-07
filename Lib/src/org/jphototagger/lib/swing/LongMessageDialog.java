package org.jphototagger.lib.swing;

import java.awt.Container;
import java.awt.Desktop;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ClipboardUtil;
import org.jphototagger.lib.util.SystemUtil;
import org.jphototagger.resources.UiFactory;

/**
 * DialogExt for displaying long messages.
 *
 * @author Elmar Baumann
 */
public class LongMessageDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private String mailTo;
    private String mailSubject;
    private static final Map<Integer, String> ICON_KEY = new HashMap<>();

    static {
        ICON_KEY.put(JOptionPane.ERROR_MESSAGE, "OptionPane.errorIcon");
        ICON_KEY.put(JOptionPane.QUESTION_MESSAGE, "OptionPane.questionIcon");
        ICON_KEY.put(JOptionPane.INFORMATION_MESSAGE, "OptionPane.informationIcon");
        ICON_KEY.put(JOptionPane.WARNING_MESSAGE, "OptionPane.warningIcon");
    }

    public LongMessageDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
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
        labelIcon.setIcon(UIManager.getIcon(ICON_KEY.get(JOptionPane.ERROR_MESSAGE)));
    }

    public void setWarningIcon() {
        labelIcon.setIcon(UIManager.getIcon(ICON_KEY.get(JOptionPane.WARNING_MESSAGE)));
    }

    public void setInformationIcon() {
        labelIcon.setIcon(UIManager.getIcon(ICON_KEY.get(JOptionPane.INFORMATION_MESSAGE)));
    }

    public void setQuestionIcon() {
        labelIcon.setIcon(UIManager.getIcon(ICON_KEY.get(JOptionPane.QUESTION_MESSAGE)));
    }

    private void copyToClipboard() {
        ClipboardUtil.copyToSystemClipboard(textArea.getText(), null);
    }

    private void sendMail() {
        try {
            URI uri = new URI("mailto", mailTo + "?subject=" + mailSubject, null);
            Desktop.getDesktop().mail(uri);
        } catch (Throwable t) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, t);
        }
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        labelIcon = UiFactory.label();
        labelShortMessage = UiFactory.label();
        scrollPane = UiFactory.scrollPane();
        textArea = UiFactory.textArea();
        panelControls = UiFactory.panel();
        panelSearch = new org.jphototagger.lib.swing.TextComponentSearchPanel();
        panelSearch.setSearchableTextComponent(textArea);
        panelButtons = UiFactory.panel();
        buttonMail = UiFactory.button();
        buttonCopy = UiFactory.button();
        buttonClose = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        getContentPane().setLayout(new java.awt.GridBagLayout());

        labelIcon.setName("labelIcon"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        getContentPane().add(labelIcon, gridBagConstraints);

        labelShortMessage.setText(Bundle.getString(getClass(), "LongMessageDialog.labelShortMessage.text")); // NOI18N
        labelShortMessage.setName("labelShortMessage"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 5);
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
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        getContentPane().add(scrollPane, gridBagConstraints);

        panelControls.setName("panelControls"); // NOI18N
        panelControls.setLayout(new java.awt.GridBagLayout());

        panelSearch.setName("panelSearch"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelControls.add(panelSearch, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonMail.setText(Bundle.getString(getClass(), "LongMessageDialog.buttonMail.text")); // NOI18N
        buttonMail.setToolTipText(Bundle.getString(getClass(), "LongMessageDialog.buttonMail.toolTipText")); // NOI18N
        buttonMail.setEnabled(false);
        buttonMail.setName("buttonMail"); // NOI18N
        buttonMail.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMailActionPerformed(evt);
            }
        });
        panelButtons.add(buttonMail, new java.awt.GridBagConstraints());

        buttonCopy.setText(Bundle.getString(getClass(), "LongMessageDialog.buttonCopy.text")); // NOI18N
        buttonCopy.setToolTipText(Bundle.getString(getClass(), "LongMessageDialog.buttonCopy.toolTipText")); // NOI18N
        buttonCopy.setName("buttonCopy"); // NOI18N
        buttonCopy.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCopyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonCopy, gridBagConstraints);

        buttonClose.setText(Bundle.getString(getClass(), "LongMessageDialog.buttonClose.text")); // NOI18N
        buttonClose.setToolTipText(Bundle.getString(getClass(), "LongMessageDialog.buttonClose.toolTipText")); // NOI18N
        buttonClose.setName("buttonClose"); // NOI18N
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonClose, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        panelControls.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        getContentPane().add(panelControls, gridBagConstraints);

        pack();
    }

    private void buttonCopyActionPerformed(java.awt.event.ActionEvent evt) {
        copyToClipboard();
    }

    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    private void buttonMailActionPerformed(java.awt.event.ActionEvent evt) {
        sendMail();
    }

    private javax.swing.JButton buttonClose;
    private javax.swing.JButton buttonCopy;
    private javax.swing.JButton buttonMail;
    private javax.swing.JLabel labelIcon;
    private javax.swing.JLabel labelShortMessage;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelControls;
    private org.jphototagger.lib.swing.TextComponentSearchPanel panelSearch;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextArea textArea;
}
