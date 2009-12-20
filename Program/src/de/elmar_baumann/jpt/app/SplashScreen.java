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
package de.elmar_baumann.jpt.app;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * The application's splashscreen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-23
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
