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
package de.elmar_baumann.jpt.controller.keywords.list;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseSynonyms;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsList;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-09
 */
public final class ControllerEditKeywordSynonyms extends ControllerKeywords {

    private static final String DELIM = ";";

    public ControllerEditKeywordSynonyms() {
        listenToActionsOf(PopupMenuKeywordsList.INSTANCE.getItemEditSynonyms());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return KeyEventUtil.isControlAlt(evt, KeyEvent.VK_S);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == PopupMenuKeywordsList.INSTANCE.getItemEditSynonyms();
    }

    @Override
    protected void action(List<String> keywords) {
        for (String keyword : keywords) {
            addSynonyms(keyword);
        }
    }

    private void addSynonyms(String keyword) {
        Set<String> oldSynonyms = DatabaseSynonyms.INSTANCE.getSynonymsOf(keyword);
        String      synonyms    = MessageDisplayer.input("ControllerAddSynonyms.Info.Input", catSynonyms(oldSynonyms),
                                                         "ControllerAddSynonyms.Pos", keyword, DELIM);

        if (synonyms != null) {
            Set<String> newSynonyms = splitSynonyms(synonyms);
            for (String synonym : newSynonyms) {
                DatabaseSynonyms.INSTANCE.insert(keyword, synonym);
            }
            for (String synonym : oldSynonyms) {
                if (!newSynonyms.contains(synonym)) {
                    DatabaseSynonyms.INSTANCE.delete(keyword, synonym);
                }
            }
        }

    }

    private Set<String> splitSynonyms(String synonymString) {
        Set<String>     synonyms = new HashSet<String>();
        StringTokenizer st       = new StringTokenizer(synonymString, DELIM);

        while (st.hasMoreTokens()) {
            String synonym = st.nextToken().trim();
            if (!synonym.isEmpty()) {
                synonyms.add(synonym);
            }
        }
        return synonyms;
    }

    private String catSynonyms(Set<String> synonyms) {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (String synonym : synonyms) {
            sb.append(i++ == 0 ? "" : DELIM);
            sb.append(synonym);
        }

        return sb.toString();
    }
}
