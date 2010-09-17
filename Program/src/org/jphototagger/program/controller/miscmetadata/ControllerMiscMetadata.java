/*
 * @(#)ControllerMiscMetadata.java    Created on 2010-03-15
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

package org.jphototagger.program.controller.miscmetadata;

import org.jphototagger.lib.event.listener.PopupMenuTree;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.helper.MiscMetadataHelper;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.Arrays;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.jphototagger.program.resource.GUI;

/**
 *
 *
 * @author Elmar Baumann
 */
public abstract class ControllerMiscMetadata extends Controller
        implements PopupMenuTree.Listener {
    protected ControllerMiscMetadata() {
        listenToKeyEventsOf(GUI.getMiscMetadataTree());
    }

    protected abstract void action(Column column, String value);

    private void action(List<Pair<Column, String>> pairs) {
        for (Pair<Column, String> pair : pairs) {
            action(pair.getFirst(), pair.getSecond());
        }
    }

    @Override
    protected void action(ActionEvent evt) {
        throw new IllegalStateException("Shall never be called!");
    }

    @Override
    public void action(JTree tree, List<TreePath> treePaths) {
        if (treePaths == null) {
            throw new NullPointerException("treePaths == null");
        }

        action(MiscMetadataHelper.getColValuesFrom(treePaths));
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return false;
    }

    @Override
    protected void action(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        TreePath[] selPaths =
            GUI.getMiscMetadataTree().getSelectionPaths();

        if (selPaths != null) {
            action(MiscMetadataHelper.getColValuesFrom(
                Arrays.asList(selPaths)));
        }
    }
}
