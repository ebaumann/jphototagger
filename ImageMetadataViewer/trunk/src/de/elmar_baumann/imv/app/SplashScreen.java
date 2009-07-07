package de.elmar_baumann.imv.app;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * The application's splashscreen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public final class SplashScreen {

    private static final int MESSAGES_X = 10;

    /**
     * Prints a message onto the splashscreen.
     * 
     * @param message message
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

    private static void splashScreenDrawMessage(
            Graphics2D g, Font standardFont, String message) {
        g.setColor(Color.BLACK);
        g.setFont(standardFont);
        g.drawString(message, MESSAGES_X, 270);
    }

    private static void splashScreenDrawAppInfo(Graphics2D g, Font standardFont) {
        int standardFontSize = standardFont.getSize();
        int y = 210;
        Font fontBold = new Font(standardFont.getName(), Font.BOLD,
                (int) (standardFontSize * 1.5 + 0.5));
        g.setFont(fontBold);
        g.setColor(Color.BLACK);
        g.drawString(AppInfo.APP_NAME + " " + AppInfo.APP_VERSION, MESSAGES_X, y);  // NOI18N
        g.setFont(standardFont);
        g.setColor(Color.BLUE);
        g.drawString(AppInfo.APP_DESCRIPTION, MESSAGES_X, y + standardFontSize +
                10);
    }

    private SplashScreen() {
    }
}
