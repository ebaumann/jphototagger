package org.jphototagger.program.controller.misc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.frames.AppFrame;

/**
 * Kontrolliert die Aktion: Informationen über die Anwendung sollen angezeigt
 * werden.
 *
 * @author Elmar Baumann
 */
public final class AboutJPhotoTaggerController implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent evt) {
        ImageIcon icon = AppLookAndFeel.getIcon("icon_logo.png");
        String key = "AboutJPhotoTaggerController.Info.About";
        String title = "JPhotoTagger";
        AppFrame parentComponent = GUI.getAppFrame();
        String message = Bundle.getString(AboutJPhotoTaggerController.class, key, AppInfo.APP_NAME, AppInfo.APP_VERSION);
        int messageType = JOptionPane.INFORMATION_MESSAGE;

        JOptionPane.showMessageDialog(parentComponent, message, title, messageType, icon);
    }
}
