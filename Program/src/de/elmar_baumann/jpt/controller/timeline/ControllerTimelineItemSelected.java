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
package de.elmar_baumann.jpt.controller.timeline;

import de.elmar_baumann.jpt.data.Timeline;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.listener.RefreshListener;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-12
 */
public final class ControllerTimelineItemSelected implements
        TreeSelectionListener, RefreshListener {

    private final AppPanel        appPanel        = GUI.INSTANCE.getAppPanel();
    private final JTree           treeTimeline    = appPanel.getTreeTimeline();
    private final ThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();

    public ControllerTimelineItemSelected() {
        listen();
    }

    private void listen() {
        treeTimeline.addTreeSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.TIMELINE);
    }

    @Override
    public void refresh() {
        if (treeTimeline.getSelectionCount() == 1) {
            setFilesOfTreePathToThumbnailsPanel(treeTimeline.getSelectionPath());
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath()) {
            setFilesOfTreePathToThumbnailsPanel(e.getNewLeadSelectionPath());
        }
    }

    private void setFilesOfTreePathToThumbnailsPanel(final TreePath path) {
        if (path != null) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    final Object lastPathComponent = path.getLastPathComponent();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            setFilesOfPossibleNodeToThumbnailsPanel(lastPathComponent);
                        }
                    });
                }
            });
            thread.setName("Timeline item selected @ " + getClass().getSimpleName());
            thread.start();
        }
    }

    private void setFilesOfPossibleNodeToThumbnailsPanel(Object lastPathComponent) {
        if (lastPathComponent instanceof DefaultMutableTreeNode) {
            setFilesOfNodeToThumbnailsPanel((DefaultMutableTreeNode) lastPathComponent);
        }
    }

    private void setFilesOfNodeToThumbnailsPanel(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (node.equals(Timeline.getUnknownNode())) {
            setTitle();
            thumbnailsPanel.setFiles(DatabaseImageFiles.INSTANCE.getFilesOfUnknownExifDate(), Content.TIMELINE);
        } else if (userObject instanceof Calendar) {
            Calendar               cal    = (Calendar) userObject;
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (parent != null) {
                boolean isYear = parent.equals(node.getRoot());
                boolean isMonth = !isYear && node.getChildCount() > 0;
                int year = cal.get(Calendar.YEAR);
                int month = isYear
                            ? -1
                            : cal.get(Calendar.MONTH) + 1;
                int day = isMonth
                          ? -1
                          : cal.get(Calendar.DAY_OF_MONTH);
                setTitle(isYear, year, isMonth, month, cal);
                thumbnailsPanel.setFiles(
                        DatabaseImageFiles.INSTANCE.getFilesOf(year, month, day), Content.TIMELINE);
            }
        }
    }

    private void setTitle() {
        GUI.INSTANCE.getAppFrame().setTitle(
                Bundle.getString("AppFrame.Title.Timline.Unknown"));
    }

    private void setTitle(boolean isYear, int year, boolean isMonth, int month, Calendar cal) {
        NumberFormat yf    = new DecimalFormat("####");
        DateFormat   mf    = new SimpleDateFormat("MMMMM");
        DateFormat   df    = DateFormat.getDateInstance(DateFormat.LONG);
        Object       fDate =   isYear
                             ? yf.format(year)
                             : isMonth
                             ? mf.format(month) + " " + yf.format(year)
                             : df.format(cal.getTime());

        GUI.INSTANCE.getAppFrame().setTitle(Bundle.getString("AppFrame.Title.Timeline.Date", fDate));
    }
}
