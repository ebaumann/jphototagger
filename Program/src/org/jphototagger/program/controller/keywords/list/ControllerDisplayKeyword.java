package org.jphototagger.program.controller.keywords.list;

import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.model.ListModelKeywords;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;
import org.jphototagger.program.view.WaitDisplay;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.List;

import javax.swing.ListModel;

/**
 * Displays a selected keyword through selecting it in the selection list.
 *
 * @author Elmar Baumann
 */
public final class ControllerDisplayKeyword extends ControllerKeywords {
    public ControllerDisplayKeyword() {
        listenToActionsOf(
            PopupMenuKeywordsList.INSTANCE.getItemDisplayImages());
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

        return evt.getSource().equals(
            PopupMenuKeywordsList.INSTANCE.getItemDisplayImages());
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        WaitDisplay.show();

        ListModel model =
            ModelFactory.INSTANCE.getModel(ListModelKeywords.class);
        List<Integer> indices = ListUtil.getIndicesOfItems(model, keywords);

        if (!indices.isEmpty()) {
            KeywordsHelper.selectInSelKeywordsList(indices);
        }

        WaitDisplay.hide();
    }
}
