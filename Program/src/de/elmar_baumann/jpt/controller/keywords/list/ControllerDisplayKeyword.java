/*
 * @(#)ControllerDisplayKeyword.java    Created on 2010-01-17
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

package de.elmar_baumann.jpt.controller.keywords.list;

import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.helper.KeywordsHelper;
import de.elmar_baumann.jpt.model.ListModelKeywords;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsList;
import de.elmar_baumann.lib.componentutil.ListUtil;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.List;

import javax.swing.ListModel;

/**
 * Displays a selected keyword through selecting it in the selection list.
 *
 * @author  Elmar Baumann
 */
public final class ControllerDisplayKeyword extends ControllerKeywords {
    public ControllerDisplayKeyword() {
        listenToActionsOf(
            PopupMenuKeywordsList.INSTANCE.getItemDisplayImages());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return false;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource().equals(
            PopupMenuKeywordsList.INSTANCE.getItemDisplayImages());
    }

    @Override
    protected void action(List<String> keywords) {
        ListModel model =
            ModelFactory.INSTANCE.getModel(ListModelKeywords.class);
        List<Integer> indices = ListUtil.getIndicesOfItems(model, keywords);

        if (!indices.isEmpty()) {
            KeywordsHelper.selectInSelKeywordsList(indices);
        }
    }
}
