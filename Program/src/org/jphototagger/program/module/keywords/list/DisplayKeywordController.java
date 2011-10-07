package org.jphototagger.program.module.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ListModel;

import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.module.keywords.KeywordsHelper;
import org.jphototagger.program.app.ui.WaitDisplay;

/**
 * Displays a selected keyword through selecting it in the selection list.
 *
 * @author Elmar Baumann
 */
public final class DisplayKeywordController extends KeywordsController {

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

        WaitDisplay.INSTANCE.show();

        ListModel model = ModelFactory.INSTANCE.getModel(KeywordsListModel.class);
        List<Integer> modelIndices = ListUtil.getModelIndicesOfItems(model, keywords);

        if (!modelIndices.isEmpty()) {
            KeywordsHelper.selectInSelKeywordsList(modelIndices);
        }

        WaitDisplay.INSTANCE.hide();
    }
}
