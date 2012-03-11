package org.jphototagger.program.app.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;

/**
 * @author Elmar Baumann
 */
public class ProgressBarPanelArray extends javax.swing.JPanel implements ProgressBarPanelListener {

    private static final long serialVersionUID = 1L;
    private final List<ProgressBarPanel> inactiveProgressBarPanels = new ArrayList<ProgressBarPanel>();
    private final List<ProgressBarPanel> activeProgressBarPanels = new ArrayList<ProgressBarPanel>();
    private static final Icon DOWN_ARROW_ICON = IconUtil.getImageIcon(ProgressBarPanelArray.class, "arrow_down.png");
    private static final Icon UP_ARROW_ICON = IconUtil.getImageIcon(ProgressBarPanelArray.class, "arrow_up.png");
    private final ToggleVisibilityOfHiddenProgressBars toggleVisibilityOfHiddenProgressBars = new ToggleVisibilityOfHiddenProgressBars();

    public ProgressBarPanelArray() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        buttonToggleVisibilityOfHiddenProgressBars.addActionListener(toggleVisibilityOfHiddenProgressBars);
    }

    public synchronized ProgressHandle createHandle() {
        return addProgressBarPanel(new ProgressBarPanel());
    }

    public synchronized ProgressHandle createHandle(Cancelable cancelable) {
        return addProgressBarPanel(new ProgressBarPanel(cancelable));
    }

    private synchronized ProgressBarPanel addProgressBarPanel(ProgressBarPanel progressBarPanel) {
        progressBarPanel.addProgressBarPanelListener(this);
        progressBarPanel.addMouseListener(toggleVisibilityOfHiddenProgressBars);
        inactiveProgressBarPanels.add(progressBarPanel);
        return progressBarPanel;
    }

    private synchronized void showActiveProgressBarPanel(ProgressBarPanel progressBarPanel) {
        if (activeProgressBarPanels.isEmpty()) {
            add(progressBarPanel, getProgressBarPanelGbc());
            repaintThis();
        } else {
            panelHiddenProgressBars.add(progressBarPanel, getHiddenProgressBarPanelGbc());
            repaintHiddenProgressBars();
        }
        inactiveProgressBarPanels.remove(progressBarPanel);
        activeProgressBarPanels.add(progressBarPanel);
        setEnabledButtonToggleEnabled();
    }

    private synchronized void setEnabledButtonToggleEnabled() {
        buttonToggleVisibilityOfHiddenProgressBars.setEnabled(isMultipleTasks());
    }

    private GridBagConstraints getProgressBarPanelGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        return gbc;
    }

    private GridBagConstraints getHiddenProgressBarPanelGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        return gbc;
    }

    private synchronized void removeProgressBarPanel(ProgressBarPanel progressBarPanel) {
        if (isAncestorOf(progressBarPanel)) {
            setProgressBarPanelFromHidden(progressBarPanel);
        } else {
            panelHiddenProgressBars.remove(progressBarPanel);
            repaintHiddenProgressBars();
        }
        activeProgressBarPanels.remove(progressBarPanel);
        if (!isMultipleTasks() && dialogHiddenProgressBars.isVisible()) {
            toggleVisibilityOfHiddenProgressBars();
        }
        setEnabledButtonToggleEnabled();
    }

    private void repaintThis() {
        ComponentUtil.forceRepaint(this);
        ComponentUtil.forceRepaint(getParent());
    }

    private synchronized void setProgressBarPanelFromHidden(ProgressBarPanel progressBarPanelToRemove) {
        List<ProgressBarPanel> pbPanels = ComponentUtil.getAllOf(panelHiddenProgressBars, ProgressBarPanel.class);
        if (!pbPanels.isEmpty()) {
            remove(progressBarPanelToRemove);
            ProgressBarPanel pbPanel = pbPanels.get(0);
            panelHiddenProgressBars.remove(pbPanel);
            add(pbPanel, getProgressBarPanelGbc());
            ComponentUtil.forceRepaint(pbPanel);
            repaintHiddenProgressBars();
            repaintThis();
        }
    }

    private void repaintHiddenProgressBars() {
        ComponentUtil.forceRepaint(panelHiddenProgressBars);
        Dimension size = panelHiddenProgressBars.getPreferredSize();
        scrollPaneHiddenProgressBars.setPreferredSize(new Dimension(size.width + 2, size.height + 2));
        ComponentUtil.forceRepaint(dialogHiddenProgressBars);
        setProgressBarDialogLocation();
    }

    private synchronized void toggleVisibilityOfHiddenProgressBars() {
        boolean multipleTasks = isMultipleTasks();
        boolean hide = !multipleTasks || dialogHiddenProgressBars.isVisible();
        buttonToggleVisibilityOfHiddenProgressBars.setIcon(hide ? UP_ARROW_ICON : DOWN_ARROW_ICON);
        if (hide) {
            dialogHiddenProgressBars.setVisible(false);
            return;
        }
        setProgressBarDialogLocation();
        dialogHiddenProgressBars.setVisible(true);
    }

    private void setProgressBarDialogLocation() {
        dialogHiddenProgressBars.pack();
        Point locationOnScreen = getLocationOnScreen();
        int xRight = locationOnScreen.x + getWidth();
        int yTop = locationOnScreen.y;
        int dialogWidth = dialogHiddenProgressBars.getWidth();
        int dialogHeight = dialogHiddenProgressBars.getHeight();
        dialogHiddenProgressBars.setLocation(xRight - dialogWidth, yTop - dialogHeight);
    }

    public synchronized boolean hasActiveTasks() {
        return !activeProgressBarPanels.isEmpty();
    }

    public synchronized boolean hasInactiveTasks() {
        return !inactiveProgressBarPanels.isEmpty();
    }

    private synchronized boolean isMultipleTasks() {
        return activeProgressBarPanels.size() > 1;
    }

    @Override
    public void progressStarted(ProgressBarPanel source) {
        showActiveProgressBarPanel(source);
    }

    @Override
    public void progressEnded(ProgressBarPanel source) {
        removeProgressBarPanel(source);
    }

    private class ToggleVisibilityOfHiddenProgressBars extends MouseAdapter implements ActionListener {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            toggleVisibilityOfHiddenProgressBars();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            toggleVisibilityOfHiddenProgressBars();
        }
    };

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        dialogHiddenProgressBars = new javax.swing.JDialog();
        scrollPaneHiddenProgressBars = new javax.swing.JScrollPane();
        panelHiddenProgressBars = new javax.swing.JPanel();
        buttonToggleVisibilityOfHiddenProgressBars = new javax.swing.JButton();

        dialogHiddenProgressBars.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/app/ui/Bundle"); // NOI18N
        dialogHiddenProgressBars.setTitle(bundle.getString("ProgressBarPanelArray.dialogHiddenProgressBars.title")); // NOI18N
        dialogHiddenProgressBars.setAlwaysOnTop(true);
        dialogHiddenProgressBars.setIconImages(AppLookAndFeel.getAppIcons());
        dialogHiddenProgressBars.setUndecorated(true);
        dialogHiddenProgressBars.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dialogHiddenProgressBarsWindowClosing(evt);
            }
        });
        dialogHiddenProgressBars.getContentPane().setLayout(new java.awt.GridBagLayout());

        scrollPaneHiddenProgressBars.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(195, 195, 195)));
        scrollPaneHiddenProgressBars.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelHiddenProgressBars.setLayout(new java.awt.GridBagLayout());
        scrollPaneHiddenProgressBars.setViewportView(panelHiddenProgressBars);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        dialogHiddenProgressBars.getContentPane().add(scrollPaneHiddenProgressBars, gridBagConstraints);

        setLayout(new java.awt.GridBagLayout());

        buttonToggleVisibilityOfHiddenProgressBars.setIcon(UP_ARROW_ICON);
        buttonToggleVisibilityOfHiddenProgressBars.setToolTipText(bundle.getString("ProgressBarPanelArray.buttonToggleVisibilityOfHiddenProgressBars.toolTipText")); // NOI18N
        buttonToggleVisibilityOfHiddenProgressBars.setBorder(null);
        buttonToggleVisibilityOfHiddenProgressBars.setEnabled(false);
        buttonToggleVisibilityOfHiddenProgressBars.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonToggleVisibilityOfHiddenProgressBars.setPreferredSize(new java.awt.Dimension(18, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(buttonToggleVisibilityOfHiddenProgressBars, gridBagConstraints);
    }//GEN-END:initComponents

    private void dialogHiddenProgressBarsWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dialogHiddenProgressBarsWindowClosing
        toggleVisibilityOfHiddenProgressBars();
    }//GEN-LAST:event_dialogHiddenProgressBarsWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonToggleVisibilityOfHiddenProgressBars;
    private javax.swing.JDialog dialogHiddenProgressBars;
    private javax.swing.JPanel panelHiddenProgressBars;
    private javax.swing.JScrollPane scrollPaneHiddenProgressBars;
    // End of variables declaration//GEN-END:variables
}
