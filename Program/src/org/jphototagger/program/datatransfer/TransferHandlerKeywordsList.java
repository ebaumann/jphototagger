/*
 * @(#)TransferHandlerKeywordsList.java    Created on 2009-07-11
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

package org.jphototagger.program.datatransfer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.lib.datatransfer.TransferableObject;

import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * Transfer handler for {@link KeywordsPanel#getList()}.
 *
 * Creates a {@link Transferable} with selected keywords as content. The
 * transferable is a {@link TransferableObject} instance which supports the data
 * flavor {@link Flavor#KEYWORDS_LIST}.
 *
 * @author  Elmar Baumann
 */
public final class TransferHandlerKeywordsList extends TransferHandler {
    private static final long serialVersionUID = -4156977618928448144L;

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
            return (Object[]) transferable.getTransferData(
                Flavor.KEYWORDS_LIST);
        } catch (Exception e) {
            AppLogger.logSevere(TransferHandlerKeywordsList.class, e);
        }

        return null;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JList    list      = (JList) c;
        Object[] selValues = list.getSelectedValues();

        return new TransferableObject(selValues, Flavor.KEYWORDS_LIST);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }
}
