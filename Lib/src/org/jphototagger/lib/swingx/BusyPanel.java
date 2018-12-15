package org.jphototagger.lib.swingx;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import org.jdesktop.swingx.JXBusyLabel;

/**
 * Draws a busy animation. Usage:
 * <ol>
 * <li>Create a field instance of this panel</li>
 * <li>Add the instance as glass pane to a root pane (will be overlayed with the animation)</li>
 * <li>Set visible before a lengthy process in a separate thread starts</li>
 * <li>Set invisible after the lengthy process ended</li>
 * </ol>
 * <pre>
 * public final class MyDialog extends JDialog {
 *     private final BusyPanel busyPanel = new BusyPanel(org.jphototagger.resources.UiFactory.dimension(200, 200));
 *     private final JXRootPane rootPane = new JXRootPane();
 *     public MyDialog {
 *         initComponents();
 *         rootPane.setGlassPane(busyPanel);
 *     }
 * ....
 *         busyPanel.setVisible(true);
 *         // Lengthy Backgroundprocess in e.g. SwingWorker starts
 *         // Lengthy Backgroundprocess in Thread ends
 *         busyPanel.setVisible(false); // e.g. in done() of the SwingWorker
 * ....
 * }
 * </pre>
 * @author Elmar Baumann
 */
public final class BusyPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final Dimension dimension;

    public BusyPanel() {
        this(org.jphototagger.resources.UiFactory.dimension(16, 16));
    }

    /**
     *
     * @param dimension Default 16x16 pixels
     */
    public BusyPanel(Dimension dimension) {
        if (dimension == null) {
            throw new NullPointerException("dimension == null");
        }
        org.jphototagger.resources.UiFactory.configure(this);
        this.dimension = dimension;
        initComponents();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        super.paint(g2);
        g2.dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        busyLabel = new JXBusyLabel(dimension);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        busyLabel.setBusy(true);
        add(busyLabel, new java.awt.GridBagConstraints());
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    // End of variables declaration//GEN-END:variables
}
