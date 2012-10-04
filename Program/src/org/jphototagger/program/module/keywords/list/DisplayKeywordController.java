package org.jphototagger.program.module.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.ListModel;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.lib.swing.util.ListUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.module.keywords.KeywordsUtil;
import org.openide.util.Lookup;

/**
 * Displays a selected keyword through selecting it in the selection list.
 *
 * @author Elmar Baumann
 */
public final class DisplayKeywordController extends KeywordsListController {

    public DisplayKeywordController() {
        listenToActionsOf(KeywordsListPopupMenu.INSTANCE.getItemDisplayImages());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return false;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource().equals(KeywordsListPopupMenu.INSTANCE.getItemDisplayImages());
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
        waitDisplayer.show();
        ListModel model = ModelFactory.INSTANCE.getModel(KeywordsListModel.class);
        List<Integer> modelIndices = ListUtil.getModelIndicesOfItems(model, keywords);

        if (!modelIndices.isEmpty()) {
            KeywordsUtil.selectInSelKeywordsList(modelIndices);
        }

        waitDisplayer.hide();
    }
}
