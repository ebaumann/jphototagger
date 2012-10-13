package org.jphototagger.program.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller base class.
 * <p>
 * Listens to all action- and key events of
 * {@code #listenToActionsOf(java.lang.Object[])} and
 * {@code #listenToKeyEventsOf(java.lang.Object[])}. If
 * {@code #myAction(java.awt.event.ActionEvent)} or
 * {@code #myKey(java.awt.event.KeyEvent)} returning true,
 * {@code #action(java.awt.event.ActionEvent)} or
 * {@code #action(java.awt.event.KeyEvent)} will be called.
 * <p>
 * If a controller is derived from a subclass of this controller it maybe
 * most effective that that controller registers itself it's actions and
 * the base class controller the key events.
 *
 * @author Elmar Baumann
 */
public abstract class Controller implements ActionListener, KeyListener {

    /**
     * Returning, whether {@code #action(java.awt.event.KeyEvent)} shall be
     * called if a key was pressed.
     *
     * @param  evt key event
     * @return     true, if that key shall trigger an action
     */
    abstract protected boolean myKey(KeyEvent evt);

    /**
     * Returning, whether {@code #action(java.awt.event.ActionEvent)} shall be
     * called if an action occured.
     *
     * @param  evt action event
     * @return     true, if that source action shall trigger a controller action
     */
    abstract protected boolean myAction(ActionEvent evt);

    /**
     * Will be called if an action on an observed source occured and
     * {@code #myAction(java.awt.event.ActionEvent)} returned true.
     *
     * @param evt action event
     */
    abstract protected void action(ActionEvent evt);

    /**
     * Will be called if a key was pressed on an observed source occured and
     * {@code #myKey(java.awt.event.KeyEvent)} returned true.
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

    protected Controller() {
    }

    /**
     * Adds all objects with action events to listen to.
     *
     * @param objects object with a method <code>addActionListener(ActionListener)</code>
     */
    protected void listenToActionsOf(Object... objects) {
        if (objects == null) {
            throw new NullPointerException("objects == null");
        }

        for (Object object : objects) {
            try {
                Method m = object.getClass().getMethod("addActionListener", ActionListener.class);

                m.invoke(object, this);
            } catch (Exception ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Adds all objects with key events to listen to.
     *
     * @param objects object with a method <code>addKeyListener(KeyListener)</code>
     */
    protected void listenToKeyEventsOf(Object... objects) {
        if (objects == null) {
            throw new NullPointerException("objects == null");
        }

        for (Object object : objects) {
            try {
                Method m = object.getClass().getMethod("addKeyListener", KeyListener.class);

                m.invoke(object, this);
            } catch (Exception ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent evt) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        // ignore
    }
}
