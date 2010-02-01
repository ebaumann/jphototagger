/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.datatransfer;

import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.helper.KeywordsHelper;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Imports into an {@link JTextField} and {@link JTextArea} strings exported via
 * a {@link TransferHandlerDragListItems} or from a
 * {@link DefaultMutableTreeNode} with an {@link Keyword} as user
 * object when it's data flavor is {@link DataFlavor#stringFlavor}.
 *
 * When multiple items exported, only the first will be inserted.
 *
 * Does <em>not</em> support moving data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public final class TransferHandlerDropEdit extends TransferHandler {

    private static final long serialVersionUID = 4543789065456550151L;

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {

        return transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor) ||
                Flavor.hasKeywordsFromTree(transferSupport) ||
                Flavor.hasKeywordsFromList(transferSupport) ||
                Flavor.hasMetadataTemplate(transferSupport);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JTextComponent) {
            return new StringSelection(((JTextComponent) c).getSelectedText());
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        Component    component    = transferSupport.getComponent();
        String       string       = null;
        Transferable transferable = transferSupport.getTransferable();

        if (transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor)) {

            string = Support.getString(transferable);

        } else if (Flavor.hasKeywordsFromList(transferSupport)) {

            string = getStrings(Support.getKeywords(transferable));

        } else if (Flavor.hasKeywordsFromTree(transferSupport)) {

            string = getStrings(Support.getKeywordNodes(transferable));

        } else if (Flavor.hasMetadataTemplate(transferSupport)) {
            
            MetadataTemplateSupport.setTemplate(transferSupport);
            return true;
        }

        if (string == null) return false;

        if (component instanceof JTextArea) {

            setText((JTextArea) component, string);

        } else if (component instanceof JTextField) {

            setText((JTextField) component, string);

        } else {
            return false;
        }
        return true;
    }

    private String getStrings(Object[] array) {
        if (array == null || array.length == 0 || array[0] == null) return null;

        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (Object o : array) {
            sb.append(index++ == 0 ? "" : ";");
            sb.append(o.toString());
        }

        return sb.toString();
    }

    private String getStrings(List<DefaultMutableTreeNode> nodes) {
        if (nodes.size() <= 0) return null;

        List<String> keywords = new ArrayList<String>();

        for (DefaultMutableTreeNode node : nodes) {
            keywords.addAll(KeywordsHelper.getKeywordStrings(node, true));
        }

        if (keywords.size() == 0) return null;

        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (String keyword : keywords) {
            sb.append(index++ == 0 ? "" : ";");
            sb.append(keyword);
        }

        return sb.toString();
    }

    private void setText(JTextArea textArea, String text) {
        if (textArea.getSelectedText() == null) {
            textArea.setText(textArea.getText() + text);
        } else {
            textArea.setText(text);
        }
    }

    private void setText(JTextField textField, String text) {
        if (textField.getSelectedText() == null) {
            textField.setText(textField.getText() + text);
        } else {
            textField.setText(text);
        }
    }
}
