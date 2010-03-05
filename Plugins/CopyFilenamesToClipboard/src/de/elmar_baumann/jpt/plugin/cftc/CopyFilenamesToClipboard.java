/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.plugin.cftc;

import de.elmar_baumann.jpt.plugin.Plugin;
import de.elmar_baumann.jpt.plugin.PluginEvent;
import de.elmar_baumann.lib.resource.Bundle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;

/**
 * Copies into the system clipboard names of files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-27
 */
public final class CopyFilenamesToClipboard extends Plugin implements Serializable {

    private static final           long       serialVersionUID           = 526527636923496736L;
    public static final            String     KEY_FILENAME_DELIMITER     = CopyFilenamesToClipboard.class.getName() + ".KeyDelimiter";
    public static final            String     DEFAULT_FILENAME_DELIMITER = "\n";
    private final                  CopyAction copyAction                 = new CopyAction();
    private                        String     fileNameDelimiter          = DEFAULT_FILENAME_DELIMITER;
    private static final transient Bundle     BUNDLE                     = new Bundle("de/elmar_baumann/jpt/plugin/cftc/Bundle");

    public CopyFilenamesToClipboard() {
    }

    @Override
    public String getName() {
        return BUNDLE.getString("CopyFilenamesToClipboard.Name");
    }

    @Override
    public String getDescription() {
        return BUNDLE.getString("CopyFilenamesToClipboard.Description");
    }

    @Override
    public JPanel getSettingsPanel() {
        return new SettingsPanel(getProperties());
    }

    @Override
    public String getHelpContentsPath() {
        return "/de/elmar_baumann/jpt/plugin/cftc/help/contents.xml";
    }

    @Override
    public String getFirstHelpPageName() {
        return "index.html";
    }

    private class CopyAction extends AbstractAction {

        private static final long serialVersionUID = 932072385600278529L;

        public CopyAction() {
            putValue(Action.NAME, getName());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            notifyPluginListeners(new PluginEvent(PluginEvent.Type.STARTED));
            setDelimiter();
            StringBuilder sb = new StringBuilder();
            int index = 0;
            for (File file : getFiles()) {
                sb.append((index++ == 0
                           ? ""
                           : fileNameDelimiter) + file.getAbsolutePath());
            }
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
            notifyFinished();
        }

        private void notifyFinished() {
            PluginEvent evt = new PluginEvent(PluginEvent.Type.FINISHED_SUCCESS);
            evt.setProcessedFiles(getFiles());
            notifyPluginListeners(evt);
        }

        private void setDelimiter() {
            Properties properties = getProperties();
            if (properties != null) {
                String delimiter = properties.getProperty(KEY_FILENAME_DELIMITER);
                if (delimiter != null) {
                    fileNameDelimiter = delimiter;
                }
            }
        }
    }

    @Override
    public List<? extends Action> getActions() {
        return Arrays.asList(copyAction);
    }
}
