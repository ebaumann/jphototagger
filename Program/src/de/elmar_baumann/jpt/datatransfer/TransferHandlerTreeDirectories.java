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
package de.elmar_baumann.jpt.datatransfer;

import de.elmar_baumann.jpt.data.FavoriteDirectory;
import de.elmar_baumann.jpt.io.ImageUtil;
import de.elmar_baumann.jpt.io.ImageUtil.ConfirmOverwrite;
import de.elmar_baumann.jpt.io.IoUtil;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-26
 */
public final class TransferHandlerTreeDirectories extends TransferHandler {

    private static final long serialVersionUID = 667981391265349868L;

    @Override
    public boolean canImport(TransferSupport transferSupport) {

        if (!Flavors.hasFiles(transferSupport.getTransferable())) {
            return false;
        }

        JTree.DropLocation dropLocation = (JTree.DropLocation) transferSupport.getDropLocation();

        return dropLocation.getPath() != null;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.NONE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferSupport transferSupport) {

        if (!transferSupport.isDrop()) return false;

        File targetDirectory = getTargetDirectory(transferSupport);

        List<File> sourceFiles = TransferUtil.getFiles(transferSupport.getTransferable(), "");

        if (targetDirectory != null && !sourceFiles.isEmpty()) {
            handleDroppedFiles(
                    transferSupport.getUserDropAction(), sourceFiles,
                    targetDirectory);
        }
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        // ignore, moving removes files from source directory
    }

    /**
     * Handles dropped files: Asks whether to copy or move and if confirmed
     * copys or moves the files.
     *
     * @param dropAction
     * @param sourceFiles
     * @param targetDirectory  target directory
     */
    public static void handleDroppedFiles(int dropAction, List<File> sourceFiles, File targetDirectory) {

        List<File> imageFiles = IoUtil.filterImageFiles(sourceFiles);

        if (imageFiles.isEmpty()) return;

        if (dropAction == COPY) {
            ImageUtil.copyImageFiles(imageFiles, targetDirectory, ConfirmOverwrite.YES);
        } else if (dropAction == MOVE) {
            ImageUtil.moveImageFiles(imageFiles, targetDirectory, ConfirmOverwrite.YES);
        }
    }

    private File getTargetDirectory(TransferSupport transferSupport) {

        TreePath path    = ((JTree.DropLocation) transferSupport.getDropLocation()).getPath();
        Object   selNode = path.getLastPathComponent();

        if (selNode instanceof DefaultMutableTreeNode) {
            Object userObject = ((DefaultMutableTreeNode) selNode).getUserObject();

            if (userObject instanceof File) {
                return (File) userObject;
            } else if (userObject instanceof FavoriteDirectory) {
                return ((FavoriteDirectory) userObject).getDirectory();
            }

            return (File) selNode;
        }
        return null;
    }
}
