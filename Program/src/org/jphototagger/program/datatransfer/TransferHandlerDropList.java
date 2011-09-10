package org.jphototagger.program.datatransfer;

import java.awt.datatransfer.Transferable;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXList;
import org.jphototagger.domain.keywords.Keyword;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.program.helper.KeywordsHelper;

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
 * @author Elmar Baumann
 */
public final class TransferHandlerDropList extends TransferHandler {

    private static final long serialVersionUID = -3654778661471221382L;

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return Flavor.isMetadataTransferred(support.getTransferable());
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }

        JXList list = (JXList) support.getComponent();
        DefaultListModel listModel = (DefaultListModel) list.getModel();
        Transferable transferable = support.getTransferable();

        if (Flavor.hasKeywordsFromList(support)) {
            return importKeywords(transferable, listModel);
        } else if (Flavor.hasKeywordsFromTree(support)) {
            return importKeywords(listModel, transferable);
        } else if (Flavor.hasMetaDataValue(support)) {
            return importMetaDataValue(listModel, transferable);
        } else if (Flavor.hasMetadataTemplate(support)) {
            MetadataTemplateSupport.setTemplate(support);

            return true;
        }

        return false;
    }

    private boolean importMetaDataValue(DefaultListModel listModel, Transferable transferable) {
        Collection<? extends MetaDataValueData> metaDataValues = Support.getMetaDataValueData(transferable);

        if (metaDataValues != null) {
            for (MetaDataValueData metaDataValue : metaDataValues) {
                listModel.addElement(metaDataValue.getData());
            }
        }

        return false;
    }

    private boolean importKeywords(Transferable transferable, DefaultListModel listModel) {
        Object[] keywords = Support.getKeywords(transferable);

        if (keywords == null) {
            return false;
        }

        return importStringArray(listModel, keywords);
    }

    private boolean importStringArray(DefaultListModel listModel, Object[] array) {
        for (Object o : array) {
            listModel.addElement(o);
        }

        return true;
    }

    private boolean importKeywords(DefaultListModel listModel, Transferable transferable) {
        List<DefaultMutableTreeNode> nodes = Support.getKeywordNodes(transferable);

        for (DefaultMutableTreeNode node : nodes) {
            importKeywords(node, listModel);
        }

        return true;
    }

    private void importKeywords(DefaultMutableTreeNode node, DefaultListModel listModel) {
        for (String keyword : KeywordsHelper.getKeywordStrings(node, true)) {
            if (!listModel.contains(keyword)) {
                listModel.addElement(keyword);
            }
        }
    }
}
