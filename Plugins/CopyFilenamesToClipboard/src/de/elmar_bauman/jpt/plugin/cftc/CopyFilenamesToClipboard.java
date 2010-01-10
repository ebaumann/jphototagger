/*
 * CopyFilenamesToClipboard - plugin for JPhotoTagger
 * Copyright (C) 2009 by Elmar Baumann<eb@elmar-baumann.de>
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
package de.elmar_bauman.jpt.plugin.cftc;

import de.elmar_baumann.jpt.plugin.Plugin;
import de.elmar_baumann.jpt.plugin.PluginListener.Event;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.JPanel;

/**
 * Copies into the system clipboard names of files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-27
 */
public final class CopyFilenamesToClipboard extends Plugin {

    public static final  String KEY_FILENAME_DELIMITER     = CopyFilenamesToClipboard.class.getName() + ".KeyDelimiter";
    public static final  String DEFAULT_FILENAME_DELIMITER = "\n";
    private static final long   serialVersionUID           = -7900546898112962080L;
    private              String fileNameDelimiter          = DEFAULT_FILENAME_DELIMITER;

    public CopyFilenamesToClipboard() {
    }

    public String getName() {
        return ResourceBundle.getBundle("de/elmar_bauman/jpt/plugin/cftc/Bundle").
                getString("CopyFilenamesToClipboard.Name");
    }

    public String getDescription() {
        return ResourceBundle.getBundle("de/elmar_bauman/jpt/plugin/cftc/Bundle").
                getString("CopyFilenamesToClipboard.Description");
    }

    @Override
    public JPanel getSettingsPanel() {
        return new SettingsPanel(getProperties());
    }

    @Override
    public String getHelpContentsPath() {
        return "/de/elmar_bauman/jpt/plugin/cftc/help/contents.xml";
    }

    public void actionPerformed(ActionEvent e) {
        setDelimiter();
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (File file : getFiles()) {
            sb.append((index++ == 0
                       ? ""
                       : fileNameDelimiter) + file.getAbsolutePath());
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
        notifyPluginListeners(Event.FINISHED_NO_ERRORS);
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
