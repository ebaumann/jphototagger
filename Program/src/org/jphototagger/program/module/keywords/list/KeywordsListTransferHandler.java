package org.jphototagger.program.module.keywords.list;

import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.jdesktop.swingx.JXList;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.lib.datatransfer.TransferableObject;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.datatransfer.DataTransferSupport;
import org.jphototagger.program.datatransfer.Flavor;
import org.jphototagger.program.module.miscmetadata.MiscMetadataUtil;

/**
 * Transfer handler for {@code KeywordsPanel#getList()}.
 *
 * Creates a {@code Transferable} with selected keywords as content. The
 * transferable is a {@code TransferableObject} instance which supports the data
 * flavor {@code Flavor#KEYWORDS_LIST}.
 *
 * @author Elmar Baumann
 */
public final class KeywordsListTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean canImport(TransferSupport support) {
        return Flavor.hasFiles(support.getTransferable()) && (TransferUtil.getListDropIndex(support) >= 0);
    }

    @Override
    public boolean importData(TransferSupport support) {
        int index = TransferUtil.getListDropIndex(support);

        if (index < 0) {
            return false;
        }

        JXList list = (JXList) support.getComponent();
        int modelIndex = list.convertIndexToModel(index);
        Object value = list.getModel().getElementAt(modelIndex);

        if (value instanceof String) {
            String keyword = (String) value;
            List<File> imageFiles = DataTransferSupport.getImageFiles(support);
            int fileCount = imageFiles.size();

            if ((fileCount > 0) && confirmImport(keyword, fileCount)) {
                MetaDataValueData cd = new MetaDataValueData(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, value);

                MiscMetadataUtil.saveToImageFiles(Collections.singletonList(cd), imageFiles);

                return true;
            }
        }

        return false;
    }

    private boolean confirmImport(String keyword, int fileCount) {
        String message = Bundle.getString(KeywordsListTransferHandler.class, "KeywordsListTransferHandler.Confirm.Import", keyword, fileCount);

        return MessageDisplayer.confirmYesNo(null, message);
    }

    /**
     * Returns the keywords in a transferable object.
     *
     * <em>The transferable has to support the data flavor
     * {@code Flavor#KEYWORDS_LIST}!</em>
     *
     * @param  transferable transferable object
     * @return              keywords or null on errors
     */
    public static Object[] getKeywords(Transferable transferable) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        try {
            return (Object[]) transferable.getTransferData(Flavor.KEYWORDS_LIST);
        } catch (Exception ex) {
            Logger.getLogger(KeywordsListTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JXList list = (JXList) c;
        Object[] selValues = list.getSelectedValues();

        return new TransferableObject(selValues, Flavor.KEYWORDS_LIST);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }
}
