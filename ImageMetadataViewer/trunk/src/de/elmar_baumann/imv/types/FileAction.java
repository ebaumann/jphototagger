package de.elmar_baumann.imv.types;

import javax.swing.TransferHandler;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
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

    private FileAction(Integer transferHandlerAction) {
        this.transferHandlerAction = transferHandlerAction;
    }
}
