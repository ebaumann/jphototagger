package org.jphototagger.program.datatransfer;

import org.jphototagger.lib.datatransfer.TransferableObject;
import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.domain.ColumnData;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.helper.MiscMetadataHelper;
import org.jphototagger.program.view.panels.KeywordsPanel;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import org.jdesktop.swingx.JXList;
import javax.swing.TransferHandler;

/**
 * Transfer handler for {@link KeywordsPanel#getList()}.
 *
 * Creates a {@link Transferable} with selected keywords as content. The
 * transferable is a {@link TransferableObject} instance which supports the data
 * flavor {@link Flavor#KEYWORDS_LIST}.
 *
 * @author Elmar Baumann
 */
public final class TransferHandlerKeywordsList extends TransferHandler {
    private static final long serialVersionUID = -4156977618928448144L;

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
            List<File> imageFiles = Support.getImageFiles(support);
            int fileCount = imageFiles.size();

            if ((fileCount > 0) && confirmImport(keyword, fileCount)) {
                ColumnData cd = new ColumnData(ColumnXmpDcSubjectsSubject.INSTANCE, value);

                MiscMetadataHelper.saveToImageFiles(Collections.singletonList(cd), imageFiles);

                return true;
            }
        }

        return false;
    }

    private boolean confirmImport(String keyword, int fileCount) {
        return MessageDisplayer.confirmYesNo(null, "TransferHandlerKeywordsList.Confirm.Import", keyword, fileCount);
    }

    /**
     * Returns the keywords in a transferable object.
     *
     * <em>The transferable has to support the data flavor
     * {@link Flavor#KEYWORDS_LIST}!</em>
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
            AppLogger.logSevere(TransferHandlerKeywordsList.class, ex);
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
