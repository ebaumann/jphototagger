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
package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.helper.HierarchicalKeywordsHelper;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Imports into an {@link JTextField} and {@link JTextArea} strings exported via
 * a {@link TransferHandlerDragListItems} or from a
 * {@link DefaultMutableTreeNode} with an {@link HierarchicalKeyword} as user
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

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor) ||
                Flavors.hasCategories(transferSupport) || Flavors.
                hasHierarchicalKeywords(transferSupport) || Flavors.hasKeywords(
                transferSupport);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        Component c = transferSupport.getComponent();
        String string = null;
        Transferable transferable = transferSupport.getTransferable();
        if (transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            string = Support.getString(transferable);
        } else if (Flavors.hasCategories(transferSupport)) {
            string = getFirstString(Support.getCategories(transferable));
        } else if (Flavors.hasKeywords(transferSupport)) {
            string = getFirstString(Support.getKeywords(transferable));
        } else if (Flavors.hasHierarchicalKeywords(transferSupport)) {
            string = getFirstString(
                    Support.getHierarchicalKeywordsNodes(transferable));
        }
        if (string == null) return false;
        if (c instanceof JTextArea) {
            setText((JTextArea) c, string);
        } else if (c instanceof JTextField) {
            setText((JTextField) c, string);
        } else {
            return false;
        }
        return true;
    }

    private String getFirstString(Object[] array) {
        if (array == null || array.length == 0 || array[0] == null) return null;
        return array[0].toString();
    }

    private String getFirstString(List<DefaultMutableTreeNode> nodes) {
        if (nodes.size() <= 0) return null;
        List<String> keywords =
                HierarchicalKeywordsHelper.getKeywordStrings(nodes.get(0), true);
        if (keywords.size() == 0) return null;
        return keywords.get(0);
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
