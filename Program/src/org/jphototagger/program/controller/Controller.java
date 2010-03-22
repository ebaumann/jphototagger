/*
 * @(#)Controller.java    Created on 2010-01-07
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

package org.jphototagger.program.controller;

import org.jphototagger.program.app.AppLogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.lang.reflect.Method;

/**
 * Controller base class.
 * <p>
 * Listens to all action- and key events of
 * {@link #listenToActionsOf(java.lang.Object[])} and
 * {@link #listenToKeyEventsOf(java.lang.Object[])}. If
 * {@link #myAction(java.awt.event.ActionEvent)} or
 * {@link #myKey(java.awt.event.KeyEvent)} returning true,
 * {@link #action(java.awt.event.ActionEvent)} or
 * {@link #action(java.awt.event.KeyEvent)} will be called.
 * <p>
 * If a controller is derived from a subclass of this controller it maybe
 * most effective that that controller registers itself it's actions and
 * the base class controller the key events.
 *
 * @author  Elmar Baumann
 */
public abstract class Controller implements ActionListener, KeyListener {

    /**
     * Returning, whether {@link #action(java.awt.event.KeyEvent)} shall be
     * called if a key was pressed.
     *
     * @param  evt key event
     * @return     true, if that key shall trigger an action
     */
    abstract protected boolean myKey(KeyEvent evt);

    /**
     * Returning, whether {@link #action(java.awt.event.ActionEvent)} shall be
     * called if an action occured.
     *
     * @param  evt action event
     * @return     true, if that source action shall trigger a controller action
     */
    abstract protected boolean myAction(ActionEvent evt);

    /**
     * Will be called if an action on an observed source occured and
     * {@link #myAction(java.awt.event.ActionEvent)} returned true.
     *
     * @param evt action event
     */
    abstract protected void action(ActionEvent evt);

    /**
     * Will be called if a key was pressed on an observed source occured and
     * {@link #myKey(java.awt.event.KeyEvent)} returned true.
     *
     * @param evt key event
     */
    abstract protected void action(KeyEvent evt);

    @Override
    public void keyPressed(KeyEvent evt) {
        if (myKey(evt)) {
            action(evt);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (myAction(evt)) {
            action(evt);
        }
    }

    protected Controller() {}

    /**
     * Adds all objects with action events to listen to.
     *
     * @param objects object with a method <code>addActionListener(ActionListener)</code>
     */
    protected void listenToActionsOf(Object... objects) {
        for (Object object : objects) {
            try {
                Method m = object.getClass().getMethod("addActionListener",
                               ActionListener.class);

                m.invoke(object, this);
            } catch (Exception ex) {
                AppLogger.logSevere(Controller.class, ex);
            }
        }
    }

    /**
     * Adds all objects with key events to listen to.
     *
     * @param objects object with a method <code>addKeyListener(KeyListener)</code>
     */
    protected void listenToKeyEventsOf(Object... objects) {
        for (Object object : objects) {
            try {
                Method m = object.getClass().getMethod("addKeyListener",
                               KeyListener.class);

                m.invoke(object, this);
            } catch (Exception ex) {
                AppLogger.logSevere(Controller.class, ex);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {

        // ignore
    }
}
