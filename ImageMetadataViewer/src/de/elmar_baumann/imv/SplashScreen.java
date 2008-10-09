package de.elmar_baumann.imv;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Splashscreen der Anwendung.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class SplashScreen {

    private static final int messagesX = 10;

    /**
     * Gibt eine Nachricht auf dem Splashscreen aus.
     * 
     * @param message Nachricht
     */
    public static void setMessageToSplashScreen(String message) {
        java.awt.SplashScreen splash = java.awt.SplashScreen.getSplashScreen();
        if (splash != null) {
            Graphics2D g = splash.createGraphics();
            Font font = new Font("Arial", Font.PLAIN, 12);  // NOI18N
            g.setFont(font);
            g.setComposite(AlphaComposite.Clear);
            g.setPaintMode();
            splashScreenClearMessage(g);
            splashScreenDrawAppInfo(g, font);
            splashScreenDrawMessage(g, font, message);
            splash.update();
        }
    }

    private static void splashScreenClearMessage(Graphics2D g) {
        g.setColor(new Color(250, 250, 250));
        g.fillRect(1, 249, 488, 50);
    }

    private static void splashScreenDrawMessage(Graphics2D g, Font standardFont,
        String message) {
        g.setColor(Color.BLACK);
        g.setFont(standardFont);
        g.drawString(message, messagesX, 270);
    }

    private static void splashScreenDrawAppInfo(Graphics2D g, Font standardFont) {
        int standardFontSize = standardFont.getSize();
        int y = 210;
        Font fontBold = new Font(standardFont.getName(), Font.BOLD,
            (int) (standardFontSize * 1.5 + 0.5));
        g.setFont(fontBold);
        g.setColor(Color.BLACK);
        g.drawString(AppInfo.appName + " " + AppInfo.appVersion, messagesX, y);  // NOI18N
        g.setFont(standardFont);
        g.setColor(Color.BLUE);
        g.drawString(AppInfo.appDescription, messagesX, y + standardFontSize + 10);
    }
}
