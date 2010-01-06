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

import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.helper.HierarchicalKeywordsHelper;
import java.awt.datatransfer.Transferable;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Imports into a {@link DefaultListModel} of strings strings exported via a
 * {@link TransferHandlerDragListItems} or from a
 * {@link DefaultMutableTreeNode} with an {@link HierarchicalKeyword} as user
 * object.
 *
 * The list model has to be <em>of the type {@link DefaultListModel}</em> and
 * it's elements {@link String}s.
 *
 * Does <em>not</em> support moving data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public final class TransferHandlerDropList extends TransferHandler {

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return  Flavors.hasKeywords            (transferSupport) ||
                Flavors.hasHierarchicalKeywords(transferSupport) ||
                Flavors.hasMetadataEditTemplate(transferSupport);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) return false;

        JList            list         = (JList) transferSupport.getComponent();
        DefaultListModel listModel    = (DefaultListModel) list.getModel();
        Transferable     transferable = transferSupport.getTransferable();

        if (Flavors.hasKeywords(transferSupport)) {

            return importKeywords(transferable, listModel);

        } else if (Flavors.hasHierarchicalKeywords(transferSupport)) {

            return importHierarchicalKeywords(listModel, transferSupport.getTransferable());

        } else if (Flavors.hasMetadataEditTemplate(transferSupport)) {

            MetadataEditTemplateSupport.setMetadataEditTemplate(transferSupport);
            return true;
        }
        return false;
    }

    private boolean importKeywords(Transferable transferable, DefaultListModel listModel) {

        Object[] keywords = Support.getKeywords(transferable);

        if (keywords == null) return false;

        return importStringArray(listModel, keywords);
    }

    private boolean importStringArray(DefaultListModel listModel, Object[] array) {
        for (Object o : array) {
            listModel.addElement(o);
        }
        return true;
    }

    private boolean importHierarchicalKeywords(DefaultListModel listModel, Transferable transferable) {

        List<DefaultMutableTreeNode> nodes = Support.getHierarchicalKeywordsNodes(transferable);

        for (DefaultMutableTreeNode node : nodes) {
            importHierarchicalKeywords(node, listModel);
        }
        return true;
    }

    private void importHierarchicalKeywords(DefaultMutableTreeNode node, DefaultListModel listModel) {

        for (String keyword : HierarchicalKeywordsHelper.getKeywordStrings(node, true)) {

            if (!listModel.contains(keyword)) {
                listModel.addElement(keyword);
            }
        }
    }
}
