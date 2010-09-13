/*
 * @(#)TransferHandlerDirectoryTree.java    Created on 2008-10-26
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.datatransfer;

import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.lib.datatransfer.TransferUtil.FilenameDelimiter;
import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.io.ImageUtil;
import org.jphototagger.program.io.ImageUtil.ConfirmOverwrite;
import org.jphototagger.program.io.IoUtil;

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
 * @author  Elmar Baumann
 */
public final class TransferHandlerDirectoryTree extends TransferHandler {
    private static final long serialVersionUID = 667981391265349868L;

    @Override
    public boolean canImport(TransferSupport support) {
        if (!Flavor.hasFiles(support.getTransferable())) {
            return false;
        }

        JTree.DropLocation dropLocation =
            (JTree.DropLocation) support.getDropLocation();

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
    public boolean importData(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }

        File       targetDirectory = getTargetDirectory(support);
        List<File> sourceFiles =
            TransferUtil.getFiles(support.getTransferable(),
                                  FilenameDelimiter.EMPTY);

        if ((targetDirectory != null) &&!sourceFiles.isEmpty()) {
            handleDroppedFiles(support.getUserDropAction(), sourceFiles,
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
    public static void handleDroppedFiles(int dropAction,
            List<File> sourceFiles, File targetDirectory) {
        if (sourceFiles == null) {
            throw new NullPointerException("sourceFiles == null");
        }

        if (targetDirectory == null) {
            throw new NullPointerException("targetDirectory == null");
        }

        List<File> imageFiles = IoUtil.filterImageFiles(sourceFiles);

        if (imageFiles.isEmpty()) {
            return;
        }

        if (dropAction == COPY) {
            ImageUtil.copyImageFiles(imageFiles, targetDirectory,
                                     ConfirmOverwrite.YES);
        } else if (dropAction == MOVE) {
            ImageUtil.moveImageFiles(imageFiles, targetDirectory,
                                     ConfirmOverwrite.YES);
        }
    }

    private File getTargetDirectory(TransferSupport support) {
        TreePath path =
            ((JTree.DropLocation) support.getDropLocation()).getPath();
        Object selNode = path.getLastPathComponent();

        if (selNode instanceof DefaultMutableTreeNode) {
            Object userObject =
                ((DefaultMutableTreeNode) selNode).getUserObject();

            if (userObject instanceof File) {
                return (File) userObject;
            } else if (userObject instanceof Favorite) {
                return ((Favorite) userObject).getDirectory();
            }
        } else if (selNode instanceof File) {
            return (File) selNode;
        }

        return null;
    }
}
