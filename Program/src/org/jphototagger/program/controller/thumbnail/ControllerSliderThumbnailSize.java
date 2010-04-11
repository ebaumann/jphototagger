/*
 * @(#)ControllerSliderThumbnailSize.java    Created on 2008-10-12
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.event.listener.UserSettingsListener;
import org.jphototagger.program.event.UserSettingsEvent;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

/**
 * Controls the slider which changes the size of the thumbnails
 *
 * @author  Elmar Baumann
 */
public final class ControllerSliderThumbnailSize
        implements AWTEventListener, ChangeListener, ThumbnailsPanelListener,
                   UserSettingsListener {
    private final AppPanel        appPanel                  =
        GUI.INSTANCE.getAppPanel();
    private final ThumbnailsPanel tnPanel                   =
        appPanel.getPanelThumbnails();
    private final JSlider         slider                    =
        appPanel.getSliderThumbnailSize();
    private static final int      STEP_WIDTH                = 1;
    private static final int      LARGER_STEP_WIDTH         = 10;
    private static final int      MIN_MAGINFICATION_PERCENT = 10;
    private static final int      MAX_MAGINFICATION_PERCENT = 100;
    private static final String   KEY_SLIDER_VALUE          =
        "org.jphototagger.program.controller.thumbnail.ControllerSliderThumbnailSize."
        + "SliderValue";
    private int currentValue      = 100;
    private int maxThumbnailWidth =
        UserSettings.INSTANCE.getMaxThumbnailWidth();

    public ControllerSliderThumbnailSize() {
        initSlider();
        listen();
    }

    private void listen() {
        tnPanel.addThumbnailsPanelListener(this);
        slider.addChangeListener(this);
        UserSettings.INSTANCE.addUserSettingsListener(this);
        Toolkit.getDefaultToolkit().addAWTEventListener(this,
                AWTEvent.KEY_EVENT_MASK);
    }

    private void initSlider() {
        readProperties();
        slider.setMinimum(MIN_MAGINFICATION_PERCENT);
        slider.setMaximum(MAX_MAGINFICATION_PERCENT);
        slider.setMajorTickSpacing(STEP_WIDTH);
        slider.setMinorTickSpacing(STEP_WIDTH);
        slider.setValue(currentValue);
        setThumbnailWidth();
    }

    @Override
    public void stateChanged(ChangeEvent evt) {
        handleSliderMoved();
    }

    @Override
    public void eventDispatched(AWTEvent awtEvent) {
        KeyEvent keyEvent = (KeyEvent) awtEvent;

        if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
            keyPressed(keyEvent);
        }
    }

    private void keyPressed(KeyEvent evt) {
        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_PLUS)) {
            moveSlider(LARGER_STEP_WIDTH, true);
        } else if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_MINUS)) {
            moveSlider(LARGER_STEP_WIDTH, false);
        }
    }

    private void moveSlider(int stepWidth, boolean increase) {
        if (increase) {
            addToSliderValue(stepWidth);
        } else {
            addToSliderValue(-stepWidth);
        }
    }

    private void addToSliderValue(int increment) {
        int value    = slider.getValue();
        int newValue =
            Math.min(Math.max(value + increment, MIN_MAGINFICATION_PERCENT),
                     MAX_MAGINFICATION_PERCENT);

        slider.setValue(newValue);
    }

    @Override
    public void thumbnailsSelectionChanged() {

        // ignore
    }

    @Override
    public void thumbnailsChanged() {
        setThumbnailWidth();
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        if (evt.getType().equals(UserSettingsEvent.Type.MAX_THUMBNAIL_WIDTH)) {
            maxThumbnailWidth = UserSettings.INSTANCE.getMaxThumbnailWidth();
            setThumbnailWidth();
        }
    }

    private void handleSliderMoved() {
        int value = slider.getValue();

        // value % STEP_WIDTH == 0 is not necessary as long as STEP_WIDTH == 1
        if ( /* value % STEP_WIDTH == 0 && */value != currentValue) {
            currentValue = value;
            writeProperties();
            setThumbnailWidth();
        }
    }

    private void readProperties() {
        Integer value =
            UserSettings.INSTANCE.getSettings().getInt(KEY_SLIDER_VALUE);

        if (!value.equals(Integer.MIN_VALUE)) {
            currentValue = value;
        }
    }

    private void setThumbnailWidth() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int width = (int) ((double) maxThumbnailWidth
                                   * ((double) currentValue / 100.0));

                tnPanel.setThumbnailWidth(width);
            }
        });
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().set(currentValue, KEY_SLIDER_VALUE);
        UserSettings.INSTANCE.writeToFile();
    }
}
