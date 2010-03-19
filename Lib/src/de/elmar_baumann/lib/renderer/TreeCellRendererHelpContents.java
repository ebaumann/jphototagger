/*
 * @(#)TreeCellRendererHelpContents.java    Created on 2008-10-02
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

package de.elmar_baumann.lib.renderer;

import de.elmar_baumann.lib.image.util.IconUtil;
import de.elmar_baumann.lib.util.help.HelpNode;
import de.elmar_baumann.lib.util.help.HelpPage;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renders items and text for a tree displaying help contents for values that
 * are an instance of
 * {@link de.elmar_baumann.lib.util.help.HelpNode} and
 * {@link de.elmar_baumann.lib.util.help.HelpPage}.
 *
 * @author  Elmar Baumann
 */
public final class TreeCellRendererHelpContents
        extends DefaultTreeCellRenderer {
    private static final ImageIcon ICON_SECTION_COLLAPSED =
        IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/icons/icon_help_section_collapsed.png");
    private static final ImageIcon ICON_SECTION_EXPANDED =
        IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/icons/icon_help_section_expanded.png");
    private static final ImageIcon ICON_PAGE =
        IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/icons/icon_help_page.png");
    private static final long serialVersionUID = 205076451185009235L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, false,
                                           row, hasFocus);

        if (value instanceof HelpPage) {
            setIcon(ICON_PAGE);
            setText(((HelpPage) value).getTitle());
        } else if (value instanceof HelpNode) {
            setIcon(expanded
                    ? ICON_SECTION_EXPANDED
                    : ICON_SECTION_COLLAPSED);
            setText(((HelpNode) value).getTitle());
        } else if (value == tree.getModel().getRoot()) {
            setIcon(null);
            setText("");
        }

        return this;
    }
}
