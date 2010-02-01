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
package de.elmar_baumann.jpt.types;

import javax.swing.TransferHandler;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-26
 */
public enum FileAction {

    COPY(TransferHandler.COPY),
    CUT(TransferHandler.MOVE),
    MOVE(TransferHandler.MOVE),
    UNDEFINED(null),;
    /**
     * Action equivalent for a {@link TransferHandler}
     */
    private final Integer transferHandlerAction;

    /**
     * Returns the action equivalent for a {@link TransferHandler}.
     *
     * @return action or null if the transfer handler has no such action
     */
    public Integer getTransferHandlerAction() {
        return transferHandlerAction;
    }

    /**
     * Returns whether this action is a {@link FileAction#COPY} or a
     * {@link FileAction#MOVE}.
     *
     * @return true if this action is a copy action or a move action
     */
    public boolean isCopyOrMove() {
        return this.equals(COPY) || this.equals(MOVE);
    }

    private FileAction(Integer transferHandlerAction) {
        this.transferHandlerAction = transferHandlerAction;
    }
}
