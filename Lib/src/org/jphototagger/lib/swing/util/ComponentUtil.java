package org.jphototagger.lib.swing.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 * @author Elmar Baumann
 */
public final class ComponentUtil {

    /**
     * Invalidates, validates and repaint a component.
     *
     * @param component component
     */
    public static void forceRepaint(Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        component.invalidate();
        component.validate();
        component.repaint();
    }

    /**
     * Centers a window on the screen.
     *
     * @param window window to center
     */
    public static void centerScreen(Window window) {
        if (window == null) {
            throw new NullPointerException("window == null");
        }

        Dimension screenDimension = window.getToolkit().getScreenSize();
        Rectangle frameBounds = window.getBounds();

        window.setLocation((screenDimension.width - frameBounds.width) / 2,
                (screenDimension.height - frameBounds.height) / 2);
    }

    /**
     * Finds frames with an icon: Frames of {@code Frame#getFrames()} where
     * {@code Frame#getIconImage()} returns not null.
     *
     * @return frames with an icon or an empty list
     */
    public static List<Frame> findFramesWithIcons() {
        List<Frame> frames = new ArrayList<>();
        Frame[] allFrames = Frame.getFrames();

        for (Frame frame : allFrames) {
            if (frame.getIconImage() != null) {
                frames.add(frame);
            }
        }

        return frames;
    }

    /**
     * Returns the first found frame of {@code #findFramesWithIcons()}. <p> Especially for usage in a
     * <code>JOptionPane#show...Dialog()</code> instead of null. Then in the dialog frame an icon will be displayed that
     * is different to the Java "coffee cup" icon.
     *
     * @return frame or null
     */
    public static Frame findFrameWithIcon() {
        List<Frame> frames = findFramesWithIcons();

        return (frames.isEmpty())
                ? null
                : frames.get(0);
    }

    /**
     * Returns all elements of a specific class from a container.
     *
     * <em>Only elements of that class are detected, not sub- and supertyes!</em>
     *
     * @param <T> class type
     * @param container container
     * @param clazz class
     * @return found elements or empty list
     */
    public static <T> List<T> getAllOf(Container container, Class<T> clazz) {
        if (container == null) {
            throw new NullPointerException("container == null");
        }

        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        List<T> components = new ArrayList<>();

        addAllOf(container, clazz, components);

        return components;
    }

    @SuppressWarnings("unchecked")
    private static <T> void addAllOf(Container container, Class<T> clazz, List<T> all) {
        int count = container.getComponentCount();

        if (container.getClass().equals(clazz)) {
            all.add((T) container);
        }

        for (int i = 0; i < count; i++) {
            Component component = container.getComponent(i);

            if (component instanceof Container) {
                addAllOf((Container) component, clazz, all);    // Recursive
            } else if (component.getClass().equals(clazz)) {
                all.add((T) component);
            }
        }
    }

    public static <T> List<T> getAllOfExclude(Container container, Class<T> clazz, Collection<? extends Component> exclude) {
        if (container == null) {
            throw new NullPointerException("container == null");
        }
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }
        if (exclude == null) {
            throw new NullPointerException("exclude == null");
        }
        List<T> components = new ArrayList<>();
        addAllOfExclude(container, clazz, components, exclude);
        return components;
    }

    @SuppressWarnings("unchecked")
    private static <T> void addAllOfExclude(Container container, Class<T> clazz, List<T> all, Collection<? extends Component> exclude) {
        if (exclude.contains(container)) {
            return;
        }
        if (container.getClass().equals(clazz)) {
            all.add((T) container);
        }
        int count = container.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = container.getComponent(i);
            if (exclude.contains(component)) {
                continue;
            }
            if (component instanceof Container) {
                addAllOfExclude((Container) component, clazz, all, exclude);    // Recursive
            } else if (component.getClass().equals(clazz)) {
                all.add((T) component);
            }
        }
    }

    /**
     * Makes a window visible - if invisible - and brings it to front.
     *
     * @param window window
     */
    public static void show(Window window) {
        if (window == null) {
            throw new NullPointerException("window == null");
        }

        if (!window.isVisible()) {
            window.setVisible(true);
        }

        window.toFront();
    }

    public static Font createBoldFont(Font font) {
        if (font == null) {
            throw new NullPointerException("font == null");
        }
        String fontName = font.getName();
        int fontSize = font.getSize();
        return new Font(fontName, Font.BOLD, fontSize);
    }

    public static Frame findParentFrame(Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        Container parent = component.getParent();
        while (parent != null) {
            if ((parent instanceof Frame)) {
                return (Frame) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    public static void parentWindowToFront(Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        Container parent = component.getParent();
        while (parent != null) {
            if ((parent instanceof Window)) {
                Window parentWindow = (Window) parent;
                parentWindow.toFront();
                return;
            }
            parent = parent.getParent();
        }
    }

    /**
     * Disposes the Window on
     * {@link #actionPerformed(java.awt.event.ActionEvent)}.
     */
    public static final class DisposeWindowAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final Window window;

        public DisposeWindowAction(Window window) {
            this.window = Objects.requireNonNull(window, "window == null");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                window.dispose();
            }
        }
    }

    /**
     * Performs an action if the ESC key was pressed.
     *
     * @param container    container
     * @param escapeAction action to perform if ESC was pressed
     */
    public static void registerForEscape(Container container, Action escapeAction) {
        Objects.requireNonNull(container, "container == null");
        Objects.requireNonNull(escapeAction, "escapeAction == null");

        KeyStroke strokeEscape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        for (Component component : getContainerAndItsChildrenRecursive(container)) {
            if (component instanceof JComponent) {
                JComponent jComponent = (JComponent) component;
                jComponent.registerKeyboardAction(escapeAction, strokeEscape, JComponent.WHEN_IN_FOCUSED_WINDOW);
            }
        }
    }

    public static Collection<Component> getContainerAndItsChildrenRecursive(Container container) {
        Objects.requireNonNull(container, "container == null");
        Collection<Component> containerAndChildren = new ArrayList<>();
        addContainerAndItsChildrenRecursive(container, containerAndChildren);
        return containerAndChildren;
    }

    private static void addContainerAndItsChildrenRecursive(Container container, Collection<Component> containerAndChildren) {
        containerAndChildren.add(container);
        Component[] containerComponents = container.getComponents();
        for (Component containerComponent : containerComponents) {
            if (containerComponent instanceof Container) {
                addContainerAndItsChildrenRecursive((Container) containerComponent, containerAndChildren);
            } else {
                containerAndChildren.add(containerComponent);
            }
        }
    }

    public static void registerForSetInvisibleOnEscape(JDialog dialog) {
        Objects.requireNonNull(dialog, "dialog == null");

        KeyStroke keyStrokeEscape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        EscapeActionListener escapeActionListener = new EscapeActionListener(dialog);

        for (Component component : dialog.getComponents()) {
            if (component instanceof JComponent) {
                JComponent jComponent = (JComponent) component;
                jComponent.registerKeyboardAction(escapeActionListener, keyStrokeEscape, JComponent.WHEN_IN_FOCUSED_WINDOW);
            }
        }
    }

    private static final class EscapeActionListener implements ActionListener {

        private final JDialog dialog;

        private EscapeActionListener(JDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.setVisible(false);
        }
    }

    private ComponentUtil() {
    }
}
