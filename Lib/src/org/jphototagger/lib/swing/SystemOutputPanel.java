package org.jphototagger.lib.swing;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

//Most parts of code from http://unserializableone.blogspot.com/2009/01/redirecting-systemout-and-systemerr-to.html

/**
 * Writes an the system's standard output to a text area.
 *
 * Starts after calling {@code #caputure()}.
 *
 * @author Elmar Baumann
 */
public class SystemOutputPanel extends PanelExt {
    private static final long  serialVersionUID = 1L;
    private static volatile int MAX_CHAR_COUNT = 100000;
    private static volatile int MAX_CHARS_EXESS  = 1000;
    private boolean  capture;

    /**
     * Default constructor.
     *
     * Call later {@code #caputure()}.
     */
    public SystemOutputPanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
    }

    /**
     * Starts to capture the system's standard output and standard error stream.
     * Redirects it via {@code System#setOut(java.io.PrintStream)} and
     * {@code System#setErr(java.io.PrintStream)}.
     */
    public synchronized void caputure() {
        if (!capture) {
            redirectSystemStreams();
        }
    }

    private void copyToClipboard() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(textArea.getText()), null);
    }

    private void deleteText() {
        textArea.setText("");
    }

    private void updateTextArea(final String text) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                textArea.append(text);
                textArea.setCaretPosition(textArea.getDocument().getLength());

                int excess = textArea.getDocument().getLength() - MAX_CHAR_COUNT;

                if (excess >= MAX_CHARS_EXESS) {
                    textArea.replaceRange("", 0, excess);
                }
            }
        });
    }

    public String getOutput() {
        return textArea.getText();
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPane = UiFactory.scrollPane();
        textArea = UiFactory.textArea();
        panelButttons = UiFactory.panel();
        searchPanel = new org.jphototagger.lib.swing.TextComponentSearchPanel();
        searchPanel.setSearchableTextComponent(textArea);
        buttonDelete = UiFactory.button();
        buttonCopyToClipboard = UiFactory.button();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        scrollPane.setName("scrollPane"); // NOI18N
        scrollPane.setPreferredSize(UiFactory.dimension(300, 200));

        textArea.setEditable(false);
        textArea.setColumns(1);
        textArea.setLineWrap(true);
        textArea.setTabSize(4);
        textArea.setWrapStyleWord(true);
        textArea.setName("textArea"); // NOI18N
        scrollPane.setViewportView(textArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);

        panelButttons.setName("panelButttons"); // NOI18N
        panelButttons.setLayout(new java.awt.GridBagLayout());

        searchPanel.setName("searchPanel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelButttons.add(searchPanel, gridBagConstraints);

        buttonDelete.setText(Bundle.getString(getClass(), "SystemOutputPanel.buttonDelete.text")); // NOI18N
        buttonDelete.setToolTipText(Bundle.getString(getClass(), "SystemOutputPanel.buttonDelete.toolTipText")); // NOI18N
        buttonDelete.setName("buttonDelete"); // NOI18N
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 30, 0, 0);
        panelButttons.add(buttonDelete, gridBagConstraints);

        buttonCopyToClipboard.setText(Bundle.getString(getClass(), "SystemOutputPanel.buttonCopyToClipboard.text")); // NOI18N
        buttonCopyToClipboard.setToolTipText(Bundle.getString(getClass(), "SystemOutputPanel.buttonCopyToClipboard.toolTipText")); // NOI18N
        buttonCopyToClipboard.setName("buttonCopyToClipboard"); // NOI18N
        buttonCopyToClipboard.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCopyToClipboardActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButttons.add(buttonCopyToClipboard, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        add(panelButttons, gridBagConstraints);
    }

    private void buttonCopyToClipboardActionPerformed(java.awt.event.ActionEvent evt) {
        copyToClipboard();
    }

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        deleteText();
    }

    private javax.swing.JButton buttonCopyToClipboard;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JPanel panelButttons;
    private javax.swing.JScrollPane scrollPane;
    private org.jphototagger.lib.swing.TextComponentSearchPanel searchPanel;
    private javax.swing.JTextArea textArea;
}
