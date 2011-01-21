package org.jphototagger.program.app;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * The application's splashscreen.
 *
 * @author Elmar Baumann
 */
public final class SplashScreen {
    private static final int   MESSAGE_X         = 10;
    private static final Color MESSAGE_COLOR     = Color.BLACK;
    private static final int   MESSAGE_FONT_SIZE = 12;
    private static final Font  MESSAGE_FONT      = new Font("Arial",
                                                       Font.PLAIN,
                                                       MESSAGE_FONT_SIZE);
    private static final Font MESSAGE_HEADER_FONT =
        new Font(MESSAGE_FONT.getName(), Font.BOLD,
                 (int) (MESSAGE_FONT_SIZE * 1.5 + 0.5));
    private static final Color SLOGAN_COLOR                  = Color.BLUE;
    private static final int   SLOGAN_PADDING_TO_MESSAGE     = 15;
    private static final int   PROGRESSBAR_HEIGHT            = 15;
    private static final int   PROGRESSBAR_PADDING_TO_TEXT   = 10;
    private static final int   PROGRESSBAR_PADDING_TO_BOTTOM = 20;
    private static final Color PROGRESSBAR_BORDER_COLOR      = new Color(225,
                                                                   225, 225);
    private static final Color          PROGRESSBAR_COLOR = new Color(234, 233,
                                                                254);
    private static final Color          BACKGROUND_COLOR  = new Color(250, 250,
                                                                250);
    private final java.awt.SplashScreen splash            =
        java.awt.SplashScreen.getSplashScreen();
    private Rectangle                splashBounds;
    private Graphics2D               graphics;
    private String                   message = "";
    private int                      progressValue;
    private volatile boolean         init;
    public static final SplashScreen INSTANCE = new SplashScreen();

    public synchronized void init() {
        if (init) {
            return;
        }

        init = true;

        if (splash == null) {
            return;
        }

        splashBounds = splash.getBounds();
        graphics     = splash.createGraphics();

        if (graphics == null) {
            return;
        }

        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawRect(0, 0, splashBounds.width - 1,
                          splashBounds.height - 1);
    }

    public void close() {
        if (splash != null) {
            splash.close();
        }
    }

    /**
     *
     * @param value progress value in percent, range 0 - 100
     */
    public void setProgress(int value) {
        assert init;

        assert (value >= 0) && (value <= 100) : value;

        if ((value >= 0) && (value <= 100)) {
            progressValue = value;
        }

        if (canDisplay()) {
            updateDisplay();
        }
    }

    public void removeMessage() {
        setMessage("");
    }

    public void setMessage(String message) {
        assert init;

        this.message = message;
        AppLogger.logInfo(getClass(), AppLogger.USE_STRING, message);

        if (canDisplay()) {
            updateDisplay();
        }
    }

    private boolean canDisplay() {
        return (splash != null) && (graphics != null);
    }

    private void updateDisplay() {
        updateMessage();
        updateProgressBar();
        updateAppInfo();
        splash.update();
    }

    private void updateProgressBar() {
        final int barWidth      = splashBounds.width - 2 * MESSAGE_X;
        final int progressWidth = barWidth * progressValue / 100 - 1;    // -1: Border
        final int x = MESSAGE_X;
        final int y = splashBounds.height - PROGRESSBAR_PADDING_TO_BOTTOM
                      - PROGRESSBAR_HEIGHT;

        graphics.setComposite(AlphaComposite.Clear);
        graphics.setPaintMode();
        graphics.setColor(PROGRESSBAR_BORDER_COLOR);
        graphics.drawRect(x, y, barWidth, PROGRESSBAR_HEIGHT);
        graphics.setColor(PROGRESSBAR_COLOR);
        graphics.fillRect(x + 1, y + 1, progressWidth, PROGRESSBAR_HEIGHT - 1);
    }

    private void updateMessage() {
        graphics.setFont(MESSAGE_FONT);
        graphics.setComposite(AlphaComposite.Clear);
        graphics.setPaintMode();
        clearMessage();
        drawMessage();
    }

    private void clearMessage() {
        final int messageHeight = getMessageHeight();

        graphics.setColor(BACKGROUND_COLOR);
        graphics.fillRect(MESSAGE_X, getMessageY() - messageHeight,
                          splashBounds.width - MESSAGE_X - 1,
                          messageHeight + 5);    // +5: descending
    }

    private int getMessageY() {
        return splashBounds.height - PROGRESSBAR_PADDING_TO_BOTTOM
               - PROGRESSBAR_HEIGHT - PROGRESSBAR_PADDING_TO_TEXT;
    }

    private int getMessageHeight() {
        final FontMetrics fm = graphics.getFontMetrics(MESSAGE_FONT);

        return fm.getHeight();
    }

    private void drawMessage() {
        graphics.setColor(MESSAGE_COLOR);
        graphics.setFont(MESSAGE_FONT);
        graphics.drawString(message, MESSAGE_X, getMessageY());
    }

    private void updateAppInfo() {
        final int messageHeight  = getMessageHeight();
        final int boldFontHeight =
            graphics.getFontMetrics(MESSAGE_HEADER_FONT).getHeight();
        final int y = getMessageY() - messageHeight - SLOGAN_PADDING_TO_MESSAGE
                      - boldFontHeight;

        graphics.setFont(MESSAGE_HEADER_FONT);
        graphics.setColor(MESSAGE_COLOR);
        graphics.drawString(AppInfo.APP_NAME + " " + AppInfo.APP_VERSION,
                            MESSAGE_X, y);
        graphics.setFont(MESSAGE_FONT);
        graphics.setColor(SLOGAN_COLOR);
        graphics.drawString(AppInfo.APP_DESCRIPTION, MESSAGE_X,
                            y + boldFontHeight);
    }

    private SplashScreen() {}
}
