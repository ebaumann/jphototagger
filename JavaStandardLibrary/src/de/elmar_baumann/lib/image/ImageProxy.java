package de.elmar_baumann.lib.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stellvertreter für ein Bild, malt dieses erst, nachdem es
 * fertig geladen ist. Vorher wird ein Rahmen gezeichnet.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/20
 */
public class ImageProxy implements Runnable {

    private String filename;
    private Component parent;
    private Image image;
    private int x;
    private int y;
    private int width;
    private int height;
    private MediaTracker tracker;
    private Thread imageCheck;

    /**
     * Konstruktor mit Bild, das aus einer Datei geladen wird.
     * Solange das Bild nicht geladen ist, wir an dessen Stelle
     * ein Rahmen gezeichnet.
     * 
     * @param parent   Elternkomponente
     * @param filename Dateiname
     * @param x        x-Koordinate linkes oberes Eck in Pixel
     * @param y        y-Koordinate linkes oberes Eck in Pixel
     * @param width    Breite für Proxy-Rahmen in Pixel
     * @param height   Höhe für Rahmen in Pixel
     */
    public ImageProxy(String filename, Component parent, int x, int y, int width,
        int height) {
        this.filename = filename;
        this.parent = parent;
        this.image = Toolkit.getDefaultToolkit().getImage(filename);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        init();
    }

    private void init() {
        tracker = new MediaTracker(parent);
        tracker.addImage(image, 0);
        imageCheck = new Thread(this);
        imageCheck.start();
        try {
            tracker.waitForID(0, 1);
        } catch (InterruptedException ex) {
            Logger.getLogger(ImageProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void paint(Graphics g) {
        if (tracker.checkID(0)) {
            height = image.getHeight(parent);
            width = image.getWidth(parent);

            g.setColor(Color.lightGray); //erase box
            g.fillRect(0, 0, width, height);
            g.drawImage(image, 0, 0, parent);
        } else {
            g.setColor(Color.black);
            g.drawRect(x, y, width - 2, height - 2);
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(100);
            while (!tracker.checkID(0)) {
                Thread.sleep(100);
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
