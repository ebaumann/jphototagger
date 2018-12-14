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
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public class ProgressBarPanelArray extends javax.swing.JPanel implements ProgressBarPanelListener {

    private static final long serialVersionUID = 1L;
    private final List<ProgressBarPanel> activeProgressBarPanels = new ArrayList<>();
    private static final Icon DOWN_ARROW_ICON = Icons.getIcon("icon_arrow_down_gray.png");
    private static final Icon UP_ARROW_ICON = Icons.getIcon("icon_arrow_up_gray.png");
    private final ToggleVisibilityOfHiddenProgressBars toggleVisibilityOfHiddenProgressBars = new ToggleVisibilityOfHiddenProgressBars();
    private ProgressBarPanel visibleProgressBarPanel;

    public ProgressBarPanelArray() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        visibleProgressBarPanel = initProgressBarPanel;
        buttonToggleVisibilityOfHiddenProgressBars.addActionListener(toggleVisibilityOfHiddenProgressBars);
    }

    public ProgressHandle createHandle() {
        return addProgressBarPanel(new ProgressBarPanel());
    }

    public ProgressHandle createHandle(Cancelable cancelable) {
        return addProgressBarPanel(new ProgressBarPanel(cancelable));
    }

    private ProgressBarPanel addProgressBarPanel(ProgressBarPanel progressBarPanel) {
        progressBarPanel.addProgressBarPanelListener(this);
        progressBarPanel.addMouseListener(toggleVisibilityOfHiddenProgressBars);
        return progressBarPanel;
    }

    private void showActiveProgressBarPanel(ProgressBarPanel progressBarPanel) {
        if (activeProgressBarPanels.isEmpty()) {
            remove(visibleProgressBarPanel);
            add(progressBarPanel, getProgressBarPanelGbc());
            visibleProgressBarPanel = progressBarPanel;
            repaintThis();
        } else {
            panelHiddenProgressBars.add(progressBarPanel, getHiddenProgressBarPanelGbc());
            repaintHiddenProgressBars();
        }
        activeProgressBarPanels.add(progressBarPanel);
        setEnabledButtonToggleEnabled();
    }

    private void setEnabledButtonToggleEnabled() {
        buttonToggleVisibilityOfHiddenProgressBars.setEnabled(isMultipleTasks());
    }

    private GridBagConstraints getProgressBarPanelGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
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

    private void removeProgressBarPanel(ProgressBarPanel progressBarPanel) {
        if (isAncestorOf(progressBarPanel)) {
            setProgressBarPanelFromHidden();
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

    private void setProgressBarPanelFromHidden() {
        List<ProgressBarPanel> pbPanels = ComponentUtil.getAllOf(panelHiddenProgressBars, ProgressBarPanel.class);
        if (!pbPanels.isEmpty()) {
            remove(visibleProgressBarPanel);
            ProgressBarPanel pbPanel = pbPanels.get(0);
            panelHiddenProgressBars.remove(pbPanel);
            visibleProgressBarPanel = pbPanel;
            add(pbPanel, getProgressBarPanelGbc());
            ComponentUtil.forceRepaint(pbPanel);
            repaintHiddenProgressBars();
            repaintThis();
        }
    }

    private void repaintHiddenProgressBars() {
        ComponentUtil.forceRepaint(panelHiddenProgressBars);
        Dimension size = panelHiddenProgressBars.getPreferredSize();
        scrollPaneHiddenProgressBars.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(size.width + 2, size.height + 2));
        ComponentUtil.forceRepaint(dialogHiddenProgressBars);
        setProgressBarDialogLocation();
    }

    private void toggleVisibilityOfHiddenProgressBars() {
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

    private  boolean isMultipleTasks() {
        return activeProgressBarPanels.size() > 1;
    }

    @Override
    public void progressStarted(final ProgressBarPanel source) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                showActiveProgressBarPanel(source);
            }
        });
    }

    @Override
    public void progressEnded(final ProgressBarPanel source) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                removeProgressBarPanel(source);
            }
        });
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
        initProgressBarPanel = new org.jphototagger.program.app.ui.ProgressBarPanel();
        buttonToggleVisibilityOfHiddenProgressBars = new javax.swing.JButton();

        dialogHiddenProgressBars.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dialogHiddenProgressBars.setTitle(Bundle.getString(getClass(), "ProgressBarPanelArray.dialogHiddenProgressBars.title")); // NOI18N
        dialogHiddenProgressBars.setAlwaysOnTop(true);
        dialogHiddenProgressBars.setIconImages(org.jphototagger.resources.Icons.getAppIcons());
        dialogHiddenProgressBars.setUndecorated(true);
        dialogHiddenProgressBars.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(initProgressBarPanel, gridBagConstraints);

        buttonToggleVisibilityOfHiddenProgressBars.setIcon(UP_ARROW_ICON);
        buttonToggleVisibilityOfHiddenProgressBars.setToolTipText(Bundle.getString(getClass(), "ProgressBarPanelArray.buttonToggleVisibilityOfHiddenProgressBars.toolTipText")); // NOI18N
        buttonToggleVisibilityOfHiddenProgressBars.setBorder(null);
        buttonToggleVisibilityOfHiddenProgressBars.setEnabled(false);
        buttonToggleVisibilityOfHiddenProgressBars.setMargin(org.jphototagger.resources.UiFactory.insets(0, 0, 0, 0));
        buttonToggleVisibilityOfHiddenProgressBars.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(18, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        add(buttonToggleVisibilityOfHiddenProgressBars, gridBagConstraints);
    }//GEN-END:initComponents

    private void dialogHiddenProgressBarsWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dialogHiddenProgressBarsWindowClosing
        toggleVisibilityOfHiddenProgressBars();
    }//GEN-LAST:event_dialogHiddenProgressBarsWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonToggleVisibilityOfHiddenProgressBars;
    private javax.swing.JDialog dialogHiddenProgressBars;
    private org.jphototagger.program.app.ui.ProgressBarPanel initProgressBarPanel;
    private javax.swing.JPanel panelHiddenProgressBars;
    private javax.swing.JScrollPane scrollPaneHiddenProgressBars;
    // End of variables declaration//GEN-END:variables
}
