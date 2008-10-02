package de.elmar_baumann.lib.componentutil;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Look and Feel der Anwendung.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/14
 */
public class LookAndFeelUtil {

    /**
     * Setzt das Look and Feel des Systems. Sollte als erste Codezeile in
     * main() aufgerufen werden, spätestens bevor das erste GUI-Element
     * erzeugt wird.
     */
    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(LookAndFeelUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
