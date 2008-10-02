package de.elmar_baumann.imagemetadataviewer.factory;

/**
 * Factory mit Kenntnis über alle Factories. Erzeugt diese in der richtigen
 * Reihenfolge.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class MetaFactory {

    private static MetaFactory instance = new MetaFactory();

    public static MetaFactory getInstance() {
        return instance;
    }

    public void startController() {
        ControllerFactory.getInstance().startController();
    }

    public void stopController() {
        ControllerFactory.getInstance().stopController();
    }

    private MetaFactory() {
        createFactories();
    }

    private void createFactories() {
        ModelFactory.getInstance();
        ControllerFactory.getInstance();
        ActionListenerFactory.getInstance();
        MouseListenerFactory.getInstance();
        RendererFactory.getInstance();
    }
}
