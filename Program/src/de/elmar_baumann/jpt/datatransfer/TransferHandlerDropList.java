/*
 * @(#)TransferHandlerDropList.java    2009-08-02
 *
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

package de.elmar_baumann.jpt.datatransfer;

import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.helper.KeywordsHelper;

import java.awt.datatransfer.Transferable;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Imports into a {@link DefaultListModel} of strings strings exported via a
 * {@link TransferHandlerDragListItems} or from a
 * {@link DefaultMutableTreeNode} with an {@link Keyword} as user
 * object.
 *
 * The list model has to be <em>of the type {@link DefaultListModel}</em> and
 * it's elements {@link String}s.
 *
 * Does <em>not</em> support moving data.
 *
 * @author  Elmar Baumann
 */
public final class TransferHandlerDropList extends TransferHandler {
    private static final long serialVersionUID = -3654778661471221382L;

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return Flavor.hasKeywordsFromList(transferSupport)
               || Flavor.hasKeywordsFromTree(transferSupport)
               || Flavor.hasMetadataTemplate(transferSupport);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) {
            return false;
        }

        JList            list         = (JList) transferSupport.getComponent();
        DefaultListModel listModel    = (DefaultListModel) list.getModel();
        Transferable     transferable = transferSupport.getTransferable();

        if (Flavor.hasKeywordsFromList(transferSupport)) {
            return importKeywords(transferable, listModel);
        } else if (Flavor.hasKeywordsFromTree(transferSupport)) {
            return importKeywords(listModel, transferSupport.getTransferable());
        } else if (Flavor.hasMetadataTemplate(transferSupport)) {
            MetadataTemplateSupport.setTemplate(transferSupport);

            return true;
        }

        return false;
    }

    private boolean importKeywords(Transferable transferable,
                                   DefaultListModel listModel) {
        Object[] keywords = Support.getKeywords(transferable);

        if (keywords == null) {
            return false;
        }

        return importStringArray(listModel, keywords);
    }

    private boolean importStringArray(DefaultListModel listModel,
                                      Object[] array) {
        for (Object o : array) {
            listModel.addElement(o);
        }

        return true;
    }

    private boolean importKeywords(DefaultListModel listModel,
                                   Transferable transferable) {
        List<DefaultMutableTreeNode> nodes =
            Support.getKeywordNodes(transferable);

        for (DefaultMutableTreeNode node : nodes) {
            importKeywords(node, listModel);
        }

        return true;
    }

    private void importKeywords(DefaultMutableTreeNode node,
                                DefaultListModel listModel) {
        for (String keyword : KeywordsHelper.getKeywordStrings(node, true)) {
            if (!listModel.contains(keyword)) {
                listModel.addElement(keyword);
            }
        }
    }
}
