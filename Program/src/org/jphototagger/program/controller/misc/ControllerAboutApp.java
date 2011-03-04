package org.jphototagger.program.controller.misc;

import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Kontrolliert die Aktion: Informationen über die Anwendung sollen angezeigt
 * werden.
 *
 * @author Elmar Baumann
 */
public final class ControllerAboutApp implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent evt) {
        ImageIcon icon = AppLookAndFeel.getIcon("icon_logo.png");
        String key = "ControllerAboutApp.Info.About";
        String title = "JPhotoTagger";

        JOptionPane.showMessageDialog(GUI.getAppFrame(),
                                      JptBundle.INSTANCE.getString(key, AppInfo.APP_NAME, AppInfo.APP_VERSION), title,
                                      JOptionPane.INFORMATION_MESSAGE, icon);
    }
}
