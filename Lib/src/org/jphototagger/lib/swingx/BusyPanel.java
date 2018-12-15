package org.jphototagger.lib.swingx;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import org.jdesktop.swingx.JXBusyLabel;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.resources.UiFactory;

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
 *     private final BusyPanel busyPanel = new BusyPanel(UiFactory.dimension(200, 200));
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
public final class BusyPanel extends PanelExt {

    private static final long serialVersionUID = 1L;
    private final Dimension dimension;

    public BusyPanel() {
        this(UiFactory.dimension(16, 16));
    }

    /**
     *
     * @param dimension Default 16x16 pixels
     */
    public BusyPanel(Dimension dimension) {
        if (dimension == null) {
            throw new NullPointerException("dimension == null");
        }
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

    private void initComponents() {

        busyLabel = new JXBusyLabel(dimension);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        busyLabel.setBusy(true);
        add(busyLabel, new java.awt.GridBagConstraints());
    }
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
}
